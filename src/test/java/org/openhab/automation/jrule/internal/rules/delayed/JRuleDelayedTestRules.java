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
package org.openhab.automation.jrule.internal.rules.delayed;

import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleDelayed;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;

/**
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleDelayedTestRules extends JRule {

    public static final String TRIGGER_ITEM = "trigger";
    public static final String TARGET_ITEM = "target";

    @JRuleName("Rule name")
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "2s")
    @JRuleDelayed(value = 2)
    public void test2s() {
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);
        stringItem.sendCommand("2s");
    }
}
