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

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.openhab.automation.jrule.items.JRuleStringItem;
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
        final AtomicInteger repeatingCounter = new AtomicInteger(0);
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);
        stringItem.sendCommand("command");
        cancelTimer("NON_EXISTING_TIMER");
        createRepeatingTimer("REPEATING_TIMER", Duration.ofSeconds(1), 1, () -> logInfo("Repeating timer completed"));
        createOrReplaceTimer("CREATE_OR_REPLACE_TIMER", Duration.ofSeconds(1),
                () -> logInfo("Replaced timer completed"));
        createTimer(null, Duration.ofSeconds(1), () -> stringItem.sendCommand("unique timer"));
        createRepeatingTimer(null, Duration.ofMillis(10), 10,
                () -> stringItem.sendCommand("repeating-" + String.valueOf(repeatingCounter.incrementAndGet())));

        createTimer("TimerName", Duration.ofMillis(500), () -> {
            stringItem.sendCommand("timedCommand");
            createTimer("NestedTimer", Duration.ofMillis(500), () -> {
                stringItem.sendCommand("nestedTimedCommand");
            });
        });
    }
}
