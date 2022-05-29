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
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;

/**
 * The {@link JRuleGroupRollershutterItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleGroupRollershutterItem extends JRuleGroupItem {

    protected JRuleGroupRollershutterItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupRollershutterItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleGroupRollershutterItem.class);
    }

    public Double getState() {
        return JRuleEventHandler.get().getStateFromItemAsDouble(itemName);
    }

    public void sendCommand(double value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(double value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void sendCommand(int value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(int value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void sendCommand(JRuleUpDownValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(JRuleUpDownValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    // Persistence method
    public Double getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {

        String string = JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId);
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            // TODO add logging
            return null;
        }
    }
}
