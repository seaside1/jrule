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

/**
 * The {@link JRuleCallItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleCallItem extends JRuleItem {

    protected JRuleCallItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleCallItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleCallItem.class);
    }

    public String getState() {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    public void sendCommand(String value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    // Persistence methods
    public String getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId);
    }
}
