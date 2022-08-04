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
import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * The {@link JRuleGroupColorItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleGroupColorItem extends JRuleGroupItem {

    protected JRuleGroupColorItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupColorItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleGroupColorItem.class);
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
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, colorValue));
    }

    public void sendCommand(JRuleOnOffValue command) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, command));
    }

    public void sendCommand(JRuleIncreaseDecreaseValue command) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, command));
    }

    public void sendCommand(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, new JRulePercentType(value)));
    }

    public void postUpdate(JRuleColorValue colorValue) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, colorValue));
    }

    public void postUpdate(JRuleOnOffValue state) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, state));
    }

    public void postUpdate(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, new JRulePercentType(value)));
    }

    // Persistence method
    public String getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId);
    }
}
