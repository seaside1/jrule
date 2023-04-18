/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleChannelExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemChangeExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemReceivedCommandExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemReceivedUpdateExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRulePreconditionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleThingExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleTimeTimerExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleTimedCronExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleTimedExecutionContext;
import org.openhab.automation.jrule.internal.engine.timer.JRuleTimerExecutor;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.internal.module.JRuleModuleEntry;
import org.openhab.automation.jrule.internal.module.JRuleRuleProvider;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleCondition;
import org.openhab.automation.jrule.rules.JRuleDebounce;
import org.openhab.automation.jrule.rules.JRuleDelayed;
import org.openhab.automation.jrule.rules.JRuleLogName;
import org.openhab.automation.jrule.rules.JRuleMemberOf;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRulePrecondition;
import org.openhab.automation.jrule.rules.JRuleTag;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRuleWhenThingTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenTimeTrigger;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.things.JRuleThingStatus;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.events.ItemEvent;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.scheduler.CronScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * The {@link JRuleEngine}
 *
 * @author Joseph (Seaside) Hagberg - Initial Contribution
 * @author Robert Delbrück - Refactoring
 */
public class JRuleEngine implements PropertyChangeListener {
    public static final String MDC_KEY_TIMER = "timer";
    private static final String[] EMPTY_LOG_TAGS = new String[0];
    private static final int AWAIT_TERMINATION_THREAD_SECONDS = 2;
    private List<JRuleExecutionContext> contextList = new CopyOnWriteArrayList<>();
    private JRuleTimerExecutor timerExecutor = new JRuleTimerExecutor(this);
    public static final String MDC_KEY_RULE = "rule";
    protected ThreadPoolExecutor ruleExecutorService;
    protected JRuleConfig config;
    private final Logger logger = LoggerFactory.getLogger(JRuleEngine.class);
    protected ItemRegistry itemRegistry;
    protected JRuleLoadingStatistics ruleLoadingStatistics;
    private static volatile JRuleEngine instance;

    private JRuleRuleProvider ruleProvider;

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

    public void add(JRule jRule, boolean enableRule) {
        logDebug("Adding rule: {}, enabled: {}", jRule, enableRule);
        ruleLoadingStatistics.addRuleClass();
        Arrays.stream(jRule.getClass().getDeclaredMethods()).filter(method -> !method.getName().startsWith("lambda$"))
                .filter(method -> method.getDeclaringClass().equals(jRule.getClass())) // Skip inherited methods
                .forEach(method -> this.add(method, jRule, enableRule));
    }

