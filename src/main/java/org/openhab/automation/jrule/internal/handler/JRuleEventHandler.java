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
package org.openhab.automation.jrule.internal.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRulePointValue;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemEvent;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.PlayPauseType;
import org.openhab.core.library.types.PointType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEventHandler} is responsible for handling commands and status
 * updates for JRule
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleEventHandler {
    private static final Map<Class<? extends JRuleValue>, Class<? extends State>> stateMapping = new HashMap<>();

    static {
        // stateMapping.put(JRuleStringValue.class, QuantityType.class);
        stateMapping.put(JRuleOpenClosedValue.class, OpenClosedType.class);
        stateMapping.put(JRuleStringValue.class, StringType.class);
        // stateMapping.put(JRuleStringValue.class, UnDefType.class);
        stateMapping.put(JRuleUpDownValue.class, UpDownType.class);
        stateMapping.put(JRuleOnOffValue.class, OnOffType.class);
        stateMapping.put(JRuleDateTimeValue.class, DateTimeType.class);
        stateMapping.put(JRuleRawValue.class, RawType.class);
        // stateMapping.put(JRuleRe.class, RewindFastforwardType.class);
        stateMapping.put(JRulePointValue.class, PointType.class);
        stateMapping.put(JRuleHsbValue.class, HSBType.class);
        // stateMapping.put(JRuleStL.class, StringListType.class);
        stateMapping.put(JRuleDecimalValue.class, DecimalType.class);
        stateMapping.put(JRulePercentValue.class, PercentType.class);
        stateMapping.put(JRulePlayPauseValue.class, PlayPauseType.class);
        // stateMapping.put(JRuleColorValue.class, HSBType.class);
    }

    private static final String LOG_NAME_EVENT = "JRuleEvent";

    private static volatile JRuleEventHandler instance;

    private EventPublisher eventPublisher;

    private ItemRegistry itemRegistry;

    private final Logger logger = LoggerFactory.getLogger(JRuleEventHandler.class);

    private JRuleEventHandler() {
    }

    public static JRuleEventHandler get() {
        if (instance == null) {
            synchronized (JRuleEventHandler.class) {
                if (instance == null) {
                    instance = new JRuleEventHandler();
                }
            }
        }
        return instance;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void sendCommand(String itemName, String command) {
        sendCommand(itemName, new StringType(command));
    }

    public void sendCommand(String itemName, double value, String unit) {
        final QuantityType<?> type = new QuantityType<>(value + " " + unit);
        sendCommand(itemName, type);
    }

    public void sendCommand(String itemName, Command command) {
        if (eventPublisher == null) {
            return;
        }
        logInfo("SendCommand '{}' to '{}'", command, itemName);
        final ItemCommandEvent commandEvent = ItemEventFactory.createCommandEvent(itemName, command);
        eventPublisher.post(commandEvent);
    }

    public void postUpdate(String itemName, String value) {
        postUpdate(itemName, new StringType(value));
    }

    public void postUpdate(String itemName, double value, String unit) {
        QuantityType<?> type = new QuantityType<>(value + " " + unit);
        postUpdate(itemName, type);
    }

    private void postUpdate(String itemName, State state) {
        if (eventPublisher == null) {
            return;
        }
        logInfo("PostUpdate '{}' to '{}'", state, itemName);
        final ItemEvent itemEvent = ItemEventFactory.createStateEvent(itemName, state);
        eventPublisher.post(itemEvent);
    }

    public State getStateFromItem(String itemName) {
        if (itemRegistry == null) {
            return null;
        }
        try {
            Item item = itemRegistry.getItem(itemName);
            return item.getState();
        } catch (ItemNotFoundException e) {
            logError("Failed to find item: {}", itemName);
            return null;
        } catch (IllegalArgumentException i) {
            logDebug("Failed to get state from item: {}, returning undef", itemName);
            return UnDefType.UNDEF;
        }
    }

    public void setItemRegistry(@NonNull ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public Set<String> getGroupMemberNames(String groupName, boolean recursive) {
        return getGroupMemberItems(groupName, recursive).stream().map(JRuleItem::getName).collect(Collectors.toSet());
    }

    public Set<JRuleItem<? extends JRuleValue>> getGroupMemberItems(String groupName, boolean recursive) {
        try {
            Item item = itemRegistry.getItem(groupName);
            if (item instanceof GroupItem) {
                Set<JRuleItem<? extends JRuleValue>> out = new HashSet<>();
                GroupItem g = (GroupItem) item;
                g.getMembers().stream().map(item1 -> JRuleItemRegistry.get(item1.getName()))
                        .forEach(jRuleValueJRuleItem -> {
                            if (recursive) {
                                out.add(jRuleValueJRuleItem);
                                if (jRuleValueJRuleItem.isGroup()) {
                                    out.addAll(getGroupMemberItems(jRuleValueJRuleItem.getName(), recursive));
                                }
                            } else {
                                out.add(jRuleValueJRuleItem);
                            }
                        });
                return out;
            } else {
                throw new JRuleRuntimeException(String.format("Given itemname '%s' is not a groupitem", groupName));
            }
        } catch (ItemNotFoundException e) {
            throw new JRuleItemNotFoundException(
                    String.format("Item not found in registry for groupname '%s'", groupName));
        }
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    private void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, getLogName(LOG_NAME_EVENT), message, parameters);
    }

    private void logInfo(String message, Object... parameters) {
        JRuleLog.info(logger, getLogName(LOG_NAME_EVENT), message, parameters);
    }

    @SuppressWarnings("unused")
    private void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, getLogName(LOG_NAME_EVENT), message, parameters);
    }

    private void logError(String message, Object... parameters) {
        JRuleLog.error(logger, getLogName(LOG_NAME_EVENT), message, parameters);
    }

    private String getLogName(String defaultValue) {
        JRuleExecutionContext context = JRule.JRULE_EXECUTION_CONTEXT.get();
        if (context != null) {
            return context.getLogName();
        } else {
            return defaultValue;
        }
    }

    public <V extends JRuleValue> V getValue(String name, Class<V> valueClass) {
        State state = getStateFromItem(name);
        Objects.requireNonNull(state, "state must not be null");
        Class<? extends State> castTo = stateMapping.get(valueClass);
        Objects.requireNonNull(castTo, String.format("casting to '%s' for '%s' results in null", valueClass, name));
        State as = Objects.requireNonNull(state.as(castTo),
                String.format("no mapping for type: %s", state.getClass().getSimpleName()));

        return toValue(as.toFullString(), valueClass);
    }

    public <V extends JRuleValue> V toValue(String plain, Class<? extends JRuleValue> valueClass) {
        if (JRuleOpenClosedValue.class.isAssignableFrom(valueClass)) {
            return (V) JRuleOpenClosedValue.valueOf(plain);
        } else if (JRulePlayPauseValue.class.isAssignableFrom(valueClass)) {
            return (V) JRulePlayPauseValue.valueOf(plain);
        } else if (JRuleStringValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRuleStringValue(plain);
        } else if (JRuleUpDownValue.class.isAssignableFrom(valueClass)) {
            return (V) JRuleUpDownValue.getValueFromString(plain);
        } else if (JRuleOnOffValue.class.isAssignableFrom(valueClass)) {
            return (V) JRuleOnOffValue.getValueFromString(plain);
        } else if (JRuleDateTimeValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRuleDateTimeValue(plain);
        } else if (JRuleRawValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRuleRawValue(plain);
        } else if (JRulePointValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRulePointValue(plain);
        } else if (JRuleHsbValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRuleHsbValue(plain);
        } else if (JRulePercentValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRulePercentValue(plain);
        } else if (JRuleDecimalValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRuleDecimalValue(plain);
        } else if (JRuleDateTimeValue.class.isAssignableFrom(valueClass)) {
            return (V) JRulePlayPauseValue.valueOf(plain);
        }
        throw new IllegalStateException(String.format("not implemented type: %s", valueClass));
    }
}
