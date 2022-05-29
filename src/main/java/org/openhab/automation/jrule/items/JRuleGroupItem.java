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

import java.time.ZonedDateTime;
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

    protected JRuleGroupItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleGroupItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleGroupItem.class);
    }

    public Set<String> members() {
        return JRuleEventHandler.get().getGroupMemberNames(itemName);
    }

    public void sendCommandToAll(String value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
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

    // Persistence method
    public String getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        // TODO Groups should have a type registered
        return JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId);
    }
}
