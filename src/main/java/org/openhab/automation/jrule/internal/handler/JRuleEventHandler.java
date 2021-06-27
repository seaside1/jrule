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
package org.openhab.automation.jrule.internal.handler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.items.JRulePercentType;
import org.openhab.automation.jrule.rules.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.JRulePlayPauseValue;
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
import org.openhab.core.library.types.OnOffType;
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

    private static JRuleEventHandler instance;

    private EventPublisher eventPublisher;

    private ItemRegistry itemRegistry;

    private final Logger logger = LoggerFactory.getLogger(JRuleEventHandler.class);

    private JRuleEventHandler() {
    }

    public static JRuleEventHandler get() {
        if (instance == null) {
            instance = new JRuleEventHandler();
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

    public void sendCommand(String itemName, Command command) {
        if (eventPublisher == null) {
            return;
        }
        logger.info("SendCommand itemName: {} command: {}", itemName, command);
        final ItemCommandEvent commandEvent = ItemEventFactory.createCommandEvent(itemName, command);
        eventPublisher.post(commandEvent);
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

    public JRuleOnOffValue getOnOffValue(String itemName) {
        State state = getStateFromItem(itemName);
        return getOnOffValueFromState(state);
    }

    private JRulePlayPauseValue getPlayPauseValueFromState(State state) {
        PlayPauseType playPauseType = PlayPauseType.valueOf(state.toFullString());
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
        Item item = null;
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
            members.stream().forEach(m -> memberNames.add(m.getName()));
        }
        return memberNames;
    }
}
