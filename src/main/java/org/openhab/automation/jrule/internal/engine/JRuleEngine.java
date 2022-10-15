/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.internal.engine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.context.JRuleChannelExecutionContext;
import org.openhab.automation.jrule.internal.engine.context.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.engine.context.JRuleItemChangeExecutionContext;
import org.openhab.automation.jrule.internal.engine.context.JRuleItemExecutionContext;
import org.openhab.automation.jrule.internal.engine.context.JRuleItemReceivedCommandExecutionContext;
import org.openhab.automation.jrule.internal.engine.context.JRuleItemReceivedUpdateExecutionContext;
import org.openhab.automation.jrule.internal.engine.context.JRulePreconditionContext;
import org.openhab.automation.jrule.internal.engine.context.JRuleTimedCronExecutionContext;
import org.openhab.automation.jrule.internal.engine.context.JRuleTimeTimerExecutionContext;
import org.openhab.automation.jrule.internal.engine.timer.TimerExecutor;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.rules.Condition;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleLogName;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRulePrecondition;
import org.openhab.automation.jrule.rules.JRuleTag;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRuleWhenTimeTrigger;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.scheduler.CronScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * The {@link JRuleEngine}
 *
 * @author Robert Delbrück
 */
public class JRuleEngine implements PropertyChangeListener {
    private static final String[] EMPTY_LOG_TAGS = new String[0];
    private static final int AWAIT_TERMINATION_THREAD_SECONDS = 2;
    private List<JRuleExecutionContext> contextList = new ArrayList<>();
    private TimerExecutor timerExecutor = new TimerExecutor(this);
    private static final String MDC_KEY_RULE = "rule";
    protected ThreadPoolExecutor ruleExecutorService;
    protected JRuleConfig config;
    private final Logger logger = LoggerFactory.getLogger(JRuleEngine.class);
    protected ItemRegistry itemRegistry;
    protected JRuleLoadingStatistics ruleLoadingStatistics;

    private static volatile JRuleEngine instance;

    public static JRuleEngine get() {
        if (instance == null) {
            synchronized (JRuleEngine.class) {
                if (instance == null) {
                    instance = new JRuleEngine();
                }
            }
        }
        return instance;
    }

    private JRuleEngine() {
        this.ruleLoadingStatistics = new JRuleLoadingStatistics(null);
    }

    public void add(JRule jRule) {
        logDebug("Adding rule: {}", jRule);
        ruleLoadingStatistics.addRuleClass();
        for (Method method : jRule.getClass().getDeclaredMethods()) {
            this.add(method, jRule);
        }
    }

