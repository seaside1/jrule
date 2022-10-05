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

/**
 * The {@link JRuleStringItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleStringItem extends JRuleItem {

    protected JRuleStringItem(String itemName) {
        super(itemName);
    }

    public static JRuleStringItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleStringItem.class);
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
    public Optional<String> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(itemName, timestamp, persistenceServiceId);
    }
}
