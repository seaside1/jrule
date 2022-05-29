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
import org.openhab.automation.jrule.rules.value.JRuleStopMoveValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;

/**
 * The {@link JRuleRollershutterItem} Items
 *
 * @author Timo Litzius - Initial contribution
 */
public class JRuleRollershutterItem extends JRuleItem {

    protected JRuleRollershutterItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleRollershutterItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleRollershutterItem.class);
    }

    public int getState() {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public void sendCommand(JRuleUpDownValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendCommand(JRuleStopMoveValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendCommand(int value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRulePercentType(value));
    }

    public void postUpdate(JRuleUpDownValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public void postUpdate(int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }

    // Persistence methods
    public int getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return Integer.parseInt(JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
    }
}
