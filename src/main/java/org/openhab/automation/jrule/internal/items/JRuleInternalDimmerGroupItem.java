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

import org.openhab.automation.jrule.items.JRuleDimmerGroupItem;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleInternalColorGroupItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalDimmerGroupItem extends JRuleInternalDimmerItem implements JRuleDimmerGroupItem {

    private static final String LOG_NAME = "JRuleGroupDimmerItem";
    private static final Logger logger = LoggerFactory.getLogger(JRuleInternalDimmerGroupItem.class);

    public JRuleInternalDimmerGroupItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(double value) {
        memberItems().forEach(i -> i.sendCommand(new JRulePercentValue(value)));
    }

    public void postUpdate(double value) {
        memberItems().forEach(i -> i.postUpdate(new JRulePercentValue(value)));
    }

    public void sendCommand(int value) {
        memberItems().forEach(i -> i.sendCommand(new JRulePercentValue(value)));
    }

    public void postUpdate(int value) {
        memberItems().forEach(i -> i.postUpdate(new JRulePercentValue(value)));
    }

    public void sendCommand(boolean command) {
        memberItems().forEach(i -> i.sendCommand(new JRulePercentValue(command)));
    }

    public void postUpdate(boolean value) {
        memberItems().forEach(i -> i.postUpdate(new JRulePercentValue(value)));
    }
}
