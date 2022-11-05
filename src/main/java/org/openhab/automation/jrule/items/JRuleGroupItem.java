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

import java.util.Set;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;

/**
 * The {@link JRuleGroupItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleGroupItem extends JRuleItem {

    protected JRuleGroupItem(String itemName) {
        super(itemName);
    }

    public Set<String> members() {
        return JRuleEventHandler.get().getGroupMemberNames(itemName);
    }

    public Set<JRuleItem> memberItems() {
        return JRuleEventHandler.get().getGroupMemberItems(itemName);
    }

    public void sendCommand(String value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(String value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(itemName);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }
}
