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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.core.events.Event;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

/**
 * Testcases for rule timers
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleTimerTest extends JRuleAbstractTest {

    @Test
    public void testTimer() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "timers")));
        verify(rule, times(1)).testTimers();
        Thread.sleep(3000); // Wait for timer inside rule to execute
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "command"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "timedCommand"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "nestedTimedCommand"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "unique timer"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "after the other one 3"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "timer2 running true"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "timer2 done false"));
        assertFalse(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "canceled timer"));
    }

    @Test
    public void testTimerReschedule() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "reschedule")));
        verify(rule, times(1)).testRescheduleTimers();
        Thread.sleep(1500); // Wait for timer inside rule to execute
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "command"));
        assertTrue(eventPublisher.countCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "timedCommand") >= 2);
    }

    @Test
    public void testRepeatingTimer() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM_REPEATING), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME_REPLACED), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM_REPEATING, TargetItem.class);
        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME, TargetItem.class);
        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME_REPLACED, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "timers-repeating")));
        verify(rule, times(1)).testRepeatingTimers();
        Thread.sleep(3000); // Wait for timer inside rule to execute
        assertTrue(eventPublisher.isLastCommandEvent(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME,
                "repeating-with-name-2"));
        assertTrue(eventPublisher.isLastCommandEvent(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME_REPLACED,
                "repeating-with-name-replaced-5"));
        assertTrue(eventPublisher.isLastCommandEvent(JRuleTimerTestRules.TARGET_ITEM_REPEATING, "repeating-10"));
    }

    @Test
    public void testRepeatingTimerComplex() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME_REPLACED), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME_REPLACED, TargetItem.class);
        fireEvents(true, List.of(itemCommandEvent(JRuleTimerTestRules.TRIGGER_ITEM, "timers-repeating-complex")));
        Thread.sleep(500);
        fireEvents(true, List.of(itemCommandEvent(JRuleTimerTestRules.TRIGGER_ITEM, "timers-repeating-complex")));
        Thread.sleep(3000); // Wait for timer inside rule to execute
        verify(rule, times(2)).testRepeatingTimersComplex();
        assertEquals(1,
                eventPublisher.getCommandEvents(JRuleTimerTestRules.TARGET_ITEM_REPEATING_WITH_NAME_REPLACED).stream()
                        .filter(c -> ((ItemCommandEvent) c.getEvent()).getItemCommand().toString()
                                .equals("repeating-with-name-replaced-5"))
                        .count());
    }

    @Test
    public void testGetTimedLock() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "locks")));
        verify(rule, times(1)).testLocks();
        Thread.sleep(3000); // Wait for timer inside rule to execute
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "first: true"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "second: false"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "third: true"));
    }

    @Test
    public void testDebounce() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "debounce")));
        Thread.sleep(600);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "debounce")));
        Thread.sleep(600);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "debounce")));
        verify(rule, times(1)).testDebounce();
        Thread.sleep(3000); // Wait for timer inside rule to execute
        assertEquals(1, eventPublisher.countCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "no debounce"));
    }

    @Test
    public void testIsTimerRunning() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "isTimerRunning")));
        verify(rule, times(1)).testIsTimerRunning();
        Thread.sleep(2000); // Wait for timer inside rule to execute
        assertEquals(1, eventPublisher.countCommandEvent(JRuleTimerTestRules.TARGET_ITEM,
                "isTimerRunning (unknown-timer): false"));
        assertEquals(1, eventPublisher.countCommandEvent(JRuleTimerTestRules.TARGET_ITEM,
                "isTimerRunning (known-timer): true"));
        assertEquals(1, eventPublisher.countCommandEvent(JRuleTimerTestRules.TARGET_ITEM,
                "isTimerRunning (known-timer): false"));
    }

    @Test
    public void testInvokeTimer() throws ItemNotFoundException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "invoke")));
        verify(rule, times(1)).testInvokeTimer();

        // the timer is scheduled an hour out, so without invoke() nothing of this would have happened yet
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "invoked"));
        // invoke() runs the function without touching the schedule
        assertTrue(
                eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "still running before invoke: true"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "still running after invoke: true"));
        // the rule's execution context has to outlive invoke() - creating a timer afterwards needs it
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "context survived"));
    }

    /**
     * The path a rule test actually takes: it never holds the JRuleTimer the rule created, so it looks the timer up
     * by name and invokes it from outside the rule. This is what JRuleTestBase.invokeTimer does in the jrule-test
     * framework, and the only reason getTimers is public.
     */
    @Test
    public void testInvokeTimerLookedUpByName() throws ItemNotFoundException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "invoke-by-name")));
        verify(rule, times(1)).testInvokeTimerByName();

        // the rule scheduled it an hour out and did not invoke it, so nothing has run yet
        assertFalse(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "invoked by name"));

        List<JRuleTimerHandler.JRuleTimer> timers = JRuleTimerHandler.get()
                .getTimers(JRuleTimerTestRules.TIMER_INVOKED_BY_NAME);
        assertEquals(1, timers.size(), "rule should have registered exactly one timer under that name");
        timers.get(0).invoke();

        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "invoked by name"));
    }

    @Test
    public void testInvokeRejectsTimeLock() throws ItemNotFoundException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "nothing", "long-lock")));
        verify(rule, times(1)).testLongLock();

        // getTimers hands out time locks too, and those carry no function - invoking one has to say so
        List<JRuleTimerHandler.JRuleTimer> locks = JRuleTimerHandler.get()
                .getTimers(JRuleTimerHandler.LOCK_PREFIX + JRuleTimerTestRules.LONG_LOCK);
        assertEquals(1, locks.size());
        JRuleRuntimeException e = Assertions.assertThrows(JRuleRuntimeException.class, () -> locks.get(0).invoke());
        assertTrue(e.getMessage().contains("time locks cannot be invoked"), e.getMessage());
    }

    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from), null, null);
    }

    private Event itemCommandEvent(String item, String to) {
        return ItemEventFactory.createCommandEvent(item, new StringType(to));
    }
}
