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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.internal.JRuleItemUtil;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.items.JRulePercentType;
import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.automation.jrule.rules.value.JRuleRgbValue;
import org.openhab.automation.jrule.rules.value.JRuleStopMoveValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.rules.value.JRuleXyValue;
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
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.PlayPauseType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.library.types.StopMoveType;
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

    public void sendCommand(String itemName, JRulePlayPauseValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, JRuleOpenClosedValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, JRuleOnOffValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, JRuleIncreaseDecreaseValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, JRuleUpDownValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, JRuleStopMoveValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, String command) {
        sendCommand(itemName, new StringType(command));
    }

    public void postUpdate(String itemName, JRuleRawValue command) {
        postUpdate(itemName, new RawType(command.getData(), command.getMimeType()));
    }

    public void sendCommand(String itemName, JRulePercentType value) {
        sendCommand(itemName, new PercentType(value.getValue()));
    }

    public void sendCommand(String itemName, int value) {
        sendCommand(itemName, new DecimalType(value));
    }

    public void sendCommand(String itemName, double value) {
        sendCommand(itemName, new DecimalType(value));
    }

    public void sendCommand(String itemName, Date date) {
        sendCommand(itemName, new DateTimeType(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())));
    }

    public void sendCommand(String itemName, ZonedDateTime zonedDateTime) {
        sendCommand(itemName, new DateTimeType(zonedDateTime));
    }

    public void sendCommand(String itemName, JRuleColorValue colorValue) {
        final HSBType hsbType = JRuleItemUtil.getHsbType(colorValue);
        if (hsbType == null) {
            logError("Failed to sen command for colorValue: {}", colorValue);
            return;
        }
        sendCommand(itemName, hsbType);
    }

    public void sendCommand(String itemName, double value, String unit) {
        final QuantityType<?> type = new QuantityType<>(value + " " + unit);
        sendCommand(itemName, type);
    }

    public void sendCommand(String itemName, JRuleRgbValue rgbValue) {
        sendCommand(itemName, HSBType.fromRGB(rgbValue.getRed(), rgbValue.getGreen(), rgbValue.getBlue()));
    }

    public void sendCommand(String itemName, JRuleXyValue xyValue) {
        sendCommand(itemName, HSBType.fromXY(xyValue.getX(), xyValue.getY()));
    }

    public void sendCommand(String itemName, Command command) {
        if (eventPublisher == null) {
            return;
        }
        logInfo("SendCommand itemName: {} command: {}", itemName, command);
        final ItemCommandEvent commandEvent = ItemEventFactory.createCommandEvent(itemName, command);
        eventPublisher.post(commandEvent);
    }

    public void postUpdate(String itemName, JRuleColorValue colorValue) {
        final HSBType hsbType = JRuleItemUtil.getHsbType(colorValue);
        if (hsbType == null) {
            logError("Failed to get HSB Type from ColorValue: {} itemName: {}", colorValue, itemName);
            return;
        }
        postUpdate(itemName, hsbType);
    }

    public void postUpdate(String itemName, JRuleRgbValue rgbValue) {
        postUpdate(itemName, HSBType.fromRGB(rgbValue.getRed(), rgbValue.getGreen(), rgbValue.getBlue()));
    }

    public void postUpdate(String itemName, JRuleXyValue xyValue) {
        postUpdate(itemName, HSBType.fromXY(xyValue.getX(), xyValue.getY()));
    }

    public void postUpdate(String itemName, Date date) {
        postUpdate(itemName, new DateTimeType(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())));
    }

    public void postUpdate(String itemName, ZonedDateTime zonedDateTime) {
        postUpdate(itemName, new DateTimeType(zonedDateTime));
    }

    public void postUpdate(String itemName, JRulePercentType value) {
        postUpdate(itemName, new PercentType(value.getValue()));
    }

    public void postUpdate(String itemName, JRulePlayPauseValue value) {
        postUpdate(itemName, getStateFromValue(value));
    }

    public void postUpdate(String itemName, JRuleOnOffValue value) {
        postUpdate(itemName, getStateFromValue(value));
    }

    public void postUpdate(String itemName, JRuleOpenClosedValue value) {
        postUpdate(itemName, getStateFromValue(value));
    }

    public void postUpdate(String itemName, JRuleUpDownValue value) {
        postUpdate(itemName, getStateFromValue(value));
    }

    public void postUpdate(String itemName, String value) {
        postUpdate(itemName, new StringType(value));
    }

    public void postUpdate(String itemName, double value, String unit) {
        QuantityType<?> type = new QuantityType<>(value + " " + unit);
        postUpdate(itemName, type);
    }

    public void postUpdate(String itemName, double value) {
        postUpdate(itemName, new DecimalType(value));
    }

    public void postUpdate(String itemName, int value) {
        postUpdate(itemName, new DecimalType(value));
    }

    public void postUpdate(String itemName, State state) {
        if (eventPublisher == null) {
            return;
        }
        logInfo("PostUpdate itemName: {} state: {}", itemName, state);
        final ItemEvent itemEvent = ItemEventFactory.createStateEvent(itemName, state);
        eventPublisher.post(itemEvent);
    }

    public JRulePlayPauseValue getPauseValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getPlayPauseValueFromState(state);
    }

    public JRuleColorValue getColorValue(String itemName) {
        State state = getStateFromItem(itemName);
        return JRuleItemUtil.getColorValueFromState(state);
    }

    public JRuleOnOffValue getOnOffValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getOnOffValueFromState(state);
    }

    public JRuleOpenClosedValue getOpenClosedValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getOpenClosedValueFromState(state);
    }

    public JRuleUpDownValue getUpDownValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getUpDownValueFromState(state);
    }

    public JRulePlayPauseValue getPlayPauseValueFromState(State state) {
        if (state instanceof UnDefType) {
            return JRulePlayPauseValue.UNDEF;
        }
        final PlayPauseType playPauseType = PlayPauseType.valueOf(state.toFullString());
        switch (playPauseType) {
            case PLAY:
                return JRulePlayPauseValue.PLAY;
            case PAUSE:
                return JRulePlayPauseValue.PAUSE;
            default:
                logError("Fail to transform playpause value");
                return JRulePlayPauseValue.UNDEF;
        }
    }

    private JRuleOnOffValue getOnOffValueFromState(State state) {
        if (state instanceof UnDefType) {
            return JRuleOnOffValue.UNDEF;
        }
        OnOffType onOffType = OnOffType.from(state.toFullString());
        switch (onOffType) {
            case OFF:
                return JRuleOnOffValue.OFF;
            case ON:
                return JRuleOnOffValue.ON;
            default:
                logError("Fail to transform onoff value");
                return JRuleOnOffValue.UNDEF;
        }
    }

    private JRuleOpenClosedValue getOpenClosedValueFromState(State state) {
        if (state instanceof UnDefType) {
            return JRuleOpenClosedValue.UNDEF;
        }
        OpenClosedType openClosedType = OpenClosedType.valueOf(state.toFullString());
        switch (openClosedType) {
            case OPEN:
                return JRuleOpenClosedValue.OPEN;
            case CLOSED:
                return JRuleOpenClosedValue.CLOSED;
            default:
                logError("Fail to transform openclosed value");
                return JRuleOpenClosedValue.UNDEF;
        }
    }

    private JRuleUpDownValue getUpDownValueFromState(State state) {
        if (state instanceof UnDefType) {
            return JRuleUpDownValue.UNDEF;
        }
        final UpDownType playPauseType = UpDownType.valueOf(state.toFullString());
        switch (playPauseType) {
            case UP:
                return JRuleUpDownValue.UP;
            case DOWN:
                return JRuleUpDownValue.DOWN;
            default:
                logError("Fail to transform up/down value");
                return JRuleUpDownValue.UNDEF;
        }
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

    private State getStateFromValue(JRuleOnOffValue value) {
        switch (value) {
            case OFF:
                return OnOffType.OFF;
            case ON:
                return OnOffType.ON;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", value);
                return null;
        }
    }

    private State getStateFromValue(JRuleUpDownValue value) {
        switch (value) {
            case UP:
                return UpDownType.UP;
            case DOWN:
                return UpDownType.DOWN;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", value);
                return null;
        }
    }

    private State getStateFromValue(JRulePlayPauseValue value) {
        switch (value) {
            case PLAY:
                return PlayPauseType.PLAY;
            case PAUSE:
                return PlayPauseType.PAUSE;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", value);
                return null;
        }
    }

    private Command getCommand(JRulePlayPauseValue command) {
        switch (command) {
            case PLAY:
                return PlayPauseType.PLAY;
            case PAUSE:
                return PlayPauseType.PAUSE;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", command);
                return null;
        }
    }

    private Command getCommand(JRuleOpenClosedValue command) {
        switch (command) {
            case OPEN:
                return OpenClosedType.OPEN;
            case CLOSED:
                return OpenClosedType.CLOSED;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", command);
                return null;
        }
    }

    private State getStateFromValue(JRuleOpenClosedValue command) {
        switch (command) {
            case OPEN:
                return OpenClosedType.OPEN;
            case CLOSED:
                return OpenClosedType.CLOSED;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", command);
                return null;
        }
    }

    private Command getCommand(JRuleOnOffValue command) {
        switch (command) {
            case OFF:
                return OnOffType.OFF;
            case ON:
                return OnOffType.ON;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", command);
                return null;
        }
    }

    private Command getCommand(JRuleUpDownValue command) {
        switch (command) {
            case UP:
                return UpDownType.UP;
            case DOWN:
                return UpDownType.DOWN;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", command);
                return null;
        }
    }

    private Command getCommand(JRuleStopMoveValue command) {
        switch (command) {
            case STOP:
                return StopMoveType.STOP;
            case MOVE:
                return StopMoveType.MOVE;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", command);
                return null;
        }
    }

    private Command getCommand(JRuleIncreaseDecreaseValue command) {
        switch (command) {
            case INCREASE:
                return IncreaseDecreaseType.INCREASE;
            case DECREASE:
                return IncreaseDecreaseType.DECREASE;
            case UNDEF:
            default:
                logError("Unhandled getCommand: {}", command);
                return null;
        }
    }

    public void setItemRegistry(@NonNull ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    @SuppressWarnings("rawtypes")
    public Double getStateFromItemAsDouble(String name) {
        State state = getStateFromItem(name);
        if (state == null) {
            return null;
        }
        if (state instanceof PercentType) {
            return ((PercentType) state).doubleValue();
        } else if (state instanceof QuantityType) {
            return ((QuantityType) state).doubleValue();
        } else {
            DecimalType decimalType = state.as(DecimalType.class);
            return decimalType != null ? decimalType.doubleValue() : null;
        }
    }

    public String getStringValue(String itemName) {
        State state = getStateFromItem(itemName);
        if (state == null) {
            return null;
        }
        StringType stringType = state.as(StringType.class);
        logDebug("Got state: {} stringType: {} fullString: {} item: {}", state, stringType, state.toFullString(),
                itemName);
        return stringType == null ? state.toFullString() : stringType.toString();
    }

    public Integer getStateFromItemAsInt(String itemName) {
        State state = getStateFromItem(itemName);
        if (state == null) {
            return null;
        }
        if (state instanceof PercentType) {
            return ((PercentType) state).intValue();
        } else {
            DecimalType decimalType = state.as(DecimalType.class);
            return decimalType != null ? decimalType.intValue() : null;
        }
    }

    public Date getStateFromItemAsDate(String itemName) {
        ZonedDateTime zonedDateTime = getStateFromItemAsZonedDateTime(itemName);
        return zonedDateTime != null ? Date.from(zonedDateTime.toInstant()) : null;
    }

    public ZonedDateTime getStateFromItemAsZonedDateTime(String itemName) {
        State state = getStateFromItem(itemName);
        if (state == null) {
            return null;
        }
        DateTimeType dateTimeType = state.as(DateTimeType.class);
        return dateTimeType != null ? dateTimeType.getZonedDateTime() : null;
    }

    public JRuleRawValue getRawValue(String itemName) {
        State state = getStateFromItem(itemName);
        if (state != null) {
            RawType raw = (RawType) state;
            return new JRuleRawValue(raw.getMimeType(), raw.getBytes());
        }
        return null;
    }

    public Set<String> getGroupMemberNames(String groupName) {
        Item item;
        final Set<String> memberNames = new HashSet<>();
        try {
            item = itemRegistry.getItem(groupName);
        } catch (ItemNotFoundException e) {
            logError("Item not found in registry for group: {}", groupName);
            return null;
        }
        if (item instanceof GroupItem) {
            GroupItem g = (GroupItem) item;
            Set<@NonNull Item> members = g.getMembers();
            members.forEach(m -> memberNames.add(m.getName()));
        }
        return memberNames;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    private void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, LOG_NAME_EVENT, message, parameters);
    }

    private void logInfo(String message, Object... parameters) {
        JRuleLog.info(logger, LOG_NAME_EVENT, message, parameters);
    }

    @SuppressWarnings("unused")
    private void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, LOG_NAME_EVENT, message, parameters);
    }

    private void logError(String message, Object... parameters) {
        JRuleLog.error(logger, LOG_NAME_EVENT, message, parameters);
    }
}
