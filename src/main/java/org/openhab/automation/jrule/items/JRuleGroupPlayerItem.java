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
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.trigger.JRulePlayerTrigger;
import org.openhab.core.library.types.PlayPauseType;

/**
 * The {@link JRuleGroupPlayerItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleGroupPlayerItem extends JRuleInternalGroupItem implements JRulePlayerTrigger {

    protected JRuleGroupPlayerItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupPlayerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleGroupPlayerItem.class);
    }

    public JRulePlayPauseValue getState() {
        return JRuleEventHandler.get().getPauseValue(name);
    }

    public void sendCommand(JRulePlayPauseValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    public void postUpdate(JRulePlayPauseValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    // Persistence method
    public Optional<JRulePlayPauseValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(name -> JRuleEventHandler.get().getPlayPauseValueFromState(PlayPauseType.valueOf(name)));
    }
}
