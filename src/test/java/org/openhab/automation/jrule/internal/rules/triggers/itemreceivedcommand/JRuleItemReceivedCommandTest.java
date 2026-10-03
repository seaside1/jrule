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
package org.openhab.automation.jrule.internal.rules.triggers.itemreceivedcommand;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.openhab.automation.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.core.events.Event;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;

/**
 * The {@link JRuleItemReceivedCommandTest} contains tests for @JRuleWhenItemReceivedCommand trigger
 *
 * @author Robert Delbrück - Initial contribution
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleItemReceivedCommandTest extends JRuleAbstractTest {
    @BeforeEach
    public void initTestClass() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(Mockito.anyString()))
                .then((Answer<Item>) invocationOnMock -> new DimmerItem(invocationOnMock.getArgument(0)));
    }

    /**
     * A dimmer ramped from a wall switch arrives as IncreaseDecreaseType. Without a commandMapping entry for it
     * the conversion threw while building the JRuleItemEvent, the event bus dropped the event with nothing but a
     * warning, and the rule never ran at all.
     */
    @Test
    public void testReceivedCommand_increaseDecrease() {
        JRuleItemReceivedCommandRules rule = initRule(JRuleItemReceivedCommandRules.class);
        fireEvents(false,
                List.of(commandEvent(JRuleItemReceivedCommandRules.DIMMER_ITEM, IncreaseDecreaseType.INCREASE),
                        commandEvent(JRuleItemReceivedCommandRules.DIMMER_ITEM, IncreaseDecreaseType.DECREASE)));
        verify(rule, times(2)).receivedCommand(Mockito.any(JRuleItemEvent.class));
    }

    @Test
    public void testReceivedCommand_onOff() {
        JRuleItemReceivedCommandRules rule = initRule(JRuleItemReceivedCommandRules.class);
        fireEvents(false, List.of(commandEvent("other_item", OnOffType.ON),
                commandEvent(JRuleItemReceivedCommandRules.DIMMER_ITEM, OnOffType.ON)));
        verify(rule, times(1)).receivedCommand(Mockito.any(JRuleItemEvent.class));
    }

    @Test
    public void testReceivedCommand_filteredOnIncrease() {
        JRuleItemReceivedCommandRules rule = initRule(JRuleItemReceivedCommandRules.class);
        fireEvents(false, List.of(
                commandEvent(JRuleItemReceivedCommandRules.DIMMER_ITEM_INCREASE_ONLY, IncreaseDecreaseType.DECREASE),
                commandEvent(JRuleItemReceivedCommandRules.DIMMER_ITEM_INCREASE_ONLY, IncreaseDecreaseType.INCREASE)));
        verify(rule, times(1)).receivedIncreaseCommand(Mockito.any(JRuleItemEvent.class));
    }

    // Syntactic sugar
    private Event commandEvent(String item, Command command) {
        return ItemEventFactory.createCommandEvent(item, command);
    }
}
