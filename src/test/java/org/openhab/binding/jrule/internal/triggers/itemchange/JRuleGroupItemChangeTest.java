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
import static org.openhab.binding.jrule.internal.triggers.itemchange.JRuleGroupItemChangeRules.GROUP_ITEM_FROM;
import static org.openhab.binding.jrule.internal.triggers.itemchange.JRuleGroupItemChangeRules.SUB_GROUP_ITEM;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.binding.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.core.events.Event;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.CoreItemFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

/**
 * The {@link JRuleGroupItemChangeRules} contains tests for @JRuleWhenItemChange with memberOf=true trigger
 *
 *
 * @author Robert Delbrück - Initial contribution
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleGroupItemChangeTest extends JRuleAbstractTest {

    public static final String MEMBER_ITEM = "memberof_item";
    public static final String OTHER_ITEM = "other_item";

    @BeforeAll
    public void initTestClass() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(OTHER_ITEM))
                .then((Answer<Item>) invocationOnMock -> new StringItem(invocationOnMock.getArgument(0)));
    }

    @Test
    public void testItemChange_selfGroup() {
        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(JRuleGroupItemChangeRules.GROUP_ITEM, "2", "1")));
        verify(rule, times(0)).groupItemChange(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_no_from_to() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames()).thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        Mockito.when(itemRegistry.getItem(OTHER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames()).thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_TO));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        registerItem(new StringItem(JRuleGroupItemChangeRules.GROUP_ITEM), UnDefType.UNDEF);
        registerItem(new StringItem(JRuleGroupItemChangeRules.GROUP_ITEM_TO), UnDefType.UNDEF);

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(OTHER_ITEM, "2", "1"), itemChangeEvent(MEMBER_ITEM, "2", "1")));
        verify(rule, times(1)).groupItemChange(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChangeJustItems() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames())
                    .thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_ITEMS));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });
        Mockito.when(itemRegistry.getItem(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_ITEMS))
                .thenAnswer(invocationOnMock -> new GroupItem(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_ITEMS,
                        new StringItem("Any")));

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        fireEvents(List.of(itemChangeEvent(MEMBER_ITEM, "2", "1")));
        verify(rule, times(0)).groupItemChangeJustGroups(Mockito.any(JRuleEvent.class));
        verify(rule, times(1)).groupItemChangeJustItems(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChangeJustGroups() throws ItemNotFoundException {
        // should not trigger
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames())
                    .thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        // should trigger
        Mockito.when(itemRegistry.getItem(SUB_GROUP_ITEM)).then((Answer<Item>) invocationOnMock -> {
            GroupItem groupItem = Mockito.mock(GroupItem.class);
            Mockito.when(groupItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(groupItem.getGroupNames())
                    .thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS));
            Mockito.when(groupItem.getType()).thenReturn(GroupItem.TYPE);
            return groupItem;
        });
        Mockito.when(itemRegistry.getItem(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS))
                .thenAnswer(invocationOnMock -> new GroupItem(invocationOnMock.getArgument(0), new StringItem("Any")));

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        fireEvents(List.of(itemChangeEvent(SUB_GROUP_ITEM, "2", "1"), itemChangeEvent(MEMBER_ITEM, "2", "1")));
        verify(rule, times(1)).groupItemChangeJustGroups(Mockito.any(JRuleEvent.class));
        verify(rule, times(0)).groupItemChangeJustItems(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemReceivedUpdateJustGroups() throws ItemNotFoundException {
        // should not trigger
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames())
                    .thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        // should trigger
        Mockito.when(itemRegistry.getItem(SUB_GROUP_ITEM)).then((Answer<Item>) invocationOnMock -> {
            GroupItem groupItem = Mockito.mock(GroupItem.class);
            Mockito.when(groupItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(groupItem.getGroupNames())
                    .thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS));
            Mockito.when(groupItem.getType()).thenReturn(GroupItem.TYPE);
            return groupItem;
        });
        Mockito.when(itemRegistry.getItem(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS))
                .thenAnswer(invocationOnMock -> new GroupItem(invocationOnMock.getArgument(0), new StringItem("Any")));

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        fireEvents(List.of(itemReceivedUpdateEvent(SUB_GROUP_ITEM, "1"), itemReceivedUpdateEvent(MEMBER_ITEM, "1")));
        verify(rule, times(1)).groupItemReceivedUpdateJustGroups(Mockito.any(JRuleEvent.class));
        verify(rule, times(0)).groupItemReceivedUpdateJustItems(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemReceivedCommandJustGroups() throws ItemNotFoundException {
        // should not trigger
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames())
                    .thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        // should trigger
        Mockito.when(itemRegistry.getItem(SUB_GROUP_ITEM)).then((Answer<Item>) invocationOnMock -> {
            GroupItem groupItem = Mockito.mock(GroupItem.class);
            Mockito.when(groupItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(groupItem.getGroupNames())
                    .thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS));
            Mockito.when(groupItem.getType()).thenReturn(GroupItem.TYPE);
            return groupItem;
        });
        Mockito.when(itemRegistry.getItem(JRuleGroupItemChangeRules.GROUP_ITEM_JUST_GROUPS))
                .thenAnswer(invocationOnMock -> new GroupItem(invocationOnMock.getArgument(0), new StringItem("Any")));

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        fireEvents(List.of(itemReceivedCommandEvent(SUB_GROUP_ITEM, "1"), itemReceivedCommandEvent(MEMBER_ITEM, "1")));
        verify(rule, times(1)).groupItemReceivedCommandJustGroups(Mockito.any(JRuleEvent.class));
        verify(rule, times(0)).groupItemReceivedCommandJustItems(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_from() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames()).thenReturn(List.of(GROUP_ITEM_FROM));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        registerItem(new StringItem(GROUP_ITEM_FROM), UnDefType.UNDEF);

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(MEMBER_ITEM, "2", "1"), itemChangeEvent(MEMBER_ITEM, "1", "2")));
        verify(rule, times(1)).groupItemChangeFrom(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemReceivedUpdate() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(SUB_GROUP_ITEM)).then((Answer<Item>) invocationOnMock -> {
            GroupItem groupItem = Mockito.mock(GroupItem.class);
            Mockito.when(groupItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(groupItem.getGroupNames()).thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_FROM));
            Mockito.when(groupItem.getType()).thenReturn(GroupItem.TYPE);
            return groupItem;
        });
        Mockito.when(itemRegistry.getItem(JRuleGroupItemChangeRules.GROUP_ITEM_FROM))
                .thenAnswer(invocationOnMock -> new GroupItem(invocationOnMock.getArgument(0), new StringItem("Any")));

        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames()).thenReturn(List.of(GROUP_ITEM_FROM));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemReceivedUpdateEvent(MEMBER_ITEM, "1"), itemReceivedUpdateEvent(SUB_GROUP_ITEM, "2")));
        verify(rule, times(2)).groupItemReceivedUpdateTo(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemReceivedCommand() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(SUB_GROUP_ITEM)).then((Answer<Item>) invocationOnMock -> {
            GroupItem groupItem = Mockito.mock(GroupItem.class);
            Mockito.when(groupItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(groupItem.getGroupNames()).thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_FROM));
            Mockito.when(groupItem.getType()).thenReturn(GroupItem.TYPE);
            return groupItem;
        });
        Mockito.when(itemRegistry.getItem(JRuleGroupItemChangeRules.GROUP_ITEM_FROM))
                .thenAnswer(invocationOnMock -> new GroupItem(invocationOnMock.getArgument(0), new StringItem("Any")));

        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames()).thenReturn(List.of(GROUP_ITEM_FROM));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemReceivedCommandEvent(MEMBER_ITEM, "1"), itemReceivedCommandEvent(SUB_GROUP_ITEM, "2")));
        verify(rule, times(2)).groupItemReceivedCommandFrom(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_to() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames()).thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_TO));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        registerItem(new StringItem(JRuleGroupItemChangeRules.GROUP_ITEM_TO), UnDefType.UNDEF);

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(MEMBER_ITEM, "1", "2"), itemChangeEvent(MEMBER_ITEM, "2", "1")));
        verify(rule, times(1)).groupItemChangeTo(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_from_to() throws ItemNotFoundException {
        Mockito.when(itemRegistry.getItem(MEMBER_ITEM)).then((Answer<Item>) invocationOnMock -> {
            StringItem stringItem = Mockito.mock(StringItem.class);
            Mockito.when(stringItem.getName()).thenReturn(invocationOnMock.getArgument(0));
            Mockito.when(stringItem.getGroupNames()).thenReturn(List.of(JRuleGroupItemChangeRules.GROUP_ITEM_FROM_TO));
            Mockito.when(stringItem.getType()).thenReturn(CoreItemFactory.STRING);
            return stringItem;
        });

        registerItem(new StringItem(JRuleGroupItemChangeRules.GROUP_ITEM_FROM_TO), UnDefType.UNDEF);

        JRuleGroupItemChangeRules rule = initRule(JRuleGroupItemChangeRules.class);
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(MEMBER_ITEM, "2", "1"), itemChangeEvent(MEMBER_ITEM, "3", "2"),
                itemChangeEvent(MEMBER_ITEM, "1", "2")));
        verify(rule, times(1)).groupItemChangeFromTo(Mockito.any(JRuleEvent.class));
    }

    // Syntactic sugar
    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from));
    }

    // Syntactic sugar
    private Event itemReceivedUpdateEvent(String item, String to) {
        return ItemEventFactory.createStateEvent(item, new StringType(to));
    }

    // Syntactic sugar
    private Event itemReceivedCommandEvent(String item, String to) {
        return ItemEventFactory.createCommandEvent(item, new StringType(to));
    }
}
