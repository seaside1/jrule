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
import org.openhab.automation.jrule.items.JRuleImageItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

/**
 * The {@link JRuleInternalImageItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleInternalImageItem extends JRuleInternalItem<JRuleRawValue> implements JRuleImageItem {

    protected JRuleInternalImageItem(String itemName) {
        super(itemName);
    }

    public static JRuleInternalImageItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalImageItem.class);
    }

    public JRuleRawValue getState() {
        return JRuleEventHandler.get().getRawValue(name);
    }

    public void postUpdate(JRuleRawValue value) {
        JRuleEventHandler.get().postUpdate(name, value);
    }

    // Persistence methods
    public Optional<JRuleRawValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicStateAsRawValue(name, timestamp, persistenceServiceId);
    }
}
