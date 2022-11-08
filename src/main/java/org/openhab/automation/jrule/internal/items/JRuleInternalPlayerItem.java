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
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.trigger.JRulePlayerTrigger;

/**
 * The {@link JRuleInternalPlayerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleInternalPlayerItem extends JRuleInternalItem<JRulePlayPauseValue> implements JRulePlayerItem {

    protected JRuleInternalPlayerItem(String itemName) {
        super(itemName);
    }

    public static JRuleInternalPlayerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalPlayerItem.class);
    }

    public JRulePlayPauseValue getState() {
        return JRuleEventHandler.get().getPauseValue(name);
    }

    public void sendCommand(JRulePlayPauseValue command) {
        JRuleEventHandler.get().sendCommand(name, command);
    }

    public void postUpdate(JRulePlayPauseValue state) {
        JRuleEventHandler.get().postUpdate(name, state);
    }

    // Persistence method
    public Optional<JRulePlayPauseValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(JRulePlayPauseValue::valueOf);
    }
}
