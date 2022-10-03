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
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;

/**
 * The {@link JRuleImageItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleImageItem extends JRuleItem {

    protected JRuleImageItem(String itemName) {
        super(itemName);
    }

    public static JRuleImageItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleImageItem.class);
    }

    public JRuleRawValue getState() {
        return JRuleEventHandler.get().getRawValue(itemName);
    }

    public void postUpdate(JRuleRawValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    // Persistence methods
    public Optional<JRuleRawValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicStateAsRawValue(itemName, timestamp, persistenceServiceId);
    }
}
