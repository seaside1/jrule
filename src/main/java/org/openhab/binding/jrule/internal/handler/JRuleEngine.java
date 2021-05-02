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
package org.openhab.binding.jrule.internal.handler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.jrule.internal.JRuleBindingConstants;
import org.openhab.binding.jrule.internal.JRuleUtil;
import org.openhab.binding.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.binding.jrule.items.JRuleItem;
import org.openhab.binding.jrule.items.JRuleItemType;
import org.openhab.binding.jrule.rules.JRule;
import org.openhab.binding.jrule.rules.JRuleEvent;
import org.openhab.binding.jrule.rules.JRuleName;
import org.openhab.binding.jrule.rules.JRuleTrigger;
import org.openhab.binding.jrule.rules.JRuleWhen;
import org.openhab.core.events.Event;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemEvent;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleBindingConstants} class defines common constants, which are
 * used across the Java Rule binding.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleEngine implements PropertyChangeListener {

    private static final String STATE_CHANGED = "/statechanged";

    private static final String ITEM_TYPE = "TYPE";

    private static JRuleEngine instance;

    private Map<String, List<JRule>> itemToRules = new HashMap<>();

    private Map<String, List<JRuleExecutionContext>> itemToExecutionContexts = new HashMap<>();

    private Set<String> itemNames = new HashSet<>();
    private final Logger logger = LoggerFactory.getLogger(JRuleEngine.class);

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

    public synchronized void reset() {
        itemNames.clear();
        itemToExecutionContexts.clear();
        itemToRules.clear();
    }

    public void remove(JRule jRule) {
    }

    //
    //
    public void add(JRule jRule) {
        logger.debug("++ adding rule: {}", jRule);
        Class<?> clazz = jRule.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(JRuleName.class)) {
                logger.warn("Rule ignored since JRuleName annotation is missing");
                continue;
            }

            if (method.getAnnotationsByType(JRuleWhen.class).length == 0) {
                // method.isAnnotationPresent(JRuleWhens.class)) {
                logger.warn("Rule ignored since JWhens annotation is missing");
                logger.warn("Rule JWhen present: {}", method.isAnnotationPresent(JRuleWhen.class));
                final JRuleWhen[] jRuleWhens = method.getAnnotationsByType(JRuleWhen.class);
                logger.debug("Got jrule whens size: {}", jRuleWhens.length);
                continue;
            }
            final JRuleName jRuleName = method.getDeclaredAnnotation(JRuleName.class);
            // final JRuleWhen jRuleWhen = method.getDeclaredAnnotation(JRuleWhen.class);
            // getAnnotationsByType(Foo.class).length != 0;
            final JRuleWhen[] jRuleWhens = method.getAnnotationsByType(JRuleWhen.class);
            logger.debug("Got jrule whens size: {}", jRuleWhens.length);
            Parameter[] parameters = method.getParameters();
            boolean jRuleEventPresent = Arrays.stream(parameters)
                    .filter(param -> (param.getType().equals(JRuleEvent.class))).count() > 0;
            // Validate make sure name and when is there
            // Make sure when has item ref
            // Loop and find the other and ors
            for (JRuleWhen jRuleWhen : jRuleWhens) {
                final String itemClass = "org.openhab.binding.jrule.items.generated._" + jRuleWhen.item();
                // getClassForItemFromAnnotation(
                // "org.openhab.binding.jrule.items.generated._" + jRuleWhen.item());

                // logger.debug("Got item class: {}", itemClass.getClass());
                logger.debug("Got item class: {}", itemClass);
                // JRuleItemType jRuleItemTypeFromItemClass = getJRuleItemTypeFromItemClass(itemClass);
                logger.info("Validating JRule: name: {} trigger: {} ", jRuleName.value(), jRuleWhen.trigger());

                addExecutionContext(jRule, itemClass, jRuleName.value(), jRuleWhen.trigger(), jRuleWhen.from(),
                        jRuleWhen.to(), jRuleWhen.update(), jRuleWhen.item(), method, jRuleEventPresent);
                itemNames.add(jRuleWhen.item() + getTypeOfEventFromTrigger(jRuleWhen.trigger()));
                // item -> (name, trigger, itemclass)

                // item and trigger to rule
                // SKa en item och tigger ge flera regler? ja

                // HERE: Put trigger to rule hashmap or item to rule hashmao
                // Check what you get from item notification
                // Take the item used in rule and put to subscripe hashmap so we don't have to listen to it all
                // items probably item to list of rules

                // JRuleItem item = getJRuleItemFromAnnotation(jRuleWhen.trigger());
                // validateRule(jRuleName.value(), "org.openhab.binding.jrule.items.generated._" + jRuleWhen.item() +
                // "",
                // jRuleWhen.trigger());
            }
        }
    }

    private void addExecutionContext(JRule jRule, String itemClass, String ruleName, String trigger, String from,
            String to, String update, String itemName, Method method, boolean eventParameterPresent) {
        List<JRuleExecutionContext> contextList = itemToExecutionContexts.get(itemName);
        if (contextList == null) {
            contextList = new ArrayList<>();
            itemToExecutionContexts.put(itemName, contextList);
        }
        logger.debug("++ContextList add: {} itemName: {}", ruleName, itemName);
        contextList.add(new JRuleExecutionContext(jRule, trigger, from, to, update, ruleName, itemClass, itemName,
                method, eventParameterPresent));
    }

    private String getTypeOfEventFromTrigger(String trigger) {
        if (trigger.startsWith(JRuleItem.TRIGGER_CHANGED)) {
            return STATE_CHANGED;
        }

        if (trigger.startsWith(JRuleItem.TRIGGER_RECEIVED_UPDATE)) {
            return "/state";
        }

        if (trigger.startsWith(JRuleItem.TRIGGER_RECEIVED_COMMAND)) {
            return "/command";
        }
        logger.error("Failed to return type of event for trigger: {}", trigger);
        return "";
    }

    @Nullable
    private JRuleItemType getJRuleItemTypeFromItemClass(Class<?> cls) {
        JRuleItemType type = null;
        Field declaredField;
        try {
            declaredField = cls.getDeclaredField(ITEM_TYPE);
            type = (JRuleItemType) (declaredField.get(null));
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            logger.warn("Failed to get Type for class: {}", cls.getCanonicalName());
            logger.debug("Failed to get type for class: {}", cls.getCanonicalName(), e);
            return null;
        }
        return type;
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
        logger.debug("++Event Update: {}", evt.getNewValue());
        if (evt.getPropertyName().equals(JRuleEventSubscriber.PROPERTY_ITEM_EVENT)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Property change item event! : {}", ((Event) evt.getNewValue()).getTopic());
            }
            handleEventUpdate((Event) evt.getNewValue());
            return;
        }
    }

    private void handleEventUpdate(Event event) {
        final String itemName = getItemNameFromEvent(event);
        final List<JRuleExecutionContext> exectionContexts = itemToExecutionContexts.get(itemName);
        if (exectionContexts == null || exectionContexts.isEmpty()) {
            logger.debug("++No execution context for changeEvent ");
            return;
        }
        final String type = ((ItemEvent) event).getType();
        final Set<String> triggerValues = new HashSet<>(5);
        String stringValue;
        if (event instanceof ItemStateEvent) {
            stringValue = ((ItemStateEvent) event).getItemState().toFullString();
            triggerValues.add("received update");
            triggerValues.add("received update " + stringValue);
        } else if (event instanceof ItemCommandEvent) {
            stringValue = ((ItemCommandEvent) event).getItemCommand().toFullString();
            triggerValues.add("received command");
            triggerValues.add("received command " + stringValue);
        } else if (event instanceof ItemStateChangedEvent) {
            final String newValue = ((ItemStateChangedEvent) event).getItemState().toFullString();
            final String oldValue = ((ItemStateChangedEvent) event).getOldItemState().toFullString();
            stringValue = newValue;
            logger.debug("StringValue: {} type: {}", newValue, type);
            logger.debug("++Invoked execution contexts: {}", exectionContexts.size());
            logger.debug("Execution topic Topic: {}", event.getTopic());
            logger.debug("Execution topic Payload: {}", event.getPayload());
            logger.debug("Execution topic Source: {}", event.getSource());
            logger.debug("Execution topic Type: {}", event.getType());
            logger.debug("Execution eventToString: {}", event);

            if (JRuleUtil.isNotEmpty(oldValue) && JRuleUtil.isNotEmpty(newValue)) {
                triggerValues.add("Changed from " + oldValue + " to " + newValue);
                triggerValues.add("Changed from" + oldValue);
                triggerValues.add("Changed to " + newValue);
                triggerValues.add("Changed");
            }
        } else {
            logger.debug("++Unhandled case: {}", event.getClass());
            return;
        }

        if (triggerValues.size() > 0) {
            exectionContexts.stream().forEach(context -> logger.debug("+Context: {} contained: {}",
                    context.getTriggerFullString(), triggerValues.contains(context.getTriggerFullString())));
            exectionContexts.stream().filter(context -> triggerValues.contains(context.getTriggerFullString()))
                    .forEach(context -> invokeRule(context, new JRuleEvent(stringValue)));
        }
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
        Class<?> itemClazz = context.getClass();
        JRule rule = context.getJrule();
        Method method = context.getMethod();
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

    public void clear() {
        itemToRules.clear();
        itemToExecutionContexts.clear();
        itemNames.clear();
    }
}
