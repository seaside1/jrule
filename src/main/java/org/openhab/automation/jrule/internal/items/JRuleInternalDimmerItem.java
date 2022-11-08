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
package org.openhab.automation.jrule.internal.items;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

/**
 * The {@link JRuleInternalDimmerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalDimmerItem extends JRuleInternalItem<JRulePercentValue> implements JRuleDimmerItem {

    protected JRuleInternalDimmerItem(String itemName) {
        super(itemName);
    }

    public int getState() {
        return JRuleEventHandler.get().getStateFromItemAsInt(name);
    }

    public void sendCommand(JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(name, command);
    }

    public void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleEventHandler.get().sendCommand(name, command);
    }

    public void sendCommand(int command) {
        JRuleEventHandler.get().sendCommand(name, new JRulePercentValue(command));
    }

    public void postUpdate(JRuleOnOffValue value) {
        JRuleEventHandler.get().postUpdate(name, value);
    }

    public void postUpdate(int value) {
        JRuleEventHandler.get().postUpdate(name, new JRulePercentValue(value));
    }

    public Optional<Integer> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(Integer::parseInt);
    }
}
