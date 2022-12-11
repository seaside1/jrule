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

import org.openhab.automation.jrule.items.JRuleRollershutterGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
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
        memberItems().forEach(i -> i.sendCommand(new JRuleDecimalValue(value)));
    }

    public void postUpdate(double value) {
        memberItems().forEach(i -> i.postUpdate(new JRuleDecimalValue(value)));
    }

    public void sendCommand(int command) {
        memberItems().forEach(i -> i.sendCommand(new JRuleDecimalValue(command)));
    }

    public void postUpdate(int value) {
        memberItems().forEach(i -> i.postUpdate(new JRuleDecimalValue(value)));
    }

    public void sendCommand(boolean command) {
        memberItems().forEach(i -> i.sendCommand(JRuleUpDownValue.valueOf(command)));
    }

    public void postUpdate(boolean value) {
        memberItems().forEach(i -> i.postUpdate(JRuleUpDownValue.valueOf(value)));
    }
}
