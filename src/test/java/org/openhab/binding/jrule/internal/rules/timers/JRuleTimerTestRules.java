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
package org.openhab.binding.jrule.internal.rules.timers;

import org.openhab.automation.jrule.internal.items.JRuleInternalStringItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleLogName;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;

/**
 * Rule used in testcase JRuleTimerTest
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleTimerTestRules extends JRule {

    public static final String TRIGGER_ITEM = "triggerItem";
    public static final String TARGET_ITEM = "targetItem";

    @JRuleName("Rule name")
    @JRuleLogName("Rule log name")
    @JRuleWhenItemChange(item = TRIGGER_ITEM)
    public void testSendCommand() {
        JRuleInternalStringItem.forName(TARGET_ITEM).sendCommand("command");
        cancelTimer("NON_EXISTING_TIMER");
        createRepeatingTimer("REPEATING_TIMER", 1, 1, unused -> logInfo("Repeating timer completed"));
        createOrReplaceTimer("CREATE_OR_REPLACE_TIMER", 1, unused -> logInfo("Replaced timer completed"));

        createTimer("TimerName", 1, unused -> {
            JRuleInternalStringItem.forName(TARGET_ITEM).sendCommand("timedCommand");
            createTimer("NestedTimer", 1, unused2 -> {
                JRuleInternalStringItem.forName(TARGET_ITEM).sendCommand("nestedTimedCommand");
            });
        });
    }
}
