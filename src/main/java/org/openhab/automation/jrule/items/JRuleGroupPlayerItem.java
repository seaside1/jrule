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
import org.openhab.automation.jrule.trigger.JRulePlayerTrigger;
import org.openhab.core.library.types.PlayPauseType;

/**
 * The {@link JRuleGroupPlayerItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleGroupPlayerItem extends JRuleGroupItem implements JRulePlayerTrigger {

    protected JRuleGroupPlayerItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupPlayerItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleGroupPlayerItem.class);
    }

    public JRulePlayPauseValue getState() {
        return JRuleEventHandler.get().getPauseValue(itemName);
    }

    public void sendCommand(JRulePlayPauseValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(JRulePlayPauseValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    // Persistence method
    public JRulePlayPauseValue getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {

        String string = JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId);
        return JRuleEventHandler.get().getPlayPauseValueFromState(PlayPauseType.valueOf(string));
    }
}
