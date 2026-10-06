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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

/**
 * Guards the timer cleanup in {@link JRuleAbstractTest}: the first test starts the self-rescheduling timer from
 * {@link JRuleTimerTestRules#testRescheduleTimers()}, the second one asserts that it no longer fires. Without the
 * cleanup the timer survives its own test method and keeps sending commands into the shared mocked event bus,
 * which makes later tests fail depending on how long the suite runs - the method order is what makes that
 * reproducible here.
 *
 * @author Robert Delbrück - Initial contribution
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class JRuleTimerIsolationTest extends JRuleAbstractTest {

    @Test
    public void a_leavePendingTimerBehind() throws ItemNotFoundException {
        initRule(JRuleTimerTestRules.class);
        registerItem(new StringItem(JRuleTimerTestRules.TARGET_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleTimerTestRules.TRIGGER_ITEM), UnDefType.UNDEF);

        JRuleItemRegistry.get(JRuleTimerTestRules.TARGET_ITEM, TargetItem.class);
        fireEvents(false, List.of(ItemEventFactory.createStateChangedEvent(JRuleTimerTestRules.TRIGGER_ITEM,
                new StringType("leak-probe"), new StringType("nothing"), null, null)));

        Assertions.assertTrue(JRuleTimerHandler.get().isTimerRunning(JRuleTimerTestRules.LEAK_PROBE_TIMER),
                "rule was expected to leave a pending timer behind");
    }

    @Test
    public void b_pendingTimerMustBeGone() {
        Assertions.assertFalse(JRuleTimerHandler.get().isTimerRunning(JRuleTimerTestRules.LEAK_PROBE_TIMER),
                "timer from the previous test is still registered and will fire into this one");
    }
}
