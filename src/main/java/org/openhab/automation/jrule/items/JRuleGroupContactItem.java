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
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;

/**
 * The {@link JRuleGroupContactItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleGroupContactItem extends JRuleGroupItem {

    protected JRuleGroupContactItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupContactItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleGroupContactItem.class);
    }

    public JRuleOpenClosedValue getState() {
        return JRuleEventHandler.get().getOpenClosedValue(itemName);
    }

    public void sendCommand(JRuleOpenClosedValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(JRuleOpenClosedValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    // Persistence method
    public JRuleOpenClosedValue getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRuleEventHandler.get().getOpenClosedValue(
                JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
    }
}
