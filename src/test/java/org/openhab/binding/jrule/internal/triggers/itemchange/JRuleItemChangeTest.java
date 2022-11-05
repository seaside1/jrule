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
package org.openhab.binding.jrule.internal.triggers.itemchange;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.binding.jrule.internal.triggers.JRuleAbstractTest;
import org.openhab.core.events.Event;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.StringType;

/**
 * The {@link JRuleItemChangeRules} contains tests for @JRuleWhenItemChange trigger
 *
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleItemChangeTest extends JRuleAbstractTest {
    @BeforeAll
    public static void initTestClass() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(Mockito.anyString()))
                .then((Answer<Item>) invocationOnMock -> new StringItem(invocationOnMock.getArgument(0)));
    }

    @Test
    public void testItemChange_no_from_to() {
        JRuleItemChangeRules rule = initRule(JRuleItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(
                List.of(itemChangeEvent("other_item", "2", "1"), itemChangeEvent(JRuleItemChangeRules.ITEM, "2", "1")));
        verify(rule, times(1)).itemChange(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_from() {
        JRuleItemChangeRules rule = initRule(JRuleItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(JRuleItemChangeRules.ITEM_FROM, "2", "1"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_FROM, "1", "2")));
        verify(rule, times(1)).itemChangeFrom(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_to() {
        JRuleItemChangeRules rule = initRule(JRuleItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(JRuleItemChangeRules.ITEM_TO, "1", "2"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_TO, "2", "1")));
        verify(rule, times(1)).itemChangeTo(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_from_to() {
        JRuleItemChangeRules rule = initRule(JRuleItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(JRuleItemChangeRules.ITEM_FROM_TO, "2", "1"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_FROM_TO, "3", "2"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_FROM_TO, "1", "2")));
        verify(rule, times(1)).itemChangeFromTo(Mockito.any(JRuleEvent.class));
    }

    // Syntactic sugar
    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from));
    }
}
