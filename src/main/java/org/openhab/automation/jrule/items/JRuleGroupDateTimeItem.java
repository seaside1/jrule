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
import java.util.Set;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.group.JRuleInternalGroupItem;
import org.openhab.automation.jrule.internal.items.JRulePersistenceExtensions;

/**
 * The {@link JRuleGroupDateTimeItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleGroupDateTimeItem extends JRuleInternalGroupItem {

    protected JRuleGroupDateTimeItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupDateTimeItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleGroupDateTimeItem.class);
    }

    public ZonedDateTime getState() {
        return JRuleEventHandler.get().getStateFromItemAsZonedDateTime(name);
    }

    public void sendCommand(ZonedDateTime value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(ZonedDateTime value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    // Persistence method
    public Optional<ZonedDateTime> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(ZonedDateTime::parse);
    }
}
