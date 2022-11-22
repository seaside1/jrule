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

import java.util.Set;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleContactGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;

/**
 * The {@link JRuleInternalContactGroupItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalContactGroupItem extends JRuleInternalContactItem implements JRuleContactGroupItem {

    public JRuleInternalContactGroupItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(JRuleOpenClosedValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(JRuleOpenClosedValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    public void sendCommand(boolean command) {
        JRuleEventHandler.get().getGroupMemberNames(name, false)
                .forEach(s -> JRuleEventHandler.get().sendCommand(s, JRuleOnOffValue.valueOf(command)));
    }

    public void postUpdate(boolean command) {
        JRuleEventHandler.get().getGroupMemberNames(name, false)
                .forEach(s -> JRuleEventHandler.get().postUpdate(s, JRuleOnOffValue.valueOf(command)));
    }
}
