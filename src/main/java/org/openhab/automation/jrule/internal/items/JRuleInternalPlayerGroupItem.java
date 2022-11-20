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
import org.openhab.automation.jrule.items.JRulePlayerGroupItem;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;

/**
 * The {@link JRuleInternalPlayerGroupItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalPlayerGroupItem extends JRuleInternalPlayerItem implements JRulePlayerGroupItem {

    public JRuleInternalPlayerGroupItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(JRulePlayPauseValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    public void postUpdate(JRulePlayPauseValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }
}
