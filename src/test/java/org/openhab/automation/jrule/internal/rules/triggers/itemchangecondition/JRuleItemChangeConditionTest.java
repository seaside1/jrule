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
package org.openhab.automation.jrule.internal.rules.triggers.itemchangecondition;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.openhab.automation.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.core.events.Event;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.StringType;

/**
 * The {@link JRuleItemChangeConditionRules} contains tests for @JRuleWhenItemChange#previousCondition
 *
 *
 * @author Robert Delbrück - Initial contribution
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleItemChangeConditionTest extends JRuleAbstractTest {
    @BeforeEach
    public void initTestClass() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(Mockito.anyString()))
                .then((Answer<Item>) invocationOnMock -> new StringItem(invocationOnMock.getArgument(0)));
    }

    @Test
    public void testItemChange_from_to() {
        JRuleItemChangeConditionRules rule = initRule(JRuleItemChangeConditionRules.class);
        // Only last event should trigger rule method
        fireEvents(false,
                List.of(itemChangeEvent(JRuleItemChangeConditionRules.ITEM_FROM_TO, "12", "13"),
                        itemChangeEvent(JRuleItemChangeConditionRules.ITEM_FROM_TO, "10", "11"),
                        itemChangeEvent(JRuleItemChangeConditionRules.ITEM_FROM_TO, "11", "12")));
        verify(rule, times(1)).itemChangeFromTo(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_from_to_2() {
        JRuleItemChangeConditionRules rule = initRule(JRuleItemChangeConditionRules.class);
        // Only last event should trigger rule method
        fireEvents(false, List.of(itemChangeEvent(JRuleItemChangeConditionRules.ITEM_FROM_TO_2, "10", "20"),
                itemChangeEvent(JRuleItemChangeConditionRules.ITEM_FROM_TO_2, "9", "21")));
        verify(rule, times(1)).itemChangeFromTo2(Mockito.any(JRuleEvent.class));
    }

    // Syntactic sugar
    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from));
    }
}
