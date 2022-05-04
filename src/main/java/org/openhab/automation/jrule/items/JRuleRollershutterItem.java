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
import org.openhab.automation.jrule.rules.value.JRuleStopMoveValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;

/**
 * The {@link JRuleRollershutterItem} Items
 *
 * @author Timo Litzius - Initial contribution
 */
public class JRuleRollershutterItem extends JRuleItem {

    private final String itemName;

    private JRuleRollershutterItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleRollershutterItem forName(String itemName) {
        return new JRuleRollershutterItem(itemName);
    }

    public int getItemState() {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public void sendItemCommand(JRuleUpDownValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendItemCommand(JRuleStopMoveValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendItemCommand(int value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRulePercentType(value));
    }

    public void postItemUpdate(JRuleUpDownValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public void postItemUpdate(int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }

    public static int getState(String itemName) {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public static void sendCommand(String itemName, JRuleUpDownValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public static void sendCommand(String itemName, JRuleStopMoveValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public static void sendCommand(String itemName, int value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRulePercentType(value));
    }

    public static void postUpdate(String itemName, JRuleUpDownValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public static void postUpdate(String itemName, int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }
}
