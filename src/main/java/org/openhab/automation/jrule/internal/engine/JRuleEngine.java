/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.JRuleItemType;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleEvent;
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

    private static JRuleEngine instance;

    private Map<String, List<JRule>> itemToRules = new HashMap<>();

    private Map<String, List<JRuleExecutionContext>> itemToExecutionContexts = new HashMap<>();

    private Map<String, List<JRuleExecutionContext>> channelToExecutionContexts = new HashMap<>();

    private Set<String> itemNames = new HashSet<>();
    private final Logger logger = LoggerFactory.getLogger(JRuleEngine.class);
    private Set<CompletableFuture<Void>> timers = new HashSet<>();
    protected final ScheduledExecutorService scheduler = ThreadPoolManager
            .getScheduledPool(ThreadPoolManager.THREAD_POOL_NAME_COMMON);

    private JRuleEngine() {
    }

    public static JRuleEngine get() {
        if (instance == null) {
            instance = new JRuleEngine();
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
        timers.stream().forEach(d -> d.cancel(true));
        timers.clear();
    }

    public void add(JRule jRule) {
        logger.debug("Adding rule: {}", jRule);
        Class<?> clazz = jRule.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(JRuleName.class)) {
                logger.debug("Rule method ignored since JRuleName annotation is missing: {}", method.getName());
                continue;
            }

            if (method.getAnnotationsByType(JRuleWhen.class).length == 0) {
                logger.warn("Rule ignored since JWhens annotation is missing");
                logger.warn("Rule JWhen present: {}", method.isAnnotationPresent(JRuleWhen.class));
                final JRuleWhen[] jRuleWhens = method.getAnnotationsByType(JRuleWhen.class);
                logger.debug("Got jrule whens size: {}", jRuleWhens.length);
                continue;
            }
            final JRuleName jRuleName = method.getDeclaredAnnotation(JRuleName.class);
            final JRuleWhen[] jRuleWhens = method.getAnnotationsByType(JRuleWhen.class);
            logger.debug("Got jrule whens size: {}", jRuleWhens.length);
            Parameter[] parameters = method.getParameters();
            boolean jRuleEventPresent = Arrays.stream(parameters)
                    .filter(param -> (param.getType().equals(JRuleEvent.class))).count() > 0;
            // Validate make sure name and when is there
            // Make sure when has item ref
            // Loop and find the other and ors
            for (JRuleWhen jRuleWhen : jRuleWhens) {

                logger.debug("- processing jRule when: {}", jRuleWhen);

                if (!jRuleWhen.item().isEmpty()) {
                    // JRuleWhen for an item
                    final String itemClass = "org.openhab.automation.jrule.items.generated._" + jRuleWhen.item();
                    logger.debug("Got item class: {}", itemClass);
                    logger.info("Validating JRule: name: {} trigger: {} ", jRuleName.value(), jRuleWhen.trigger());

                    addExecutionContext(jRule, itemClass, jRuleName.value(), jRuleWhen.trigger(), jRuleWhen.from(),
                            jRuleWhen.to(), jRuleWhen.update(), jRuleWhen.item(), method, jRuleEventPresent,
                            getDoubelFromAnnotation(jRuleWhen.lt()), getDoubelFromAnnotation(jRuleWhen.lte()),
                            getDoubelFromAnnotation(jRuleWhen.gt()), getDoubelFromAnnotation(jRuleWhen.gte()),
                            getDoubelFromAnnotation(jRuleWhen.eq()));
                    itemNames.add(jRuleWhen.item());

                } else if (jRuleWhen.hours() != -1 || jRuleWhen.minutes() != -1 || jRuleWhen.seconds() != -1) {
                    // JRuleWhen for a time trigger
                    addTimedExecution(jRule, jRuleName.value(), jRuleWhen, method, jRuleEventPresent);

                } else if (!jRuleWhen.channel().isEmpty()) {
                    // JRuleWhen for a channel
                    addChannelExecutionContext(jRule, jRuleWhen.channel(), jRuleName.value(), method,
                            jRuleEventPresent);
                }

                // HERE: Put trigger to rule hashmap or item to rule hashmao
                // Check what you get from item notification
                // Take the item used in rule and put to subscripe hashmap so we don't have to listen to it all
                // items probably item to list of rules
            }
        }
    }

    private Double getDoubelFromAnnotation(double d) {
        if (d == Double.MIN_VALUE) {
            return null;
        }
        return Double.valueOf(d);
    }

    private CompletableFuture<Void> createTimer(int hours, int minutes, int seconds) {
        Calendar calFuture = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        calFuture.set(Calendar.HOUR_OF_DAY, hours == -1 ? 0 : hours);
        calFuture.set(Calendar.MINUTE, minutes == -1 ? 0 : minutes);
        calFuture.set(Calendar.SECOND, seconds == -1 ? 0 : seconds);
        logger.debug("Hours: {} calHours: {}", hours, calFuture.get(Calendar.HOUR_OF_DAY));
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
        Date date = calFuture.getTime();
        long initialDelay = new Date(date.getTime() - System.currentTimeMillis()).getTime();
        logger.debug("Schedule crond: {} initialDelay: {}", date, initialDelay);
        Executor delayedExecutor = CompletableFuture.delayedExecutor(initialDelay, TimeUnit.MILLISECONDS, scheduler);
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> null, delayedExecutor);
        return future;
    }

    private synchronized void addTimedExecution(JRule jRule, String jRuleName, JRuleWhen jRuleWhen, Method method,
            boolean jRuleEventPresent) {
        CompletableFuture<Void> future = createTimer(jRuleWhen.hours(), jRuleWhen.minutes(), jRuleWhen.seconds());
        timers.add(future);
        logger.info("Scheduling timer for rule: {} hours: {} minutes: {} seconds: {}", jRule, jRuleWhen.hours(),
                jRuleWhen.minutes(), jRuleWhen.seconds());
        JRuleExecutionContext executionContext = new JRuleExecutionContext(jRule, method, jRuleName, jRuleEventPresent);
        Consumer<Void> consumer = new Consumer<Void>() {
            @Override
            public void accept(Void t) {
                try {
                    invokeRule(executionContext, jRuleEventPresent ? new JRuleEvent("") : null);
                } finally {
                    timers.remove(future);
                }
            }
        };
        future.thenAccept(consumer).thenAccept(s -> {
            logger.info("Timer has finsihed rule: {}", jRuleName);
            createTimer(jRuleWhen.hours(), jRuleWhen.minutes(), jRuleWhen.seconds());
            addTimedExecution(jRule, jRuleName, jRuleWhen, method, jRuleEventPresent);
        });
    }

    private void addExecutionContext(JRule jRule, String itemClass, String ruleName, String trigger, String from,
            String to, String update, String itemName, Method method, boolean eventParameterPresent, Double lt,
            Double lte, Double gt, Double gte, Double eq) {
        List<JRuleExecutionContext> contextList = itemToExecutionContexts.computeIfAbsent(itemName,
                k -> new ArrayList<>());
        final JRuleExecutionContext context = new JRuleExecutionContext(jRule, trigger, from, to, update, ruleName,
                itemClass, itemName, method, eventParameterPresent, lt, lte, gt, gte, eq);
        logger.debug("ItemContextList add context: {}", context);
        contextList.add(context);
    }

    private void addChannelExecutionContext(JRule jRule, String channel, String ruleName, Method method,
            boolean eventParameterPresent) {
        List<JRuleExecutionContext> contextList = channelToExecutionContexts.computeIfAbsent(channel,
                k -> new ArrayList<>());
        final JRuleExecutionContext context = new JRuleExecutionContext(jRule, null, null, null, null, ruleName, null,
                null, method, eventParameterPresent, null, null, null, null, null);
        logger.debug("ChannelContextList add context: {}", context);
        contextList.add(context);
    }

    private String getTypeOfEventFromTrigger(String trigger) {
        if (trigger.startsWith(JRuleItem.TRIGGER_CHANGED)) {
            return STATE_CHANGED;
        }

        if (trigger.startsWith(JRuleItem.TRIGGER_RECEIVED_UPDATE)) {
            return STATE;
        }

        if (trigger.startsWith(JRuleItem.TRIGGER_RECEIVED_COMMAND)) {
            return COMMAND;
        }
        logger.error("Failed to return type of event for trigger: {}", trigger);
        return "";
    }

    private boolean validateRule(String ruleName, String item, JRuleTrigger trigger) {
        Class<?> cls = null;
        try {
            cls = Class.forName(item);
        } catch (ClassNotFoundException e) {
            logger.warn("Failed to validate rule: {}, could not find class for item: {}", ruleName, item);
            return false;
        }
        JRuleItemType type = null;
        try {
            final Field declaredField = cls.getDeclaredField(ITEM_TYPE);
            type = (JRuleItemType) (declaredField.get(null));
        } catch (NoSuchFieldException e) {
            logger.warn("Failed to validate, missing Item type Field ruleName: {}, item: {}", ruleName, item);
            return false;
        } catch (SecurityException e) {
            logger.warn("Failed to validate, security exception ruleName: {}, item: {}", ruleName, item, e);
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to validate ruleName: {}, item: {}", ruleName, item, e);
            return false;
        } catch (IllegalAccessException e) {
            logger.warn("Failed to validate, exception ruleName: {}, item: {}", ruleName, item, e);
            return false;
        }

        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_ITEM_EVENT)) {
            logger.debug("Property change item event! : {}", ((Event) evt.getNewValue()).getTopic());
            handleEventUpdate((Event) evt.getNewValue());

        } else if (evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_CHANNEL_EVENT)) {
            logger.debug("Channel event! : {}", ((Event) evt.getNewValue()).getTopic());
            handleChannelEvent((ChannelTriggeredEvent) evt.getNewValue());
        }
    }

    private void handleChannelEvent(ChannelTriggeredEvent channelEvent) {
        List<JRuleExecutionContext> executionContexts = channelToExecutionContexts
                .get(channelEvent.getChannel().toString());
        if (executionContexts == null || executionContexts.isEmpty()) {
            logger.debug("No execution context for channelEvent: {}", channelEvent);
            return;
        }
        executionContexts
                .forEach(context -> invokeWhenMatchParameters(context, new JRuleEvent(channelEvent.getEvent())));
    }

    private void handleEventUpdate(Event event) {
        final String itemName = getItemNameFromEvent(event);
        final List<JRuleExecutionContext> exectionContexts = itemToExecutionContexts.get(itemName);
        if (exectionContexts == null || exectionContexts.isEmpty()) {
            logger.debug("No execution context for changeEvent ");
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

            logger.debug("newValue: {} oldValue: {} type: {}", newValue, oldValue, type);
            logger.debug("Invoked execution contexts: {}", exectionContexts.size());
            logger.debug("Execution topic Topic: {}", event.getTopic());
            logger.debug("Execution topic Payload: {}", event.getPayload());
            logger.debug("Execution topic Source: {}", event.getSource());
            logger.debug("Execution topic Type: {}", event.getType());
            logger.debug("Execution eventToString: {}", event);
        } else {
            logger.debug("Unhandled case: {}", event.getClass());
            return;
        }

        if (triggerValues.size() > 0) {
            String member = memberName == null ? "" : memberName;
            exectionContexts.stream().filter(context -> triggerValues.contains(context.getTriggerFullString()))
                    .forEach(context -> invokeWhenMatchParameters(context, new JRuleEvent(stringValue, member)));
        } else {
            logger.debug("Execution ignored, no trigger values for itemName: {} eventType: {}", itemName, type);
        }
    }

    private void invokeWhenMatchParameters(JRuleExecutionContext context, @NonNull JRuleEvent jRuleEvent) {
        logger.debug("invoke when context matches");
        if (context.isNumericOperation()) {
            Double value = getValuAsDouble(jRuleEvent.getValue());
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

    private Double getValuAsDouble(String value) {
        double parseDouble = 0;
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            parseDouble = Double.parseDouble(value);
        } catch (Exception x) {
            logger.error("Failed to parse value: {} as double", value, x);
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

    private void invokeRule(JRuleExecutionContext context, JRuleEvent event) {
        logger.debug("Invoking rule for context: {}", context);
        final JRule rule = context.getJrule();
        final Method method = context.getMethod();
        logger.debug("Invoking context: {}", context);
        try {
            final Object invoke = context.isEventParameterPresent() ? method.invoke(rule, event) : method.invoke(rule);
        } catch (IllegalAccessException e) {
            logger.error("Error", e);
        } catch (IllegalArgumentException e) {
            logger.error("Error", e);
        } catch (InvocationTargetException e) {
            logger.error("Error", e);
        } catch (SecurityException e) {
            logger.error("Error", e);
        }
    }
}
