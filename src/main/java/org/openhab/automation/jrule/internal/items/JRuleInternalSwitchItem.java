/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.internal.items;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * The {@link JRuleInternalSwitchItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalSwitchItem extends JRuleInternalItem<JRuleOnOffValue> implements JRuleSwitchItem {

    protected JRuleInternalSwitchItem(String itemName) {
        super(itemName);
    }

    @Override
    public Optional<JRuleOnOffValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(JRuleOnOffValue::getValueFromString);
    }

    @Override
    public JRuleOnOffValue getState() {
        return JRuleEventHandler.get().getOnOffValue(getName());
    }
}
