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
import java.util.Set;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleDateTimeGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;

/**
 * The {@link JRuleInternalDateTimeGroupItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalDateTimeGroupItem extends JRuleInternalDateTimeItem implements JRuleDateTimeGroupItem {

    public JRuleInternalDateTimeGroupItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(ZonedDateTime value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, new JRuleDateTimeValue(value)));
    }

    public void postUpdate(ZonedDateTime value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, new JRuleDateTimeValue(value)));
    }
}