    private void add(Method method, JRule jRule) {
        logDebug("Adding rule method: {}", method.getName());
        if (!method.isAnnotationPresent(JRuleName.class)) {
            logError("Rule method ignored since JRuleName annotation is missing: {}", method.getName());
            return;
        }
        // Check if method is public, else execution will fail at runtime
        boolean isPublic = (method.getModifiers() & Modifier.PUBLIC) != 0;
        if (!isPublic) {
            logError("Rule method ignored since method isn't public: {}", method.getName());
            return;
        }

        final String logName = Optional.ofNullable(method.getDeclaredAnnotation(JRuleLogName.class))
                .map(JRuleLogName::value).orElse(method.getDeclaredAnnotation(JRuleName.class).value());

        List<JRulePreconditionContext> jRulePreconditionContexts = Arrays
                .stream(method.getAnnotationsByType(JRulePrecondition.class)).map(jRulePrecondition -> {
                    Condition condition = jRulePrecondition.condition();
                    return new JRulePreconditionContext(jRulePrecondition.item(),
                            Optional.of(condition.lt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(condition.lte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(condition.gt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(condition.gte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(condition.eq()).filter(StringUtils::isNotEmpty),
                            Optional.of(condition.neq()).filter(StringUtils::isNotEmpty));
                }).collect(Collectors.toList());

        final String[] loggingTags = Optional.ofNullable(method.getDeclaredAnnotation(JRuleTag.class))
                .map(JRuleTag::value).orElse(EMPTY_LOG_TAGS);

        ruleLoadingStatistics.addRuleMethod();

        Arrays.stream(method.getAnnotationsByType(JRuleWhenItemReceivedUpdate.class)).forEach(jRuleWhen -> {
            Condition condition = jRuleWhen.condition();
            contextList.add(new JRuleItemReceivedUpdateExecutionContext(jRule, logName, loggingTags, jRuleWhen.item(),
                    method, Optional.of(condition.lt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.lte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.gt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.gte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.eq()).filter(StringUtils::isNotEmpty),
                    Optional.of(condition.neq()).filter(StringUtils::isNotEmpty), jRulePreconditionContexts,
                    Optional.of(jRuleWhen.to()).filter(StringUtils::isNotEmpty)));
            ruleLoadingStatistics.addItemStateTrigger();
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenItemReceivedCommand.class)).forEach(jRuleWhen -> {
            Condition condition = jRuleWhen.condition();
            contextList.add(new JRuleItemReceivedCommandExecutionContext(jRule, logName, loggingTags, jRuleWhen.item(),
                    method, Optional.of(condition.lt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.lte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.gt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.gte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.eq()).filter(StringUtils::isNotEmpty),
                    Optional.of(condition.neq()).filter(StringUtils::isNotEmpty), jRulePreconditionContexts,
                    Optional.of(jRuleWhen.to()).filter(StringUtils::isNotEmpty)));
            ruleLoadingStatistics.addItemStateTrigger();
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenItemChange.class)).forEach(jRuleWhen -> {
            Condition condition = jRuleWhen.condition();
            contextList.add(new JRuleItemChangeExecutionContext(jRule, logName, loggingTags, jRuleWhen.item(), method,
                    Optional.of(condition.lt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.lte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.gt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.gte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                    Optional.of(condition.eq()).filter(StringUtils::isNotEmpty),
                    Optional.of(condition.neq()).filter(StringUtils::isNotEmpty), jRulePreconditionContexts,
                    Optional.of(jRuleWhen.from()).filter(StringUtils::isNotEmpty),
                    Optional.of(jRuleWhen.to()).filter(StringUtils::isNotEmpty)));
            ruleLoadingStatistics.addItemStateTrigger();
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenChannelTrigger.class)).forEach(jRuleWhen -> {
            contextList.add(
                    new JRuleChannelExecutionContext(jRule, logName, loggingTags, method, jRulePreconditionContexts,
                            jRuleWhen.channel(), Optional.of(jRuleWhen.event()).filter(StringUtils::isNotEmpty)));
            ruleLoadingStatistics.addChannelTrigger();
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenCronTrigger.class)).forEach(jRuleWhen -> {
            timerExecutor.add(new JRuleTimedCronExecutionContext(jRule, logName, loggingTags, method,
                    jRulePreconditionContexts, jRuleWhen.cron()));
            ruleLoadingStatistics.addTimedTrigger();
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenTimeTrigger.class)).forEach(jRuleWhen -> {
            timerExecutor.add(new JRuleTimeTimerExecutionContext(jRule, logName, loggingTags, method,
                    jRulePreconditionContexts, Optional.of(jRuleWhen.hours()).filter(i -> i != -1),
                    Optional.of(jRuleWhen.minutes()).filter(i -> i != -1),
                    Optional.of(jRuleWhen.seconds()).filter(i -> i != -1)));
            ruleLoadingStatistics.addTimedTrigger();
        });
    }

    public void fire(AbstractEvent event) {
        contextList.stream().filter(context -> context.match(event)).filter(this::matchPrecondition)
                .forEach(context -> invokeRule(context, context.createJRuleEvent(event)));
    }

    private boolean matchPrecondition(JRuleExecutionContext jRuleExecutionContext) {
        return jRuleExecutionContext.getPreconditionContextList().stream().allMatch(context -> {
            final Item item;
            try {
                item = itemRegistry.getItem(context.getItem());
            } catch (ItemNotFoundException e) {
                throw new JRuleItemNotFoundException("Cannot find item for precondition", e);
            }
            final String state = item.getState().toString();
            if (context.getEq().isPresent() && context.getEq().filter(state::equals).isEmpty()) {
                return false;
            }
            if (context.getNeq().isPresent() && context.getNeq().filter(ref -> !state.equals(ref)).isEmpty()) {
                return false;
            }
            if (context.getLt().isPresent()
                    && context.getLt().filter(ref -> ref < QuantityType.valueOf(state).doubleValue()).isEmpty()) {
                return false;
            }
            if (context.getLte().isPresent()
                    && context.getLte().filter(ref -> ref <= QuantityType.valueOf(state).doubleValue()).isEmpty()) {
                return false;
            }
            if (context.getGt().isPresent()
                    && context.getGt().filter(ref -> ref > QuantityType.valueOf(state).doubleValue()).isEmpty()) {
                return false;
            }
            if (context.getGte().isPresent()
                    && context.getGte().filter(ref -> ref >= QuantityType.valueOf(state).doubleValue()).isEmpty()) {
                return false;
            }
            return true;
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_ITEM_EVENT)
                || evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_CHANNEL_EVENT)) {
            fire((AbstractEvent) evt.getNewValue());
        }
    }

    public void dispose() {
        if (config.isExecutorsEnabled()) {
            ruleExecutorService.shutdownNow();
            try {
                ruleExecutorService.awaitTermination(AWAIT_TERMINATION_THREAD_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logWarn("Not all rules ran to completion before rule engine shutdown", e);
            }
        }
    }

    public synchronized void reset() {
        contextList.clear();
        timerExecutor.clear();

        ruleLoadingStatistics = new JRuleLoadingStatistics(ruleLoadingStatistics);
    }

    public boolean watchingForItem(String itemName) {
        return this.contextList.stream().map(context -> ((JRuleItemExecutionContext) context))
                .anyMatch(context -> context.getItemName().equals(itemName));
    }

    public boolean watchingForChannel(String channel) {
        return this.contextList.stream().map(context -> ((JRuleChannelExecutionContext) context))
                .anyMatch(context -> context.getChannel().equals(channel));
    }

    public void setCronScheduler(CronScheduler cronScheduler) {
        this.timerExecutor.setCronScheduler(cronScheduler);
    }

    public JRuleLoadingStatistics getRuleLoadingStatistics() {
        return this.ruleLoadingStatistics;
    }

    protected void logInfo(String message, Object... parameters) {
        JRuleLog.info(logger, JRuleEngine.class.getSimpleName(), message, parameters);
    }

    protected void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, JRuleEngine.class.getSimpleName(), message, parameters);
    }

    protected void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, JRuleEngine.class.getSimpleName(), message, parameters);
    }

    protected void logError(String message, Object... parameters) {
        JRuleLog.error(logger, JRuleEngine.class.getSimpleName(), message, parameters);
    }

    public void setConfig(@NonNull JRuleConfig config) {
        this.config = config;
    }

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void initialize() {
        if (config.isExecutorsEnabled()) {
            logInfo("Initializing Java Rule Engine with Separate Thread Executors min: {} max: {}",
                    config.getMinExecutors(), config.getMaxExecutors());
            final ThreadFactory ruleExecutorThreadFactory = new ThreadFactory() {
                private final AtomicLong threadIndex = new AtomicLong(0);

                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName("JRule-Executor-" + threadIndex.getAndIncrement());
                    return thread;
                }
            };

            // Keep unused threads for 2 minutes before scaling back
            ruleExecutorService = new ThreadPoolExecutor(config.getMinExecutors(), config.getMaxExecutors(),
                    config.getKeepAliveExecutors(), TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
                    ruleExecutorThreadFactory);
        } else {
            logInfo("Initializing Java Rule Engine with Single Thread Execution");
        }
    }

