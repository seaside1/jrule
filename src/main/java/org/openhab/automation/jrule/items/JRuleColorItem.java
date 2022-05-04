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
import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * The {@link JRuleColorItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleColorItem extends JRuleItem {

    private final String itemName;

    private JRuleColorItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleColorItem forName(String itemName) {
        return new JRuleColorItem(itemName);
    }

    public JRuleColorValue getState() {
        return JRuleEventHandler.get().getColorValue(itemName);
    }

    public JRuleOnOffValue getOnOffState() {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public int getPercentState() {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public void sendCommand(JRuleColorValue colorValue) {
        JRuleEventHandler.get().sendCommand(itemName, colorValue);
    }

    public void sendCommand(JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendCommand(int value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRulePercentType(value));
    }

    public void postUpdate(JRuleColorValue colorValue) {
        JRuleEventHandler.get().postUpdate(itemName, colorValue);
    }

    public void postUpdate(JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public void postUpdate(int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }

    public static JRuleColorValue getState(String itemName) {
        return JRuleEventHandler.get().getColorValue(itemName);
    }

    public static JRuleOnOffValue getOnOffState(String itemName) {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public static int getPercentState(String itemName) {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public static void sendCommand(String itemName, JRuleColorValue colorValue) {
        JRuleEventHandler.get().sendCommand(itemName, colorValue);
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

    public static void postUpdate(String itemName, JRuleColorValue colorValue) {
        JRuleEventHandler.get().postUpdate(itemName, colorValue);
    }

    public static void postUpdate(String itemName, JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public static void postUpdate(String itemName, int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }
}
