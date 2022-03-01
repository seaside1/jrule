/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.cron.JRuleCronExpression;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.items.JRuleItemType;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleLogName;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleTrigger;
import org.openhab.automation.jrule.rules.JRuleWhen;
import org.openhab.core.common.ThreadPoolManager;
import org.openhab.core.events.Event;
import org.openhab.core.items.events.GroupItemStateChangedEvent;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemEvent;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.openhab.core.thing.events.ChannelTriggeredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleConstants} class defines common constants, which are
 * used across the Java Rule automation.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleEngine implements PropertyChangeListener {

    private static final String RECEIVED_COMMAND = "received command";
    private static final String RECEIVED_COMMAND_APPEND = RECEIVED_COMMAND + " ";

    private static final String CHANGED_FROM_TO_PATTERN = "Changed from %1$s to %2$s";
    private static final String CHANGED_FROM = "Changed from ";
    private static final String CHANGED_TO = "Changed to ";
    private static final String CHANGED = "Changed";

    private static final String RECEIVED_UPDATE = "received update";
    private static final String RECEIVED_UPDATE_APPEND = RECEIVED_UPDATE + " ";

    private static final String COMMAND = "/command";

    private static final String STATE = "/state";

    private static final String STATE_CHANGED = "/statechanged";

    private static final String ITEM_TYPE = "TYPE";
    private static final String LOG_NAME_ENGINE = "JRuleEngine";

    private static volatile JRuleEngine instance;

    private JRuleConfig config;

    private final Map<String, List<JRule>> itemToRules = new HashMap<>();

    private final Map<String, List<JRuleExecutionContext>> itemToExecutionContexts = new HashMap<>();

    private final Map<String, List<JRuleExecutionContext>> channelToExecutionContexts = new HashMap<>();

    private final Set<String> itemNames = new HashSet<>();
    private final Logger logger = LoggerFactory.getLogger(JRuleEngine.class);
    private final Set<CompletableFuture<Void>> timers = new HashSet<>();
    protected final ScheduledExecutorService scheduler = ThreadPoolManager
            .getScheduledPool(ThreadPoolManager.THREAD_POOL_NAME_COMMON);

    private JRuleEngine() {
    }

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

    public Set<String> getItemNames() {
        return itemNames;
    }

    public Set<String> getChannelNames() {
        return channelToExecutionContexts.keySet();
    }

    public synchronized void reset() {
        itemNames.clear();
        itemToExecutionContexts.clear();
        channelToExecutionContexts.clear();
        itemToRules.clear();
        clearTimers();
    }

    private synchronized void clearTimers() {
        timers.forEach(timer -> timer.cancel(true));
        timers.clear();
    }

    private void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, LOG_NAME_ENGINE, message, parameters);
    }

    private void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, LOG_NAME_ENGINE, message, parameters);
    }

    private void logError(String message, Object... parameters) {
        JRuleLog.error(logger, LOG_NAME_ENGINE, message, parameters);
    }

    public void add(JRule jRule) {
        logDebug("Adding rule: {}", jRule);
        Class<?> clazz = jRule.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            logDebug("Adding rule method: {}", method.getName());
            if (!method.isAnnotationPresent(JRuleName.class)) {
                logDebug("Rule method ignored since JRuleName annotation is missing: {}", method.getName());
                continue;
            }

            if (method.getAnnotationsByType(JRuleWhen.class).length == 0) {
                logWarn("Rule ignored since JWhens annotation is missing");
                logWarn("Rule JWhen present: {}", method.isAnnotationPresent(JRuleWhen.class));
                final JRuleWhen[] jRuleWhens = method.getAnnotationsByType(JRuleWhen.class);
                logDebug("Got jrule whens size: {}", jRuleWhens.length);
                continue;
            }
            final JRuleName jRuleName = method.getDeclaredAnnotation(JRuleName.class);
            final JRuleLogName jRuleLogName = method.isAnnotationPresent(JRuleLogName.class)
                    ? method.getDeclaredAnnotation(JRuleLogName.class)
                    : null;

            final JRuleWhen[] jRuleWhens = method.getAnnotationsByType(JRuleWhen.class);
            logDebug("Got jrule whens size: {}", jRuleWhens.length);
            final Parameter[] parameters = method.getParameters();
            boolean jRuleEventPresent = Arrays.stream(parameters)
                    .anyMatch(param -> (param.getType().equals(JRuleEvent.class)));

            String logName = (jRuleLogName != null && !jRuleLogName.value().isEmpty()) ? jRuleLogName.value()
                    : jRuleName.value();

            // TODO: Do validation on syntax in when annotations
            // Loop for other ORs
            for (JRuleWhen jRuleWhen : jRuleWhens) {
                JRuleLog.debug(logger, logName, "Processing jRule when: {}", jRuleWhen);
                if (!jRuleWhen.item().isEmpty()) {
                    // JRuleWhen for an item

                    String itemPackage = config.getGeneratedItemPackage();
                    String prefix = config.getGeneratedItemPrefix();
                    String itemClass = String.format("%s.%s%s", itemPackage, prefix, jRuleWhen.item());

                    JRuleLog.debug(logger, logName, "Got item class: {}", itemClass);
                    JRuleLog.info(logger, logName, "Validating JRule: {} trigger: {} ", jRuleName.value(),
                            jRuleWhen.trigger());
                    addExecutionContext(jRule, logName, itemClass, jRuleName.value(), jRuleWhen.trigger(),
                            jRuleWhen.from(), jRuleWhen.to(), jRuleWhen.update(), jRuleWhen.item(), method,
                            jRuleEventPresent, getDoubleFromAnnotation(jRuleWhen.lt()),
                            getDoubleFromAnnotation(jRuleWhen.lte()), getDoubleFromAnnotation(jRuleWhen.gt()),
                            getDoubleFromAnnotation(jRuleWhen.gte()), getDoubleFromAnnotation(jRuleWhen.eq()));
                    itemNames.add(jRuleWhen.item());
                } else if (jRuleWhen.hours() != -1 || jRuleWhen.minutes() != -1 || jRuleWhen.seconds() != -1
                        || !jRuleWhen.cron().isEmpty()) {
                    // JRuleWhen for a time trigger
                    addTimedExecution(jRule, logName, jRuleName.value(), jRuleWhen, method, jRuleEventPresent);
                } else if (!jRuleWhen.channel().isEmpty()) {
                    // JRuleWhen for a channel
                    addChannelExecutionContext(jRule, logName, jRuleWhen.channel(), jRuleName.value(), method,
                            jRuleEventPresent);
                }
            }
        }
    }

    private Double getDoubleFromAnnotation(double d) {
        if (d == Double.MIN_VALUE) {
            return null;
        }
        return Double.valueOf(d);
    }

    private CompletableFuture<Void> createTimer(String logName, String cronExpressionStr) {
        try {
            final JRuleCronExpression cronExpression = new JRuleCronExpression(cronExpressionStr);
            final ZonedDateTime nextTimeAfter = cronExpression.nextTimeAfter(ZonedDateTime.now());
            Date futureTime = Date.from(nextTimeAfter.toInstant());
            return createTimer(logName, futureTime);
        } catch (IllegalArgumentException x) {
            JRuleLog.error(logger, logName, "Failed to parse cron expression for cron: {} message: {}",
                    cronExpressionStr, x.getMessage());
            return null;
        }
    }

    private CompletableFuture<Void> createTimer(String logName, int hours, int minutes, int seconds) {
        Calendar calFuture = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        calFuture.set(Calendar.HOUR_OF_DAY, hours == -1 ? 0 : hours);
        calFuture.set(Calendar.MINUTE, minutes == -1 ? 0 : minutes);
        calFuture.set(Calendar.SECOND, seconds == -1 ? 0 : seconds);
        calFuture.set(Calendar.MILLISECOND, 0);
        calFuture.set(Calendar.HOUR_OF_DAY, hours);
        if (calFuture.before(now)) {
            if (hours != -1) {
                calFuture.add(Calendar.DAY_OF_MONTH, 1);
            } else if (minutes != -1) {
                calFuture.add(Calendar.HOUR, 1);
            } else if (seconds != -1) {
                calFuture.add(Calendar.MINUTE, 1);
            }
        }
        return createTimer(logName, calFuture.getTime());
    }

    private CompletableFuture<Void> createTimer(String logName, Date date) {
        long initialDelay = new Date(date.getTime() - System.currentTimeMillis()).getTime();
        JRuleLog.debug(logger, logName, "Schedule cron: {} initialDelay: {}", date, initialDelay);
        Executor delayedExecutor = CompletableFuture.delayedExecutor(initialDelay, TimeUnit.MILLISECONDS, scheduler);
        return CompletableFuture.supplyAsync(() -> null, delayedExecutor);
    }

    private synchronized void addTimedExecution(JRule jRule, String logName, String jRuleName, JRuleWhen jRuleWhen,
            Method method, boolean jRuleEventPresent) {
        CompletableFuture<Void> future = (!jRuleWhen.cron().isEmpty()) ? createTimer(logName, jRuleWhen.cron())
                : createTimer(logName, jRuleWhen.hours(), jRuleWhen.minutes(), jRuleWhen.seconds());
        timers.add(future);
        JRuleLog.info(logger, logName, "Scheduling timer for rule: {} hours: {} minutes: {} seconds: {} cron: {}",
                jRuleWhen.hours(), jRuleWhen.minutes(), jRuleWhen.seconds(), jRuleWhen.cron());
        JRuleExecutionContext executionContext = new JRuleExecutionContext(jRule, logName, method, jRuleName,
                jRuleEventPresent);
        Consumer<Void> consumer = t -> {
            try {
                invokeRule(executionContext, jRuleEventPresent ? new JRuleEvent("") : null);
            } finally {
                timers.remove(future);
            }
        };
        future.thenAccept(consumer).thenAccept(s -> {
            JRuleLog.info(logger, logName, "Timer has finished");
            addTimedExecution(jRule, logName, jRuleName, jRuleWhen, method, jRuleEventPresent);
        });
    }

    private void addExecutionContext(JRule jRule, String logName, String itemClass, String ruleName, String trigger,
            String from, String to, String update, String itemName, Method method, boolean eventParameterPresent,
            Double lt, Double lte, Double gt, Double gte, Double eq) {
        List<JRuleExecutionContext> contextList = itemToExecutionContexts.computeIfAbsent(itemName,
                k -> new ArrayList<>());
        final JRuleExecutionContext context = new JRuleExecutionContext(jRule, logName, trigger, from, to, update,
                ruleName, itemClass, itemName, method, eventParameterPresent, lt, lte, gt, gte, eq);
        JRuleLog.debug(logger, logName, "ItemContextList add context: {}", context);
        contextList.add(context);
    }

    private void addChannelExecutionContext(JRule jRule, String logName, String channel, String ruleName, Method method,
            boolean eventParameterPresent) {
        List<JRuleExecutionContext> contextList = channelToExecutionContexts.computeIfAbsent(channel,
                k -> new ArrayList<>());
        final JRuleExecutionContext context = new JRuleExecutionContext(jRule, logName, null, null, null, null,
                ruleName, null, null, method, eventParameterPresent, null, null, null, null, null);
        JRuleLog.debug(logger, logName, "ChannelContextList add context: {}", context);
        contextList.add(context);
    }

    private boolean validateRule(String ruleName, String item, JRuleTrigger trigger) {
        Class<?> cls = null;
        try {
            cls = Class.forName(item);
        } catch (ClassNotFoundException e) {
            JRuleLog.warn(logger, ruleName, "Failed to validate could not find class for item: {}", item);
            return false;
        }
        JRuleItemType type = null;
        try {
            final Field declaredField = cls.getDeclaredField(ITEM_TYPE);
            type = (JRuleItemType) (declaredField.get(null));
        } catch (NoSuchFieldException e) {
            JRuleLog.warn(logger, ruleName, "Failed to validate, missing Item type: {}", item);
            return false;
        } catch (SecurityException e) {
            JRuleLog.warn(logger, ruleName, "Failed to validate, security exception item: {}", item, e);
            return false;
        } catch (IllegalArgumentException e) {
            JRuleLog.warn(logger, ruleName, "Failed to validate item: {}", item, e);
            return false;
        } catch (IllegalAccessException e) {
            JRuleLog.warn(logger, ruleName, "Failed to validate, exception for item: {}", item, e);
            return false;
        }

        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_ITEM_EVENT)) {
            logDebug("Property change item event! : {}", ((Event) evt.getNewValue()).getTopic());
            handleEventUpdate((Event) evt.getNewValue());

        } else if (evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_CHANNEL_EVENT)) {
            logDebug("Channel event! : {}", ((Event) evt.getNewValue()).getTopic());
            handleChannelEvent((ChannelTriggeredEvent) evt.getNewValue());
        }
    }

    private void handleChannelEvent(ChannelTriggeredEvent channelEvent) {
        List<JRuleExecutionContext> executionContexts = channelToExecutionContexts
                .get(channelEvent.getChannel().toString());
        if (executionContexts == null || executionContexts.isEmpty()) {
            logDebug("No execution context for channelEvent: {}", channelEvent);
            return;
        }
        executionContexts
                .forEach(context -> invokeWhenMatchParameters(context, new JRuleEvent(channelEvent.getEvent())));
    }

    private void handleEventUpdate(Event event) {
        final String itemName = getItemNameFromEvent(event);
        final List<JRuleExecutionContext> executionContexts = itemToExecutionContexts.get(itemName);
        if (executionContexts == null || executionContexts.isEmpty()) {
            logDebug("No execution context for changeEvent ");
            return;
        }
        final String type = ((ItemEvent) event).getType();

        final Set<String> triggerValues = new HashSet<>(5);
        String stringValue;
        String memberName = null;
        if (event instanceof GroupItemStateChangedEvent) {
            memberName = ((GroupItemStateChangedEvent) event).getMemberName();
        }

        if (event instanceof ItemStateEvent) {
            stringValue = ((ItemStateEvent) event).getItemState().toFullString();
            triggerValues.add(RECEIVED_UPDATE);
            triggerValues.add(RECEIVED_UPDATE_APPEND.concat(stringValue));
        } else if (event instanceof ItemCommandEvent) {
            stringValue = ((ItemCommandEvent) event).getItemCommand().toFullString();
            triggerValues.add(RECEIVED_COMMAND);
            triggerValues.add(RECEIVED_COMMAND_APPEND.concat(stringValue));
        } else if (event instanceof ItemStateChangedEvent) {
            final String newValue = ((ItemStateChangedEvent) event).getItemState().toFullString();
            final String oldValue = ((ItemStateChangedEvent) event).getOldItemState().toFullString();
            stringValue = newValue;

            if (JRuleUtil.isNotEmpty(oldValue) && JRuleUtil.isNotEmpty(newValue)) {
                triggerValues.add(String.format(CHANGED_FROM_TO_PATTERN, oldValue, newValue));
                triggerValues.add(CHANGED_FROM.concat(oldValue));
                triggerValues.add(CHANGED_TO.concat(newValue));
                triggerValues.add(CHANGED);
            }

            logDebug("newValue: {} oldValue: {} type: {}", newValue, oldValue, type);
            logDebug("Invoked execution contexts: {}", executionContexts.size());
            logDebug("Execution topic Topic: {}", event.getTopic());
            logDebug("Execution topic Payload: {}", event.getPayload());
            logDebug("Execution topic Source: {}", event.getSource());
            logDebug("Execution topic Type: {}", event.getType());
            logDebug("Execution eventToString: {}", event);
        } else {
            logDebug("Unhandled case: {}", event.getClass());
            return;
        }

        if (triggerValues.size() > 0) {
            String member = memberName == null ? "" : memberName;
            executionContexts.stream().filter(context -> triggerValues.contains(context.getTriggerFullString()))
                    .forEach(context -> invokeWhenMatchParameters(context, new JRuleEvent(stringValue, member)));
        } else {
            logDebug("Execution ignored, no trigger values for itemName: {} eventType: {}", itemName, type);
        }
    }

    private void invokeWhenMatchParameters(JRuleExecutionContext context, @NonNull JRuleEvent jRuleEvent) {
        JRuleLog.debug(logger, context.getLogName(), "invoke when context matches");
        if (context.isNumericOperation()) {
            Double value = getValueAsDouble(jRuleEvent.getValue());
            if (context.getGt() != null) {
                if (value > context.getGt()) {
                    invokeRule(context, jRuleEvent);
                }
            } else if (context.getGte() != null) {
                if (value >= context.getGte()) {
                    invokeRule(context, jRuleEvent);
                }
            } else if (context.getLt() != null) {
                if (value < context.getLt()) {
                    invokeRule(context, jRuleEvent);
                }

            } else if (context.getLte() != null) {
                if (value <= context.getLte()) {
                    invokeRule(context, jRuleEvent);
                }
            } else if (context.getEq() != null) {
                if (value.equals(context.getEq())) {
                    invokeRule(context, jRuleEvent);
                }
            }
        } else {
            invokeRule(context, jRuleEvent);
        }
    }

    private Double getValueAsDouble(String value) {
        double parseDouble = 0;
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            parseDouble = Double.parseDouble(value);
        } catch (Exception x) {
            logError("Failed to parse value: {} as double", value, x);
            return null;
        }
        return parseDouble;
    }

    @Nullable
    private String getItemNameFromEvent(Event event) {
        if (event instanceof ItemEvent) {
            return ((ItemEvent) event).getItemName();
        }
        return null;
    }

    private synchronized void invokeRule(JRuleExecutionContext context, JRuleEvent event) {
        JRuleLog.debug(logger, context.getLogName(), "Invoking rule for context: {}", context);
        final JRule rule = context.getJrule();
        final Method method = context.getMethod();
        rule.setRuleLogName(context.getLogName());
        try {
            final Object invoke = context.isEventParameterPresent() ? method.invoke(rule, event) : method.invoke(rule);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            JRuleLog.error(logger, context.getRuleName(), "Error {}", e);
        } catch (InvocationTargetException e) {
            Throwable ex = e.getCause() != null ? e.getCause() : null;
            JRuleLog.error(logger, context.getRuleName(), "Error message: {}", ex.getMessage());
            JRuleLog.error(logger, context.getRuleName(), "Error Stacktrace: {}", getStackTraceAsString(ex));
        }
    }

    private synchronized static String getStackTraceAsString(Throwable throwable) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public void setConfig(@NonNull JRuleConfig config) {
        this.config = config;
    }
}
