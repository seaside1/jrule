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

import java.util.Set;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;

/**
 * The {@link JRuleGroupItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleGroupItem extends JRuleItem {

    private final String itemName;

    private JRuleGroupItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleGroupItem forName(String itemName) {
        return new JRuleGroupItem(itemName);
    }

    public Set<String> members() {
        return JRuleEventHandler.get().getGroupMemberNames(itemName);
    }

    public void sendCommandToAll(String value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> sendCommand(m, value));
    }

    public String getState() {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    public JRuleOnOffValue getStateAsOnOffValue() {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public JRulePlayPauseValue getStateAsPlayPauseValue() {
        return JRuleEventHandler.get().getPauseValue(itemName);
    }

    public JRuleUpDownValue getStateAsUpDownValue() {
        return JRuleEventHandler.get().getUpDownValue(itemName);
    }

    public void sendCommand(JRulePlayPauseValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void sendCommand(JRuleOnOffValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void sendCommand(JRuleUpDownValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void sendCommand(String value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void postUpdate(JRulePlayPauseValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void postUpdate(JRuleOnOffValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void postUpdate(JRuleUpDownValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public static Set<String> members(String groupName) {
        return JRuleEventHandler.get().getGroupMemberNames(groupName);
    }

    public static void sendCommandToAll(String groupName, String value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(groupName);
        groupMemberNames.forEach(m -> sendCommand(m, value));
    }

    public static String getState(String itemName) {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    public static JRuleOnOffValue getStateAsOnOffValue(String itemName) {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public static JRulePlayPauseValue getStateAsPlayPauseValue(String itemName) {
        return JRuleEventHandler.get().getPauseValue(itemName);
    }

    public static JRuleUpDownValue getStateAsUpDownValue(String itemName) {
        return JRuleEventHandler.get().getUpDownValue(itemName);
    }

    public static void sendCommand(String itemName, JRulePlayPauseValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void sendCommand(String itemName, JRuleOnOffValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void sendCommand(String itemName, JRuleUpDownValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void sendCommand(String itemName, String value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void postUpdate(String itemName, String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public static void postUpdate(String itemName, JRulePlayPauseValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public static void postUpdate(String itemName, JRuleOnOffValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public static void postUpdate(String itemName, JRuleUpDownValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }
}
