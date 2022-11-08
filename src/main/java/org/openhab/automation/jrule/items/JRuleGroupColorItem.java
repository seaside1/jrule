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
import java.util.Optional;
import java.util.Set;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.group.JRuleInternalGroupItem;
import org.openhab.automation.jrule.internal.items.JRulePersistenceExtensions;
import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleGroupColorItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleGroupColorItem extends JRuleInternalGroupItem {

    protected JRuleGroupColorItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupColorItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleGroupColorItem.class);
    }

    public JRuleColorValue getState() {
        return JRuleEventHandler.get().getColorValue(name);
    }

    public JRuleOnOffValue getOnOffState() {
        return JRuleEventHandler.get().getOnOffValue(name);
    }

    public int getPercentState() {
        return JRuleEventHandler.get().getStateFromItemAsInt(name);
    }

    public void sendCommand(JRuleColorValue colorValue) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, colorValue));
    }

    public void sendCommand(JRuleOnOffValue command) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, command));
    }

    public void sendCommand(JRuleIncreaseDecreaseValue command) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, command));
    }

    public void sendCommand(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, new JRulePercentValue(value)));
    }

    public void postUpdate(JRuleColorValue colorValue) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, colorValue));
    }

    public void postUpdate(JRuleOnOffValue state) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, state));
    }

    public void postUpdate(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, new JRulePercentValue(value)));
    }

    // Persistence method
    public Optional<String> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId);
    }
}
