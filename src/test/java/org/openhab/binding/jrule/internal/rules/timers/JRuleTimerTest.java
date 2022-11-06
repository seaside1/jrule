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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.binding.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.core.events.Event;
import org.openhab.core.items.ItemNotFoundException;
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
    public void testOneOffTimerWithNestedtimer() throws ItemNotFoundException, InterruptedException {
        JRuleTimerTestRules rule = initRule(JRuleTimerTestRules.class);
        // Set item state in ItemRegistry
        setState(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(List.of(itemChangeEvent(JRuleTimerTestRules.TRIGGER_ITEM, "2", "1")));
        verify(rule, times(1)).testSendCommand();
        Thread.sleep(3000); // Wait for timer inside rule to execute
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "command"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "timedCommand"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleTimerTestRules.TARGET_ITEM, "nestedTimedCommand"));
    }

    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from));
    }
}
