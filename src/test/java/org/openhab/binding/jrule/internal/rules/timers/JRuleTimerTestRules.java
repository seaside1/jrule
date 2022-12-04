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

import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
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
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "timers")
    public void testTimers() {
        final AtomicInteger repeatingCounter = new AtomicInteger(0);
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);
        stringItem.sendCommand("command");
        cancelTimer("NON_EXISTING_TIMER");
        createRepeatingTimer("REPEATING_TIMER", Duration.ofSeconds(1), 1, () -> logInfo("Repeating timer completed"));
        createOrReplaceTimer("CREATE_OR_REPLACE_TIMER", Duration.ofSeconds(1),
                () -> logInfo("Replaced timer completed"));
        createTimer(null, Duration.ofSeconds(1), () -> {
            logInfo("log something");
            stringItem.sendCommand("unique timer");
        });
        createRepeatingTimer(null, Duration.ofMillis(10), 10,
                () -> stringItem.sendCommand("repeating-" + String.valueOf(repeatingCounter.incrementAndGet())));

        JRuleTimerHandler.JRuleTimer timer = createTimer("TimerName", Duration.ofMillis(500), () -> {
            stringItem.sendCommand("timedCommand");
            createTimer("NestedTimer", Duration.ofMillis(500), () -> {
                stringItem.sendCommand("nestedTimedCommand");
            });
        });
        stringItem.sendCommand("timer2 running " + timer.isRunning());
        stringItem.sendCommand("timer2 done " + timer.isDone());

        final AtomicInteger chainedCounter = new AtomicInteger(0);
        createTimer("inner-1", Duration.ofSeconds(1), () -> {
            logInfo("calling inner-1");
            chainedCounter.incrementAndGet();
        }).createTimerAfter("inner-2", Duration.ofMillis(500), () -> {
            logInfo("calling inner-2");
            chainedCounter.incrementAndGet();
        }).createTimerAfter("inner-3", Duration.ofMillis(200), () -> {
            logInfo("calling inner-3");
            stringItem.sendCommand("after the other one " + chainedCounter.incrementAndGet());
        });

        JRuleTimerHandler.JRuleTimer canceledTimer = createTimer(null, Duration.ofSeconds(1),
                () -> stringItem.sendCommand("canceled timer"));
        canceledTimer.cancel();
    }

    @JRuleName("Rule name")
    @JRuleLogName("Rule log name")
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "locks")
    public void testLocks() throws InterruptedException {
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);

        boolean first = getTimedLock("MyLock", Duration.ofSeconds(1));
        stringItem.sendCommand("first: " + first);

        boolean second = getTimedLock("MyLock", Duration.ofSeconds(1));
        stringItem.sendCommand("second: " + second);

        Thread.sleep(1100);

        boolean third = getTimedLock("MyLock", Duration.ofSeconds(1));
        stringItem.sendCommand("third: " + third);
    }
}
