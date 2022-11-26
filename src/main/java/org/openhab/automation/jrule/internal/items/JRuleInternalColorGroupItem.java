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

import org.openhab.automation.jrule.items.JRuleColorGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleInternalColorGroupItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalColorGroupItem extends JRuleInternalColorItem implements JRuleColorGroupItem {

    public JRuleInternalColorGroupItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(int value) {
        memberItems().forEach(i -> i.sendCommand(new JRulePercentValue(value)));
    }

    public void postUpdate(int value) {
        memberItems().forEach(i -> i.postUpdate(new JRulePercentValue(value)));
    }

    public void sendCommand(boolean command) {
        memberItems().forEach(i -> i.sendCommand(JRuleOnOffValue.valueOf(command)));
    }

    public void postUpdate(boolean command) {
        memberItems().forEach(i -> i.postUpdate(JRuleOnOffValue.valueOf(command)));
    }
}