    private void add(Method method, JRule jRule, boolean enableRule) {
        logDebug("Adding rule method: {}", method.getName());

        if (!method.isAnnotationPresent(JRuleName.class)) {
            logDebug("Skipping method {} on class {} since JRuleName annotation is missing", method.getName(),
                    jRule.getClass().getName());
            return;
        }

        // Check if method is public, else execution will fail at runtime
        boolean isPublic = (method.getModifiers() & Modifier.PUBLIC) != 0;
        if (!isPublic) {
            logWarn("Skipping non-public method {} on class {}", method.getName(), jRule.getClass().getName());
            return;
        }
        // Check if method is has none or a single parameter
        if (method.getParameterCount() > 1) {
            logWarn("Skipping method {} on class {}. Rule methods should have none or a single parameter",
                    method.getName(), jRule.getClass().getName());
            return;
        }

        final String logName = Optional.ofNullable(method.getDeclaredAnnotation(JRuleLogName.class))
                .map(JRuleLogName::value).orElse(method.getDeclaredAnnotation(JRuleName.class).value());

        JRuleModuleEntry ruleModuleEntry = new JRuleModuleEntry(jRule, method,
                method.getDeclaredAnnotation(JRuleName.class).value());

        List<JRulePreconditionContext> jRulePreconditionContexts = Arrays
                .stream(method.getAnnotationsByType(JRulePrecondition.class)).map(jRulePrecondition -> {
                    JRuleCondition jRuleCondition = jRulePrecondition.condition();
                    return new JRulePreconditionContext(jRulePrecondition.item(),
                            Optional.of(jRuleCondition.lt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(jRuleCondition.lte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(jRuleCondition.gt()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(jRuleCondition.gte()).filter(aDouble -> aDouble != Double.MIN_VALUE),
                            Optional.of(jRuleCondition.eq()).filter(StringUtils::isNotEmpty),
                            Optional.of(jRuleCondition.neq()).filter(StringUtils::isNotEmpty));
                }).collect(Collectors.toList());

        final String[] loggingTags = Optional.ofNullable(method.getDeclaredAnnotation(JRuleTag.class))
                .map(JRuleTag::value).orElse(EMPTY_LOG_TAGS);
        ruleModuleEntry.addTags(loggingTags);

        Duration timedLock = Optional.ofNullable(method.getDeclaredAnnotation(JRuleDebounce.class))
                .filter(jRuleDebounce -> jRuleDebounce.value() > 0)
                .map(jRuleDebounce -> Duration.of(jRuleDebounce.value(), jRuleDebounce.unit())).orElse(null);

        Duration delayed = Optional.ofNullable(method.getAnnotation(JRuleDelayed.class))
                .filter(jRuleDelayed -> jRuleDelayed.value() > 0)
                .map(jRuleDebounce -> Duration.of(jRuleDebounce.value(), jRuleDebounce.unit())).orElse(null);

        ruleLoadingStatistics.addRuleMethod();
        AtomicBoolean addedToContext = new AtomicBoolean(false);

        Arrays.stream(method.getAnnotationsByType(JRuleWhenItemReceivedUpdate.class)).forEach(jRuleWhen -> {
            JRuleCondition jRuleCondition = jRuleWhen.condition();
            JRuleItemReceivedUpdateExecutionContext context = new JRuleItemReceivedUpdateExecutionContext(jRule,
                    logName, loggingTags, method, jRuleWhen.item(), jRuleWhen.memberOf(),
                    Optional.of(new JRuleItemExecutionContext.JRuleConditionContext(jRuleCondition)),
                    jRulePreconditionContexts, Optional.of(jRuleWhen.state()).filter(StringUtils::isNotEmpty),
                    timedLock, delayed);
            addToContext(context, enableRule);
            ruleLoadingStatistics.addItemStateTrigger();
            ruleModuleEntry.addJRuleWhenItemReceivedUpdate(context);
            addedToContext.set(true);
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenItemReceivedCommand.class)).forEach(jRuleWhen -> {
            JRuleCondition jRuleCondition = jRuleWhen.condition();
            JRuleItemReceivedCommandExecutionContext context = new JRuleItemReceivedCommandExecutionContext(jRule,
                    logName, loggingTags, method, jRuleWhen.item(), jRuleWhen.memberOf(),
                    Optional.of(new JRuleItemExecutionContext.JRuleConditionContext(jRuleCondition)),
                    jRulePreconditionContexts, Optional.of(jRuleWhen.command()).filter(StringUtils::isNotEmpty),
                    timedLock, delayed);
            addToContext(context, enableRule);
            ruleLoadingStatistics.addItemStateTrigger();
            ruleModuleEntry.addJRuleWhenItemReceivedCommand(context);
            addedToContext.set(true);
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenItemChange.class)).forEach(jRuleWhen -> {
            JRuleCondition jRuleCondition = jRuleWhen.condition();
            JRuleCondition jRulePreviousCondition = jRuleWhen.previousCondition();
            JRuleItemChangeExecutionContext context = new JRuleItemChangeExecutionContext(jRule, logName, loggingTags,
                    method, jRuleWhen.item(), jRuleWhen.memberOf(),
                    Optional.of(new JRuleItemExecutionContext.JRuleConditionContext(jRuleCondition)),
                    Optional.of(new JRuleItemExecutionContext.JRuleConditionContext(jRulePreviousCondition)),
                    jRulePreconditionContexts, Optional.of(jRuleWhen.from()).filter(StringUtils::isNotEmpty),
                    Optional.of(jRuleWhen.to()).filter(StringUtils::isNotEmpty), timedLock, delayed);
            addToContext(context, enableRule);
            ruleLoadingStatistics.addItemStateTrigger();
            ruleModuleEntry.addJRuleWhenItemChange(context);
            addedToContext.set(true);
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenChannelTrigger.class)).forEach(jRuleWhen -> {
            JRuleChannelExecutionContext context = new JRuleChannelExecutionContext(jRule, logName, loggingTags, method,
                    jRulePreconditionContexts, jRuleWhen.channel(),
                    Optional.of(jRuleWhen.event()).filter(StringUtils::isNotEmpty), timedLock, delayed);
            addToContext(context, enableRule);
            ruleLoadingStatistics.addChannelTrigger();
            ruleModuleEntry.addJRuleWhenChannelTrigger(context);
            addedToContext.set(true);
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenCronTrigger.class)).forEach(jRuleWhen -> {
            JRuleTimedCronExecutionContext context = new JRuleTimedCronExecutionContext(jRule, logName, loggingTags,
                    method, jRulePreconditionContexts, jRuleWhen.cron());
            addToContext(context, enableRule);
            ruleLoadingStatistics.addTimedTrigger();
            ruleModuleEntry.addJRuleWhenCronTrigger(context);
            addedToContext.set(true);
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenTimeTrigger.class)).forEach(jRuleWhen -> {
            JRuleTimeTimerExecutionContext context = new JRuleTimeTimerExecutionContext(jRule, logName, loggingTags,
                    method, jRulePreconditionContexts, Optional.of(jRuleWhen.hours()).filter(i -> i != -1),
                    Optional.of(jRuleWhen.minutes()).filter(i -> i != -1),
                    Optional.of(jRuleWhen.seconds()).filter(i -> i != -1));
            addToContext(context, enableRule);
            ruleLoadingStatistics.addTimedTrigger();
            ruleModuleEntry.addJRuleWhenTimeTrigger(context);
            addedToContext.set(true);
        });

        Arrays.stream(method.getAnnotationsByType(JRuleWhenThingTrigger.class)).forEach(jRuleWhen -> {
            ruleLoadingStatistics.addThingTrigger();
            JRuleThingExecutionContext context = new JRuleThingExecutionContext(jRule, logName, loggingTags, method,
                    Optional.of(jRuleWhen.thing()).filter(StringUtils::isNotEmpty).filter(s -> !s.equals("*")),
                    Optional.of(jRuleWhen.from()).filter(s -> s != JRuleThingStatus.THING_UNKNOWN),
                    Optional.of(jRuleWhen.to()).filter(s -> s != JRuleThingStatus.THING_UNKNOWN),
                    jRulePreconditionContexts, timedLock, delayed);
            addToContext(context, enableRule);
            ruleLoadingStatistics.addThingTrigger();
            ruleModuleEntry.addJRuleWhenThingTrigger(context);
            addedToContext.set(true);
        });

        // Check if any rule triggers are present
        if (!addedToContext.get()) {
            logWarn("Skipping rule method {} on class {} with no JRuleWhenXXX annotation triggers", method.getName(),
                    jRule.getClass().getName());
        }

        ruleProvider.add(ruleModuleEntry);
    }

    private boolean addToContext(JRuleExecutionContext context, boolean enableRule) {
        logDebug("add to context: {}", context);
        context.setEnabled(enableRule);
        if (context instanceof JRuleTimedExecutionContext) {
            timerExecutor.add(context);
        } else {
            contextList.add(context);
        }

        return true;
    }

    public void fire(AbstractEvent event) {
        JRuleItemExecutionContext.JRuleAdditionalItemCheckData additionalCheckData = getAdditionalCheckData(event);

        List<JRuleExecutionContext> matchingExecutionContexts = contextList.stream()
                .filter(context -> context.match(event, additionalCheckData)).filter(this::matchPrecondition)
                .filter(distinctByKey(p -> p.getMethod().getName())).collect(Collectors.toList());
        matchingExecutionContexts.forEach(context -> invokeRule(context, context.createJRuleEvent(event)));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private JRuleItemExecutionContext.JRuleAdditionalItemCheckData getAdditionalCheckData(AbstractEvent event) {
        return Optional.ofNullable(event instanceof ItemEvent ? ((ItemEvent) event).getItemName() : null).map(s -> {
            try {
                return itemRegistry.getItem(s);
            } catch (ItemNotFoundException e) {
                throw new IllegalStateException("this can never occur", e);
            }
        }).map(item -> new JRuleItemExecutionContext.JRuleAdditionalItemCheckData(item.getType().equals(GroupItem.TYPE),
                item.getGroupNames()))
                .orElse(new JRuleItemExecutionContext.JRuleAdditionalItemCheckData(false, List.of()));
    }

    private Item getItem(String name) {
        try {
            return itemRegistry.getItem(name);
        } catch (ItemNotFoundException e) {
            throw new JRuleRuntimeException(String.format("cannot find item: %s", name), e);
        }
    }

    public boolean matchPrecondition(JRuleExecutionContext jRuleExecutionContext) {
        return jRuleExecutionContext.getPreconditionContextList().stream().allMatch(context -> {
            final Item item;
            try {
                item = itemRegistry.getItem(context.getItem());
            } catch (ItemNotFoundException e) {
                throw new JRuleItemNotFoundException("Cannot find item for precondition", e);
            }
            final String state = item.getState().toString();
            if (context.getEq().isPresent() && context.getEq().filter(state::equals).isEmpty()) {
                logDebug("precondition mismatch: {} = {}", state, context.getEq().get());
                return false;
            }
            if (context.getNeq().isPresent() && context.getNeq().filter(ref -> !state.equals(ref)).isEmpty()) {
                logDebug("precondition mismatch: {} != {}", state, context.getNeq().get());
                return false;
            }
            if (context.getLt().isPresent()
                    && context.getLt().filter(ref -> QuantityType.valueOf(state).doubleValue() < ref).isEmpty()) {
                logDebug("precondition mismatch: {} < {}", state, context.getLt().get());
                return false;
            }
            if (context.getLte().isPresent()
                    && context.getLte().filter(ref -> QuantityType.valueOf(state).doubleValue() <= ref).isEmpty()) {
                logDebug("precondition mismatch: {} <= {}", state, context.getLte().get());
                return false;
            }
            if (context.getGt().isPresent()
                    && context.getGt().filter(ref -> QuantityType.valueOf(state).doubleValue() > ref).isEmpty()) {
                logDebug("precondition mismatch: {} > {}", state, context.getGt().get());
                return false;
            }
            if (context.getGte().isPresent()
                    && context.getGte().filter(ref -> QuantityType.valueOf(state).doubleValue() >= ref).isEmpty()) {
                logDebug("precondition mismatch: {} >= {}", state, context.getGte().get());
                return false;
            }

            logDebug("precondition match: {} matches {}", state, context);
            return true;
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_ITEM_EVENT)
                || evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_CHANNEL_EVENT)
                || evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_THING_STATUS_EVENT)) {
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
        ruleProvider.reset();
    }

    public boolean watchingForItem(String itemName) {
        if (itemRegistry == null) {
            // JRuleEngine not completely initialized
            return false;
        }
        List<String> parentGroups = Optional.of(itemName).map(this::getItem).map(Item::getGroupNames).orElse(List.of());

        boolean b = this.contextList.stream().filter(context -> context instanceof JRuleItemExecutionContext)
                .map(context -> ((JRuleItemExecutionContext) context))
                .anyMatch(context -> (context.getItemName().equals(itemName)
                        && context.getMemberOf() == JRuleMemberOf.None)
                        || (parentGroups.contains(context.getItemName())
                                && context.getMemberOf() != JRuleMemberOf.None));
        logDebug("watching for item: '{}'? -> {}", itemName, b);
        return b;
    }

    public boolean watchingForChannel(String channel) {
        boolean b = this.contextList.stream().filter(context -> context instanceof JRuleChannelExecutionContext)
                .map(context -> ((JRuleChannelExecutionContext) context))
                .anyMatch(context -> context.getChannel().equals(channel));
        logDebug("watching for channel: '{}'? -> {}", channel, b);
        return b;
    }

    public boolean watchingForThing(String thing) {
        boolean b = this.contextList.stream().filter(context -> context instanceof JRuleThingExecutionContext)
                .map(context -> ((JRuleThingExecutionContext) context))
                .anyMatch(context -> context.getThing().map(s -> s.equals(thing)).orElse(true));
        logDebug("watching for thing: '{}'? -> {}", thing, b);
        return b;
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
        if (context.isEnabled()) {
            if (config.isExecutorsEnabled()) {
                ruleExecutorService.submit(() -> invokeDelayed(context, event,
                        (jRuleExecutionContext, jRuleEvent) -> invokeRuleInternal(context, event)));
            } else {
                invokeDelayed(context, event,
                        (jRuleExecutionContext, jRuleEvent) -> invokeRuleInternal(context, event));
            }
        } else {
            JRuleLog.debug(logger, context.getLogName(), "Not invoking rule because context {} is disabled", context);

        }
    }

    private void invokeRuleInternal(JRuleExecutionContext context, JRuleEvent event) {
        Duration timedLock = context.getTimedLock();
        if (timedLock != null) {
            if (!JRuleTimerHandler.get().getTimedLock(
                    context.getMethod().getDeclaringClass().getName() + "#" + context.getMethod().getName(),
                    timedLock)) {
                JRuleLog.debug(logger, context.getLogName(),
                        "Not invoking rule because method has an active debounce lock (context={})", context);
                return;
            }
        }

        JRuleLog.debug(logger, context.getLogName(), "Invoking rule for context: {}", context);

        final JRule rule = context.getRule();
        final Method method = context.getMethod();

        try {
            JRule.JRULE_EXECUTION_CONTEXT.set(context);
            JRuleLog.debug(logger, context.getMethod().getName(), "setting mdc tags: {}", context.getLoggingTags());
            MDC.put(MDC_KEY_RULE, context.getMethod().getName());
            Arrays.stream(context.getLoggingTags()).forEach(s -> MDC.put(s, s));
            if (context.hasEventParameterPresent()) {
                method.invoke(rule, event);
            } else {
                method.invoke(rule);
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            JRuleLog.error(logger, context.getMethod().getName(), "Error calling rule method: {}",
                    ExceptionUtils.getStackTrace(e));
        } catch (InvocationTargetException e) {
            JRuleLog.error(logger, context.getMethod().getName(), "Error in rule: {}",
                    ExceptionUtils.getStackTrace(e.getCause()));
        } finally {
            Arrays.stream(context.getLoggingTags()).forEach(MDC::remove);
            MDC.remove(MDC_KEY_RULE);
            JRule.JRULE_EXECUTION_CONTEXT.remove();
        }
    }

    private void invokeDelayed(JRuleExecutionContext context, JRuleEvent event,
            BiConsumer<JRuleExecutionContext, JRuleEvent> ruleInvoker) {
        if (context.getDelayed() != null) {
            JRuleTimerHandler.get().createTimer(null, context.getDelayed(), () -> ruleInvoker.accept(context, event),
                    context);
        } else {
            ruleInvoker.accept(context, event);
        }
    }

    public void setRuleProvider(JRuleRuleProvider ruleProvider) {
        this.ruleProvider = ruleProvider;
    }
}
