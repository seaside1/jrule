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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.internal.rules.CollectingEventPublisher;
import org.openhab.automation.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.automation.jrule.internal.rules.timers.TargetItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.core.events.Event;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

/**
 * Testcases for delayed executions
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleDelayedTest extends JRuleAbstractTest {
    @BeforeEach
    public void cleanup() {
        eventPublisher.clear();
        JRuleTimerHandler.get().cancelAll();
    }

    @Test
    public void testDelayed() throws ItemNotFoundException, InterruptedException {
        JRuleDelayedTestRules rule = initRule(JRuleDelayedTestRules.class);
        // Set item state in ItemRegistry

        registerItem(new StringItem(JRuleDelayedTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleDelayedTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleDelayedTestRules.TARGET_ITEM, TargetItem.class);
        ZonedDateTime fired = ZonedDateTime.now();
        fireEvents(false, List.of(itemChangeEvent(JRuleDelayedTestRules.TRIGGER_ITEM, "nothing", "2s")));
        Thread.sleep(3000); // Wait for delayed execution
        verify(rule, times(1)).test2s();
        List<CollectingEventPublisher.Container> events = eventPublisher
                .getCommandEvents(JRuleDelayedTestRules.TARGET_ITEM);
        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.get(0).getTime().isAfter(fired.plusSeconds(2)));
    }

    @Test
    public void testDelayedNotWaiting() throws ItemNotFoundException, InterruptedException {
        JRuleDelayedTestRules rule = initRule(JRuleDelayedTestRules.class);
        // Set item state in ItemRegistry
        registerItem(new StringItem(JRuleDelayedTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleDelayedTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleDelayedTestRules.TARGET_ITEM, TargetItem.class);
        ZonedDateTime fired = ZonedDateTime.now();
        fireEvents(false, List.of(itemChangeEvent(JRuleDelayedTestRules.TRIGGER_ITEM, "nothing", "2s")));
        verify(rule, times(0)).test2s();
    }

    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from));
    }
}
