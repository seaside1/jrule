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
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleSwitchTrigger;

/**
 * The {@link JRuleGroupSwitchItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleGroupSwitchItem extends JRuleGroupItem implements JRuleSwitchTrigger {

    protected JRuleGroupSwitchItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupSwitchItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleGroupSwitchItem.class);
    }

    public JRuleOnOffValue getState() {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public void sendCommand(JRuleOnOffValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(JRuleOnOffValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    // Persistence method
    public Optional<JRuleOnOffValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(itemName, timestamp, persistenceServiceId)
                .map(s -> JRuleEventHandler.get().getOnOffValue(s));
    }
}
