/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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
package org.openhab.automation.jrule.internal.rules.timers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleDebounce;
import org.openhab.automation.jrule.rules.JRuleLogName;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;

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
                t -> logInfo("Replaced timer completed"));
        createTimer(null, Duration.ofSeconds(1), t -> {
            logInfo("log something");
            stringItem.sendCommand("unique timer");
        });

        JRuleTimerHandler.JRuleTimer timer = createTimer("TimerName", Duration.ofMillis(500), t -> {
            stringItem.sendCommand("timedCommand");
            createTimer("NestedTimer", Duration.ofMillis(500), t2 -> {
                stringItem.sendCommand("nestedTimedCommand");
            });
        });
        stringItem.sendCommand("timer2 running " + timer.isRunning());
        stringItem.sendCommand("timer2 done " + timer.isDone());

        final AtomicInteger chainedCounter = new AtomicInteger(0);
        createTimer("inner-1", Duration.ofSeconds(1), t -> {
            logInfo("calling inner-1");
            chainedCounter.incrementAndGet();
        }).createTimerAfter("inner-2", Duration.ofMillis(500), t -> {
            logInfo("calling inner-2");
            chainedCounter.incrementAndGet();
        }).createTimerAfter("inner-3", Duration.ofMillis(200), t -> {
            logInfo("calling inner-3");
            stringItem.sendCommand("after the other one " + chainedCounter.incrementAndGet());
        });

        JRuleTimerHandler.JRuleTimer canceledTimer = createTimer(null, Duration.ofSeconds(1),
                t -> stringItem.sendCommand("canceled timer"));
        canceledTimer.cancel();
    }

    @JRuleName("Reschedule Timer")
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "reschedule")
    public void testRescheduleTimers() {
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);
        stringItem.sendCommand("command");

        createTimer("TimerName", Duration.ofMillis(500), t -> {
            stringItem.sendCommand("timedCommand");
            t.rescheduleTimer(Duration.ofMillis(500));
        });
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
        createRepeatingTimer("repeating-with-name", Duration.ofMillis(500), 2, t -> repeatingWithNameItem
                .sendCommand("repeating-with-name-" + repeatingWithNameCounter.incrementAndGet()));
        final AtomicInteger repeatingWithNameReplacedCounter = new AtomicInteger(0);
        createOrReplaceRepeatingTimer("repeating-with-name-replaced", Duration.ofMillis(200), 5,
                t -> repeatingWithNameReplacedItem.sendCommand(
                        "repeating-with-name-replaced-" + repeatingWithNameReplacedCounter.incrementAndGet()));

        // without name
        final AtomicInteger repeatingCounter = new AtomicInteger(0);
        createRepeatingTimer(Duration.ofMillis(100), 10,
                t -> repeatingItem.sendCommand("repeating-" + repeatingCounter.incrementAndGet()));
    }

    @JRuleName("Repeating Timers")
    @JRuleWhenItemReceivedCommand(item = TRIGGER_ITEM, command = "timers-repeating-complex")
    public void testRepeatingTimersComplex() {
        final AtomicInteger counter = new AtomicInteger(0);

        JRuleStringItem repeatingWithNameReplacedItem = JRuleStringItem
                .forName(TARGET_ITEM_REPEATING_WITH_NAME_REPLACED);

        final String TIMER_NAME = "repeating-with-name-replaced";
        createOrReplaceRepeatingTimer(TIMER_NAME, Duration.ofMillis(200), 5,
                t -> repeatingWithNameReplacedItem.sendCommand(TIMER_NAME + "-" + counter.incrementAndGet()));
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

    @JRuleName("Rule name")
    @JRuleWhenItemChange(item = TRIGGER_ITEM, to = "isTimerRunning")
    public void testIsTimerRunning() throws InterruptedException {
        JRuleStringItem stringItem = JRuleStringItem.forName(TARGET_ITEM);
        String timerUnknown = "unknown-timer";
        String timerKnown = "known-timer";

        stringItem.sendCommand("isTimerRunning (" + timerUnknown + "): " + isTimerRunning(timerUnknown));

        createTimer(timerKnown, Duration.ofSeconds(1), t -> {
            // do just nothing, just run...
        });
        stringItem.sendCommand("isTimerRunning (known-timer): " + isTimerRunning(timerKnown));
        Thread.sleep(1100);
        stringItem.sendCommand("isTimerRunning (known-timer): " + isTimerRunning(timerKnown));
    }
}
