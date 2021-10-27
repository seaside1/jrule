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
package org.openhab.automation.jrule.internal.handler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.items.JRulePercentType;
import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleRgbValue;
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
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEventHandler} is responsible for handling commands and status
 * updates for JRule
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleEventHandler {

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

    public void sendCommand(String itemName, JRuleOnOffValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, JRuleIncreaseDecreaseValue command) {
        sendCommand(itemName, getCommand(command));
    }

    public void sendCommand(String itemName, String command) {
        sendCommand(itemName, new StringType(command));
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

    public void sendCommand(String itemName, JRuleColorValue colorValue) {
        final HSBType hsbType = getHsbType(colorValue);
        if (hsbType == null) {
            logger.error("Failed to sen command for colorValue: {}", colorValue);
            return;
        }
        sendCommand(itemName, hsbType);
    }

    private HSBType getHsbType(JRuleColorValue colorValue) {
        final JRuleHsbValue hsbValue = colorValue.getHsbValue();
        if (hsbValue != null) {
            return new HSBType(new DecimalType(hsbValue.getHue()), new PercentType(hsbValue.getSaturation()),
                    new PercentType(hsbValue.getBrightness()));
        }

        final JRuleRgbValue rgbValue = colorValue.getRgbValue();
        if (rgbValue != null) {
            return HSBType.fromRGB(rgbValue.getRed(), rgbValue.getGreen(), rgbValue.getBlue());
        }

        final JRuleXyValue xyValue = colorValue.getXyValue();
        if (xyValue != null) {
            return HSBType.fromXY(xyValue.getX(), xyValue.getY());
        }

        return null;
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
        logger.info("SendCommand itemName: {} command: {}", itemName, command);
        final ItemCommandEvent commandEvent = ItemEventFactory.createCommandEvent(itemName, command);
        eventPublisher.post(commandEvent);
    }

    public void postUpdate(String itemName, JRuleColorValue colorValue) {
        final HSBType hsbType = getHsbType(colorValue);
        if (hsbType == null) {
            logger.error("Failed to get HSB Type from ColorValue: {}", colorValue);
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

    public void postUpdate(String itemName, JRulePercentType value) {
        postUpdate(itemName, new PercentType(value.getValue()));
    }

    public void postUpdate(String itemName, JRulePlayPauseValue state) {
        postUpdate(itemName, getStateFromValue(state));
    }

    public void postUpdate(String itemName, JRuleOnOffValue state) {
        postUpdate(itemName, getStateFromValue(state));
    }

    public void postUpdate(String itemName, String value) {
        postUpdate(itemName, new StringType(value));
    }

    public void postUpdate(String itemName, double value) {
        postUpdate(itemName, new DecimalType(value));
    }

    public void postUpdate(String itemName, State state) {
        if (eventPublisher == null) {
            return;
        }
        final ItemEvent itemEvent = ItemEventFactory.createStateEvent(itemName, state);
        eventPublisher.post(itemEvent);
    }

    public JRulePlayPauseValue getPauseValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getPlayPauseValueFromState(state);
    }

    public JRuleColorValue getColorValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getColorValueFromState(state);
    }

    public JRuleOnOffValue getOnOffValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getOnOffValueFromState(state);
    }

    public JRuleOpenClosedValue getOpenClosedValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getOpenClosedValueFromState(state);
    }

    private JRuleColorValue getColorValueFromState(State state) {
        HSBType hsbValue = null;
        try {
            hsbValue = HSBType.valueOf(state.toFullString());
        } catch (IllegalArgumentException x) {
            logger.error("Failed to parse state: {}", state.toFullString());
            return null;
        }
        final JRuleHsbValue jRuleHsbValue = new JRuleHsbValue(hsbValue.getHue().intValue(),
                hsbValue.getSaturation().intValue(), hsbValue.getBrightness().intValue());
        final JRuleRgbValue jRuleRgbValue = new JRuleRgbValue(hsbValue.getRed().intValue(),
                hsbValue.getGreen().intValue(), hsbValue.getBlue().intValue());
        PercentType[] xyY = hsbValue.toXY();
        final JRuleXyValue jRuleXyValue = new JRuleXyValue(xyY[0].floatValue(), xyY[1].floatValue(),
                xyY[2].floatValue());

        return new JRuleColorValue(jRuleHsbValue, jRuleRgbValue, jRuleXyValue);
    }

    private JRulePlayPauseValue getPlayPauseValueFromState(State state) {
        final PlayPauseType playPauseType = PlayPauseType.valueOf(state.toFullString());
        switch (playPauseType) {
            case PLAY:
                return JRulePlayPauseValue.PLAY;
            case PAUSE:
                return JRulePlayPauseValue.PAUSE;
            default:
                logger.error("Fail to transform switch value");
                return JRulePlayPauseValue.UNDEF;
        }
    }

    private JRuleOnOffValue getOnOffValueFromState(State state) {
        OnOffType onOffType = OnOffType.from(state.toFullString());
        switch (onOffType) {
            case OFF:
                return JRuleOnOffValue.OFF;
            case ON:
                return JRuleOnOffValue.ON;
            default:
                logger.error("Fail to transform switch value");
                return JRuleOnOffValue.UNDEF;
        }
    }

    private JRuleOpenClosedValue getOpenClosedValueFromState(State state) {
        OpenClosedType openClosedType = OpenClosedType.valueOf(state.toFullString());
        switch (openClosedType) {
            case OPEN:
                return JRuleOpenClosedValue.OPEN;
            case CLOSED:
                return JRuleOpenClosedValue.CLOSED;
            default:
                logger.error("Fail to transform switch value");
                return JRuleOpenClosedValue.UNDEF;
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
            logger.error("Failed to find item: {}", itemName);
            return null;
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
                logger.error("Unhandled getCommand: {}", value);
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
                logger.error("Unhandled getCommand: {}", value);
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
                logger.error("Unhandled getCommand: {}", command);
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
                logger.error("Unhandled getCommand: {}", command);
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
                logger.error("Unhandled getCommand: {}", command);
                return null;
        }
    }

    public void setItemRegistry(@NonNull ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public Double getStateFromItemAsDouble(String name) {
        State state = getStateFromItem(name);
        if (state == null) {
            return null;
        }
        DecimalType decimalType = state.as(DecimalType.class);
        return decimalType != null ? decimalType.doubleValue() : null;
    }

    public String getStringValue(String itemName) {
        State state = getStateFromItem(itemName);
        if (state == null) {
            return null;
        }
        StringType stringType = state.as(StringType.class);
        logger.debug("Got state: {} stringType: {} fullString: {}", state, stringType, state.toFullString());
        return stringType == null ? state.toFullString() : stringType.toString();
    }

    public Integer getStateFromItemAsInt(String itemName) {
        State state = getStateFromItem(itemName);
        if (state == null) {
            return null;
        }
        DecimalType decimalType = state.as(DecimalType.class);
        return decimalType != null ? decimalType.intValue() : null;
    }

    public Date getStateFromItemAsDate(String itemName) {
        State state = getStateFromItem(itemName);
        if (state == null) {
            return null;
        }
        DateTimeType dateTimeType = state.as(DateTimeType.class);
        return dateTimeType != null ? Date.from(dateTimeType.getZonedDateTime().toInstant()) : null;
    }

    public Set<String> getGroupMemberNames(String groupName) {
        Item item;
        final Set<String> memberNames = new HashSet<>();
        try {
            item = itemRegistry.getItem(groupName);
        } catch (ItemNotFoundException e) {
            logger.error("Item not found in registry for group: {}", groupName);
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
}
