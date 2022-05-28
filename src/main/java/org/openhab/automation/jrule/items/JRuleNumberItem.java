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
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * The {@link JRuleNumberItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleNumberItem extends JRuleItem implements JRuleCommonTrigger {

    protected JRuleNumberItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleNumberItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleNumberItem.class);
    }

    public void sendCommand(double value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(double value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void sendCommand(double value, String unit) {
        JRuleEventHandler.get().sendCommand(itemName, value, unit);
    }

    public void postUpdate(double value, String unit) {
        JRuleEventHandler.get().postUpdate(itemName, value, unit);
    }

    public Double getState() {
        return JRuleEventHandler.get().getStateFromItemAsDouble(itemName);
    }

    public Double getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return Double.parseDouble(JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
    }
}
