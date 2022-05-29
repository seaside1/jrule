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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * The {@link JRuleGroupSwitchItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleGroupSwitchItem extends JRuleGroupItem {

    protected JRuleGroupSwitchItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupSwitchItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleGroupSwitchItem.class);
    }

    public JRuleOnOffValue getState() {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public void sendCommand(JRuleOnOffValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(JRuleOnOffValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    // Persistence method
    public JRuleOnOffValue getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRuleEventHandler.get()
                .getOnOffValue(JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
    }
}
