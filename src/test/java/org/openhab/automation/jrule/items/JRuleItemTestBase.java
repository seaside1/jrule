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
package org.openhab.automation.jrule.items;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleItemTestBase} base class for item testing
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleItemTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(JRuleItemTestBase.class);

    protected EventPublisher eventPublisher;

    @BeforeEach
    public void init() throws ItemNotFoundException {
        ItemRegistry itemRegistry = Mockito.mock(ItemRegistry.class);
        GenericItem ohItem = getOhItem();
        GroupItem ohGroupItem = new GroupItem("Group", ohItem);
        ohGroupItem.addMember(ohItem);
        Mockito.when(itemRegistry.getItem("Name")).thenReturn(ohItem);
        Mockito.when(itemRegistry.getItem("Group")).thenReturn(ohGroupItem);
        JRuleEventHandler.get().setItemRegistry(itemRegistry);

        eventPublisher = Mockito.mock(EventPublisher.class);
        Mockito.doAnswer(invocationOnMock -> {
            Event event = invocationOnMock.getArgument(0);
            if (event instanceof ItemStateEvent) {
                State state = ((ItemStateEvent) event).getItemState();
                ohItem.setState(state);
                ohGroupItem.setState(state);
            } else if (event instanceof ItemCommandEvent) {
                if (((ItemCommandEvent) event).getItemCommand() instanceof State) {
                    State state = (State) ((ItemCommandEvent) event).getItemCommand();
                    ohItem.setState(state);
                    ohGroupItem.setState(state);
                } else {
                    LOG.warn("do nothing with commands which are not changing the state: "
                            + ((ItemCommandEvent) event).getItemCommand());
                }
            }
            return null;
        }).when(eventPublisher).post(Mockito.any());
        JRuleEventHandler.get().setEventPublisher(eventPublisher);
    }

    protected abstract JRuleItem getJRuleItem();

    @Test
    public void testPostNull() {
        JRuleItem item = getJRuleItem();
        JRuleValue command = getDefaultCommand();
        item.postUncheckedUpdate(command);
        Assertions.assertNotNull(item.getState());
        item.postNullUpdate();
        Assertions.assertNull(item.getState());
        Assertions.assertNull(item.getStateAsString());
    }

    @Test
    public void testEquals() {
        Assertions.assertEquals(getJRuleItem(), getJRuleItem());
    }

    protected abstract JRuleValue getDefaultCommand();

    protected abstract GenericItem getOhItem();

    protected void verifyEventTypes(int wantedStateCalls, int wantedCommandCalls) {
        Mockito.verify(eventPublisher, Mockito.times(wantedStateCalls)).post(Mockito.any(ItemStateEvent.class));
        Mockito.verify(eventPublisher, Mockito.times(wantedCommandCalls)).post(Mockito.any(ItemCommandEvent.class));
    }
}
