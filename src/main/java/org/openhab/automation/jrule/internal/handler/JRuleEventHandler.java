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
package org.openhab.automation.jrule.internal.handler;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.items.JRuleGroupItem;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.value.*;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.*;
import org.openhab.core.items.events.ItemEvent;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.types.*;
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
    private static final Map<Class<? extends JRuleValue>, Class<? extends Command>> commandMapping = new HashMap<>();

    static {
        commandMapping.put(JRuleOpenClosedValue.class, OpenClosedType.class);
        commandMapping.put(JRuleStringValue.class, StringType.class);
        commandMapping.put(JRuleUpDownValue.class, UpDownType.class);
        commandMapping.put(JRuleOnOffValue.class, OnOffType.class);
        commandMapping.put(JRuleStopMoveValue.class, StopMoveType.class);
        commandMapping.put(JRuleDateTimeValue.class, DateTimeType.class);
        commandMapping.put(JRulePointValue.class, PointType.class);
        commandMapping.put(JRuleDecimalValue.class, DecimalType.class);
        commandMapping.put(JRulePercentValue.class, PercentType.class);
        commandMapping.put(JRulePlayPauseValue.class, PlayPauseType.class);
        commandMapping.put(JRuleRewindFastforwardValue.class, RewindFastforwardType.class);
        commandMapping.put(JRuleNextPreviousValue.class, NextPreviousType.class);
        commandMapping.put(JRuleHsbValue.class, HSBType.class);
        commandMapping.put(JRuleQuantityValue.class, QuantityType.class);

        stateMapping.put(JRuleOpenClosedValue.class, OpenClosedType.class);
        stateMapping.put(JRuleStringValue.class, StringType.class);
        stateMapping.put(JRuleUpDownValue.class, UpDownType.class);
        stateMapping.put(JRuleOnOffValue.class, OnOffType.class);
        stateMapping.put(JRuleDateTimeValue.class, DateTimeType.class);
        stateMapping.put(JRuleRawValue.class, RawType.class);
        stateMapping.put(JRulePointValue.class, PointType.class);
        stateMapping.put(JRuleHsbValue.class, HSBType.class);
        stateMapping.put(JRuleDecimalValue.class, DecimalType.class);
        stateMapping.put(JRulePercentValue.class, PercentType.class);
        stateMapping.put(JRulePlayPauseValue.class, PlayPauseType.class);
        stateMapping.put(JRuleRewindFastforwardValue.class, RewindFastforwardType.class);
        stateMapping.put(JRuleQuantityValue.class, QuantityType.class);
        stateMapping.put(JRuleStringListValue.class, StringListType.class);
    }

    public static Class<? extends State> mapJRuleToOhType(Class<? extends JRuleValue> type) {
        return stateMapping.get(type);
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

    public void sendCommand(String itemName, JRuleValue command) {
        sendCommand(itemName, command.toOhCommand());
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
        try {
            if (!itemRegistry.getItem(itemName).getAcceptedCommandTypes().contains(command.getClass())) {
                throw new JRuleRuntimeException(
                        String.format("unacceptable command type '%s' for item '%s'", command.getClass(), itemName));
            }
        } catch (ItemNotFoundException e) {
            throw new JRuleRuntimeException("cannot resolve item: " + itemName, e);
        }
        eventPublisher.post(ItemEventFactory.createCommandEvent(itemName, command));
    }

    public void postUpdate(String itemName, JRuleValue value) {
        if (value == null) {
            postUpdate(itemName, UnDefType.NULL);
        } else {
            postUpdate(itemName, value.toOhState());
        }
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
        try {
            if (!itemRegistry.getItem(itemName).getAcceptedDataTypes().contains(state.getClass())) {
                throw new JRuleRuntimeException(
                        String.format("unacceptable command type '%s' for item '%s'", state.getClass(), itemName));
            }
        } catch (ItemNotFoundException e) {
            throw new JRuleRuntimeException("cannot resolve item: " + itemName, e);
        }
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

    public void setValue(String itemName, State itemState) {
        if (itemRegistry == null) {
            throw new JRuleRuntimeException("ItemRegistry must not be null");
        }
        try {
            Item item = itemRegistry.getItem(itemName);
            if (!(item instanceof GenericItem)) {
                throw new JRuleRuntimeException("Given item must be of type GenericItem");
            }
            ((GenericItem) item).setState(itemState);
        } catch (ItemNotFoundException e) {
            throw new JRuleRuntimeException(String.format("Failed to find item: %s", itemName));
        }
    }

    public void setItemRegistry(@NonNull ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public Set<String> getGroupMemberNames(String groupName, boolean recursive) {
        return getGroupMemberItems(groupName, recursive).stream().map(JRuleItem::getName).collect(Collectors.toSet());
    }

    public Set<JRuleItem> getGroupMemberItems(String groupName, boolean recursive) {
        try {
            Item item = itemRegistry.getItem(groupName);
            if (item instanceof GroupItem) {
                Set<JRuleItem> out = new HashSet<>();
                GroupItem g = (GroupItem) item;
                g.getMembers().stream().map(item1 -> JRuleItemRegistry.get(item1.getName())).forEach(jRuleItem -> {
                    if (recursive) {
                        out.add(jRuleItem);
                        if (jRuleItem.isGroup()) {
                            out.addAll(getGroupMemberItems(jRuleItem.getName(), recursive));
                        }
                    } else {
                        out.add(jRuleItem);
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

    public List<JRuleGroupItem<? extends JRuleItem>> getGroupItems(String itemName, boolean recursive) {
        try {
            Item item = itemRegistry.getItem(itemName);
            List<JRuleGroupItem<? extends JRuleItem>> list = item.getGroupNames().stream().map(JRuleItemRegistry::get)
                    .filter(JRuleItem::isGroup).map(i -> (JRuleGroupItem<? extends JRuleItem>) i)
                    .collect(Collectors.toList());
            if (recursive) {
                list.addAll(new ArrayList<>(list).stream().map(i -> getGroupItems(i.getName(), recursive))
                        .flatMap(Collection::stream).collect(Collectors.toList()));
            }
            return list.stream().distinct().collect(Collectors.toList());
        } catch (ItemNotFoundException e) {
            throw new JRuleItemNotFoundException(
                    String.format("Item not found in registry for item-name '%s'", itemName));
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
        if (state.getClass().equals(UnDefType.class)) {
            return null;
        }
        Objects.requireNonNull(state, "state must not be null");
        Class<? extends State> castTo = stateMapping.get(valueClass);
        Objects.requireNonNull(castTo, String.format("casting to '%s' for '%s' results in null", valueClass, name));
        State as = Optional.ofNullable(state.as(castTo))
                .orElseThrow(() -> new JRuleRuntimeException(String.format("no mapping for type '%s' to '%s'",
                        state.getClass().getSimpleName(), valueClass.getSimpleName())));

        return toValue(as.toFullString(), valueClass);
    }

    public JRuleValue toValue(Command itemCommand) {
        Class<? extends JRuleValue> valueClass = commandMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(itemCommand.getClass())).findFirst()
                .map((Map.Entry<Class<? extends JRuleValue>, Class<? extends Command>> classClassEntry) -> Objects
                        .requireNonNull(classClassEntry.getKey()))
                .orElseThrow(
                        () -> new IllegalStateException("cannot find mapping for oh type: " + itemCommand.getClass()));
        return toValue(itemCommand.toFullString(), valueClass);
    }

    public JRuleValue toValue(State itemState) {
        if (itemState == null || itemState instanceof UnDefType) {
            return null;
        }
        Class<? extends JRuleValue> valueClass = stateMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(itemState.getClass())).findFirst()
                .map((Map.Entry<Class<? extends JRuleValue>, Class<? extends State>> classClassEntry) -> Objects
                        .requireNonNull(classClassEntry.getKey()))
                .orElseThrow(
                        () -> new IllegalStateException("cannot find mapping for oh type: " + itemState.getClass()));
        return toValue(itemState.toFullString(), valueClass);
    }

    public JRuleValue getValue(String name) {
        return toValue(getStateFromItem(name));
    }

    public <V extends JRuleValue> V toValue(String plain, Class<? extends JRuleValue> valueClass) {
        if (JRuleOpenClosedValue.class.isAssignableFrom(valueClass)) {
            return (V) JRuleOpenClosedValue.valueOf(plain);
        } else if (JRulePlayPauseValue.class.isAssignableFrom(valueClass)) {
            return (V) JRulePlayPauseValue.valueOf(plain);
        } else if (JRuleNextPreviousValue.class.isAssignableFrom(valueClass)) {
            return (V) JRuleNextPreviousValue.valueOf(plain);
        } else if (JRuleRewindFastforwardValue.class.isAssignableFrom(valueClass)) {
            return (V) JRuleRewindFastforwardValue.valueOf(plain);
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
        } else if (JRuleQuantityValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRuleQuantityValue(plain);
        } else if (JRuleDateTimeValue.class.isAssignableFrom(valueClass)) {
            return (V) JRulePlayPauseValue.valueOf(plain);
        } else if (JRuleStopMoveValue.class.isAssignableFrom(valueClass)) {
            return (V) JRuleStopMoveValue.valueOf(plain);
        } else if (JRuleStringListValue.class.isAssignableFrom(valueClass)) {
            return (V) new JRuleStringListValue(plain);
        }
        throw new IllegalStateException(String.format("not implemented type: %s", valueClass));
    }
}
