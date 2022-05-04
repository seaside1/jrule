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
package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * The {@link JRuleDimmerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleDimmerItem extends JRuleItem {

    public static final String TRIGGER_RECEIVED_UPDATE_ON = "received update ON";
    public static final String TRIGGER_RECEIVED_UPDATE_OFF = "received update OFF";
    public static final String TRIGGER_RECEIVED_COMMAND_ON = "received command ON";
    public static final String TRIGGER_RECEIVED_COMMAND_OFF = "received command OFF";
    public static final String TRIGGER_CHANGED_FROM_ON_TO_OFF = "Changed from ON to OFF";
    public static final String TRIGGER_CHANGED_FROM_ON = "Changed from ON";
    public static final String TRIGGER_CHANGED_FROM_OFF = "Changed from OFF";
    public static final String TRIGGER_CHANGED_TO_OFF = "Changed to OFF";
    public static final String TRIGGER_CHANGED_TO_ON = "Changed to ON";
    public static final String TRIGGER_CHANGED_FROM_OFF_TO_ON = "Changed from OFF to ON";

    private final String itemName;

    private JRuleDimmerItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleDimmerItem forName(String itemName) {
        return new JRuleDimmerItem(itemName);
    }

    public int getItemState() {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public void sendItemCommand(JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendItemCommand(JRuleIncreaseDecreaseValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendItemCommand(int value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRulePercentType(value));
    }

    public void postItemUpdate(JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public void postItemUpdate(int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }

    public static int getState(String itemName) {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public static void sendCommand(String itemName, JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public static void sendCommand(String itemName, JRuleIncreaseDecreaseValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public static void sendCommand(String itemName, int value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRulePercentType(value));
    }

    public static void postUpdate(String itemName, JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public static void postUpdate(String itemName, int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }
}
