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
import java.util.Date;
import java.util.Set;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;

/**
 * The {@link JRuleGroupDateTimeItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleGroupDateTimeItem extends JRuleGroupItem {

    protected JRuleGroupDateTimeItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupDateTimeItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleGroupDateTimeItem.class);
    }

    public Date getState() {
        return JRuleEventHandler.get().getStateFromItemAsDate(itemName);
    }

    public void sendCommand(Date value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(Date value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    // Persistence method
    public String getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        // TODO should return as Date
        return JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId);
    }
}
