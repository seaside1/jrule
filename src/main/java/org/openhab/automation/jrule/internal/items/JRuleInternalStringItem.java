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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

/**
 * The {@link JRuleInternalStringItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalStringItem extends JRuleInternalItem<JRuleStringValue> implements JRuleStringItem {

    public JRuleInternalStringItem(String itemName) {
        super(itemName);
    }

    // Persistence methods
    public Optional<JRuleStringValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(JRuleStringValue::getValueFromString);
    }

    @Override
    public JRuleStringValue getState() {
        return new JRuleStringValue(JRuleEventHandler.get().getStringValue(getName()));
    }
}
