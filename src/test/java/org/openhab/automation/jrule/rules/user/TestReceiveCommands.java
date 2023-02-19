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
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.items.*;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

/**
 * The {@link TestReceiveCommands}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class TestReceiveCommands extends JRule {
    public static final String NAME_RECEIVE_SWITCH_COMMAND = "receive switch command";
    public static final String NAME_RECEIVE_NUMBER_COMMAND = "receive number command";
    public static final String NAME_RECEIVE_STRING_COMMAND = "receive string command";
    public static final String NAME_RECEIVE_DATETIME_COMMAND = "receive datetime command";
    public static final String NAME_RECEIVE_PLAYER_COMMAND = "receive player command";
    public static final String NAME_RECEIVE_CONTACT_COMMAND = "receive contact command";
    public static final String NAME_RECEIVE_IMAGE_COMMAND = "receive image command";
    public static final String NAME_RECEIVE_ROLLERSHUTTER_COMMAND = "receive rollershutter command";
    public static final String NAME_RECEIVE_DIMMER_COMMAND = "receive dimmer command";
    public static final String NAME_RECEIVE_COLOR_COMMAND = "receive color command";
    public static final String NAME_RECEIVE_LOCATION_COMMAND = "receive location command";
    public static final String NAME_RECEIVE_QUANTITY_COMMAND = "receive quantity command";
    public static final String NAME_RECEIVE_SWITCH_UPDATE = "receive switch update";
    public static final String NAME_RECEIVE_NUMBER_UPDATE = "receive number update";
    public static final String NAME_RECEIVE_STRING_UPDATE = "receive string update";
    public static final String NAME_RECEIVE_DATETIME_UPDATE = "receive datetime update";
    public static final String NAME_RECEIVE_PLAYER_UPDATE = "receive player update";
    public static final String NAME_RECEIVE_CONTACT_UPDATE = "receive contact update";
    public static final String NAME_RECEIVE_IMAGE_UPDATE = "receive image update";
    public static final String NAME_RECEIVE_ROLLERSHUTTER_UPDATE = "receive rollershutter update";
    public static final String NAME_RECEIVE_DIMMER_UPDATE = "receive dimmer update";
    public static final String NAME_RECEIVE_COLOR_UPDATE = "receive color update";
    public static final String NAME_RECEIVE_LOCATION_UPDATE = "receive location update";
    public static final String NAME_RECEIVE_QUANTITY_UPDATE = "receive quantity update";

    public static final String ITEM_SWITCH_EVENT = "Switch_Event";
    public static final String ITEM_NUMBER_EVENT = "Number_Event";
    public static final String ITEM_STRING_EVENT = "String_Event";
    public static final String ITEM_DATETIME_EVENT = "DateTime_Event";
    public static final String ITEM_PLAYER_EVENT = "Player_Event";
    public static final String ITEM_CONTACT_EVENT = "Contact_Event";
    public static final String ITEM_IMAGE_EVENT = "Image_Event";
    public static final String ITEM_ROLLERSHUTTER_EVENT = "Rollershutter_Event";
    public static final String ITEM_DIMMER_EVENT = "Dimmer_Event";
    public static final String ITEM_COLOR_EVENT = "Color_Event";
    public static final String ITEM_LOCATION_EVENT = "Location_Event";
    public static final String ITEM_QUANTITY_EVENT = "Quantity_Event";

    @JRuleName(NAME_RECEIVE_SWITCH_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_SWITCH_EVENT)
    public void receiveSwitchCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_NUMBER_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_NUMBER_EVENT)
    public void receiveNumberCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_STRING_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_STRING_EVENT)
    public void receiveStringCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_DATETIME_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_DATETIME_EVENT)
    public void receiveDatetimeCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_PLAYER_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_PLAYER_EVENT)
    public void receivePlayerCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_CONTACT_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_CONTACT_EVENT)
    public void receiveContactCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_IMAGE_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_IMAGE_EVENT)
    public void receiveImageCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_ROLLERSHUTTER_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_ROLLERSHUTTER_EVENT)
    public void receiveRollershutterCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_DIMMER_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_DIMMER_EVENT)
    public void receiveDimmerCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_COLOR_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_COLOR_EVENT)
    public void receiveColorCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_LOCATION_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_LOCATION_EVENT)
    public void receiveLocationCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_QUANTITY_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_QUANTITY_EVENT)
    public void receiveQuantityCommand(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getState(), event.getState().getClass());
    }

    @JRuleName(NAME_RECEIVE_SWITCH_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_SWITCH_EVENT)
    public void receiveSwitchUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleSwitchItem.class).getStateAsOnOff());
    }

    @JRuleName(NAME_RECEIVE_NUMBER_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_NUMBER_EVENT)
    public void receiveNumberUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleNumberItem.class).getStateAsDecimal().floatValue());
    }

    @JRuleName(NAME_RECEIVE_STRING_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_STRING_EVENT)
    public void receiveStringUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleStringItem.class).getStateAsString());
    }

    @JRuleName(NAME_RECEIVE_DATETIME_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_DATETIME_EVENT)
    public void receiveDatetimeUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleDateTimeItem.class).getStateAsDateTime().getValue());
    }

    @JRuleName(NAME_RECEIVE_PLAYER_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_PLAYER_EVENT)
    public void receivePlayerUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRulePlayerItem.class).getStateAsPlayPause());
    }

    @JRuleName(NAME_RECEIVE_CONTACT_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_CONTACT_EVENT)
    public void receiveContactUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleContactItem.class).getStateAsOpenClose());
    }

    @JRuleName(NAME_RECEIVE_IMAGE_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_IMAGE_EVENT)
    public void receiveImageUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleImageItem.class).getStateAsRaw());
    }

    @JRuleName(NAME_RECEIVE_ROLLERSHUTTER_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_ROLLERSHUTTER_EVENT)
    public void receiveRollershutterUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleRollershutterItem.class).getStateAsPercent().floatValue());
    }

    @JRuleName(NAME_RECEIVE_DIMMER_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_DIMMER_EVENT)
    public void receiveDimmerUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleDimmerItem.class).getStateAsPercent().floatValue());
    }

    @JRuleName(NAME_RECEIVE_COLOR_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_COLOR_EVENT)
    public void receiveColorUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleColorItem.class).getStateAsHsb());
    }

    @JRuleName(NAME_RECEIVE_LOCATION_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_LOCATION_EVENT)
    public void receiveLocationUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleLocationItem.class).getStateAsPoint());
    }

    @JRuleName(NAME_RECEIVE_QUANTITY_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_QUANTITY_EVENT)
    public void receiveQuantityUpdate(JRuleItemEvent event) {
        logInfo("received: '{}', type: '{}'", event.getItem().getState(), event.getItem().getState().getClass());
        logInfo("received value: {}", event.getItem(JRuleQuantityItem.class).getStateAsDecimal().floatValue());
    }
}
