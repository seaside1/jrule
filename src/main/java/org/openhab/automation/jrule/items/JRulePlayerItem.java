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
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;

/**
 * The {@link JRulePlayerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRulePlayerItem extends JRuleItem {

    protected JRulePlayerItem(String itemName) {
        super(itemName);
    }

    public static JRulePlayerItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRulePlayerItem.class);
    }

    public JRulePlayPauseValue getState() {
        return JRuleEventHandler.get().getPauseValue(itemName);
    }

    public void sendCommand(JRulePlayPauseValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void postUpdate(JRulePlayPauseValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    // Persistence method
    public JRulePlayPauseValue getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePlayPauseValue
                .valueOf(JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
    }
}
