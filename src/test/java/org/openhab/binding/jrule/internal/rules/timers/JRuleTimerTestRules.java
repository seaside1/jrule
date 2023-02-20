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
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.*;

/**
 * Rule used in testcase JRuleTimerTest
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleTimerTestRules extends JRule {

    public static final String TRIGGER_ITEM = "triggerItem";
    public static final String TARGET_ITEM = "targetItem";
    public static final String TARGET_ITEM_REPEATING = "repeating";
    public static final String TARGET_ITEM_REPEATING_WITH_NAME = "repeating-with-name";
    public static final String TARGET_ITEM_REPEATING_WITH_NAME_REPLACED = "repeating-with-name-replaced";

    @JRuleName("Rule name")
    @JRuleLogName("Rule log name")
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "timers")
    public void testTimers() {
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);
        stringItem.sendCommand("command");
        cancelTimer("NON_EXISTING_TIMER");
        createOrReplaceTimer("CREATE_OR_REPLACE_TIMER", Duration.ofSeconds(1),
                () -> logInfo("Replaced timer completed"));
        createTimer(null, Duration.ofSeconds(1), () -> {
            logInfo("log something");
            stringItem.sendCommand("unique timer");
        });

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

    @JRuleName("Repeating Timers")
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "timers-repeating")
    public void testRepeatingTimers() {
        JRuleStringItem repeatingItem = JRuleStringItem.forName(TARGET_ITEM_REPEATING);
        JRuleStringItem repeatingWithNameItem = JRuleStringItem.forName(TARGET_ITEM_REPEATING_WITH_NAME);
        JRuleStringItem repeatingWithNameReplacedItem = JRuleStringItem
                .forName(TARGET_ITEM_REPEATING_WITH_NAME_REPLACED);

        // with name
        final AtomicInteger repeatingWithNameCounter = new AtomicInteger(0);
        createRepeatingTimer("repeating-with-name", Duration.ofMillis(500), 2, () -> repeatingWithNameItem
                .sendCommand("repeating-with-name-" + repeatingWithNameCounter.incrementAndGet()));
        final AtomicInteger repeatingWithNameReplacedCounter = new AtomicInteger(0);
        createOrReplaceRepeatingTimer("repeating-with-name-replaced", Duration.ofMillis(200), 5,
                () -> repeatingWithNameReplacedItem.sendCommand(
                        "repeating-with-name-replaced-" + repeatingWithNameReplacedCounter.incrementAndGet()));

        // without name
        final AtomicInteger repeatingCounter = new AtomicInteger(0);
        createRepeatingTimer(Duration.ofMillis(100), 10,
                () -> repeatingItem.sendCommand("repeating-" + repeatingCounter.incrementAndGet()));
    }

    @JRuleName("Repeating Timers")
    @JRuleWhenItemReceivedCommand(item = TRIGGER_ITEM, command = "timers-repeating-complex")
    public void testRepeatingTimersComplex() {
        final AtomicInteger counter = new AtomicInteger(0);

        JRuleStringItem repeatingWithNameReplacedItem = JRuleStringItem
                .forName(TARGET_ITEM_REPEATING_WITH_NAME_REPLACED);

        final String TIMER_NAME = "repeating-with-name-replaced";
        createOrReplaceRepeatingTimer(TIMER_NAME, Duration.ofMillis(200), 5,
                () -> repeatingWithNameReplacedItem.sendCommand(TIMER_NAME + "-" + counter.incrementAndGet()));
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

    @JRuleDebounce(value = 1500, unit = ChronoUnit.MILLIS)
    @JRuleName("Rule name")
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "debounce")
    public void testDebounce() throws InterruptedException {
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);
        stringItem.sendCommand("no debounce");
    }
}