    public void invokeRule(JRuleExecutionContext context, JRuleEvent event) {
        if (config.isExecutorsEnabled()) {
            ruleExecutorService.submit(() -> invokeRuleInternal(context, event));
        } else {
            invokeRuleInternal(context, event);
        }
    }

    private void invokeRuleInternal(JRuleExecutionContext context, JRuleEvent event) {
        JRuleLog.debug(logger, context.getLogName(), "Invoking rule for context: {}", context);

        final JRule rule = context.getJrule();
        final Method method = context.getMethod();
        rule.setRuleLogName(context.getLogName());
        try {
            JRuleLog.debug(logger, context.getMethod().getName(), "setting mdc tags: {}", context.getLoggingTags());
            MDC.put(MDC_KEY_RULE, context.getMethod().getName());
            Arrays.stream(context.getLoggingTags()).forEach(s -> MDC.put(s, s));
            if (context.hasEventParameterPresent()) {
                method.invoke(rule, event);
            } else {
                method.invoke(rule);
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            JRuleLog.error(logger, context.getMethod().getName(), "Error {}", e);
        } catch (InvocationTargetException e) {
            Throwable ex = e.getCause() != null ? e.getCause() : null;
            JRuleLog.error(logger, context.getMethod().getName(), "Error message", ex);
            JRuleLog.error(logger, context.getMethod().getName(), "Error Stacktrace: {}",
                    ExceptionUtils.getStackTrace(ex));
        } finally {
            Arrays.stream(context.getLoggingTags()).forEach(MDC::remove);
            MDC.remove(MDC_KEY_RULE);
        }
    }
}
