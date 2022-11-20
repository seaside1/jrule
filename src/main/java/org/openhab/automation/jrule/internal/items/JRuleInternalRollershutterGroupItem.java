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
import org.openhab.automation.jrule.items.JRuleRollershutterGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleStopMoveValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;

/**
 * The {@link JRuleInternalRollershutterGroupItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalRollershutterGroupItem extends JRuleInternalRollershutterItem
        implements JRuleRollershutterGroupItem {

    public JRuleInternalRollershutterGroupItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(double value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, new JRuleDecimalValue(value)));
    }

    public void postUpdate(double value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, new JRuleDecimalValue(value)));
    }

    public void sendCommand(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, new JRuleDecimalValue(value)));
    }

    public void postUpdate(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, new JRuleDecimalValue(value)));
    }

    public void sendCommand(JRuleUpDownValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(JRuleUpDownValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    public void sendCommand(JRuleStopMoveValue value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name, false);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }
}
