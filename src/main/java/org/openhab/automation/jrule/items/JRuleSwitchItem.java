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
import org.openhab.automation.jrule.trigger.JRuleSwitchTrigger;

/**
 * The {@link JRuleSwitchItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleSwitchItem extends JRuleItem implements JRuleSwitchTrigger {

    protected JRuleSwitchItem(String itemName) {
        super(itemName);
    }

    public static JRuleSwitchItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleSwitchItem.class);
    }

    public JRuleOnOffValue getState() {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public void sendCommand(JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void postUpdate(JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    // Persistence method
    public JRuleOnOffValue getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRuleOnOffValue.getValueFromString(
                JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
    }
}
