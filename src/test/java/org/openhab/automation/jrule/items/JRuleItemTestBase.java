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
package org.openhab.automation.jrule.items;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.items.metadata.JRuleMetadataRegistry;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.MetadataRegistry;
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
    public static final String ITEM_NON_EXISTING = "NonExisting";
    public static final String ITEM_NAME = "Name";
    public static final String GROUP_NAME = "Group";
    public static final String ITEM_NAME_2 = "Name2";
    public static final String GROUP_NAME_2 = "Group2";
    public static final String SUB_ITEM_NAME = "NameSub";
    public static final String METADATA_WHAT_S_THE_TIME = "what's the time";
    public static final String METADATA_VOICE_SYSTEM = "voiceSystem";

    protected EventPublisher eventPublisher;

    protected final JRuleMetadataRegistry mock = Mockito.mock(JRuleMetadataRegistry.class);

    @BeforeEach
    public void init() throws ItemNotFoundException {
        JRuleItemRegistry.clear();

        ItemRegistry itemRegistry = Mockito.mock(ItemRegistry.class);
        GenericItem ohItem = getOhItem(ITEM_NAME);
        GenericItem ohItem2 = getOhItem(ITEM_NAME_2);
        GroupItem ohGroupItem = new GroupItem(GROUP_NAME, getOhItem(null));
        ohGroupItem.addMember(ohItem);
        ohGroupItem.addMember(ohItem2);
        GroupItem ohSubGroupItem = new GroupItem(GROUP_NAME_2, getOhItem(null));
        GenericItem ohItem3 = getOhItem(SUB_ITEM_NAME);
        ohSubGroupItem.addMember(ohItem3);
        ohGroupItem.addMember(ohSubGroupItem);
        Mockito.when(itemRegistry.getItem(ITEM_NAME)).thenReturn(ohItem);
        Mockito.when(itemRegistry.getItem(ITEM_NAME_2)).thenReturn(ohItem2);
        Mockito.when(itemRegistry.getItem(SUB_ITEM_NAME)).thenReturn(ohItem3);
        Mockito.when(itemRegistry.getItem(GROUP_NAME)).thenReturn(ohGroupItem);
        Mockito.when(itemRegistry.getItem(GROUP_NAME_2)).thenReturn(ohSubGroupItem);
        Mockito.when(itemRegistry.getItem(ITEM_NON_EXISTING)).thenThrow(JRuleItemNotFoundException.class);
        JRuleEventHandler.get().setItemRegistry(itemRegistry);

        eventPublisher = Mockito.mock(EventPublisher.class);
        Mockito.doAnswer(invocationOnMock -> {
            Event event = invocationOnMock.getArgument(0);
            if (event instanceof ItemStateEvent) {
                State state = ((ItemStateEvent) event).getItemState();
                ohItem.setState(state);
                ohGroupItem.setState(state);
            } else if (event instanceof ItemCommandEvent) {
                if (((ItemCommandEvent) event).getItemCommand() instanceof State state) {
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

        MetadataRegistry metadataRegistry = Mockito.mock(MetadataRegistry.class);
        JRuleItemRegistry.setMetadataRegistry(metadataRegistry);

        Mockito.when(mock.getAllMetadata(Mockito.anyString()))
                .thenReturn(Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom")),
                        METADATA_VOICE_SYSTEM, new JRuleItemMetadata(METADATA_WHAT_S_THE_TIME)));
    }

    protected abstract JRuleItem getJRuleItem();

    @Test
    public void testGetTags() {
        JRuleItem item = getJRuleItem();
        Assertions.assertEquals(2, item.getTags().size());
    }

    @Test
    public void testGetGroupItems() {
        JRuleItem item = getJRuleItem();
        if (item.isGroup()) {
            Assertions.assertEquals(0, item.getGroupItems().size());
        } else {
            Assertions.assertEquals(1, item.getGroupItems().size());
            Assertions.assertEquals(GROUP_NAME, new ArrayList<>(item.getGroupItems()).get(0).getName());
        }
    }

    @Test
    public void testGetMetadata() {
        JRuleItem item = getJRuleItem();
        Assertions.assertEquals(2, item.getMetadata().size());
        Assertions.assertEquals("SetLightState", item.getMetadata().get("Speech").getValue());
        Assertions.assertEquals(1, item.getMetadata().get("Speech").getConfiguration().size());
        Assertions.assertEquals("Livingroom", item.getMetadata().get("Speech").getConfiguration().get("location"));
    }

    @Test
    public void testAddMetadata() {
        JRuleItem item = getJRuleItem();
        item.addMetadata(METADATA_VOICE_SYSTEM, new JRuleItemMetadata(METADATA_WHAT_S_THE_TIME), false);
        Assertions.assertEquals(METADATA_WHAT_S_THE_TIME, item.getMetadata().get(METADATA_VOICE_SYSTEM).getValue());

        item.addMetadata(METADATA_VOICE_SYSTEM, new JRuleItemMetadata("something else"), false);
        Assertions.assertEquals(METADATA_WHAT_S_THE_TIME, item.getMetadata().get(METADATA_VOICE_SYSTEM).getValue());

        // not handled by the mock, therefor it's ok here
        item.addMetadata(METADATA_VOICE_SYSTEM, new JRuleItemMetadata("something else"), true);
        Assertions.assertEquals(METADATA_WHAT_S_THE_TIME, item.getMetadata().get(METADATA_VOICE_SYSTEM).getValue());
    }

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

    protected abstract GenericItem getOhItem(String name);

    protected void verifyEventTypes(TestInfo testInfo, int wantedStateCalls, int wantedCommandCalls) {
        if (testInfo.getTestClass().orElseThrow().getSimpleName().contains("GroupItem")) {
            Mockito.verify(eventPublisher, Mockito.times(wantedStateCalls * 5)).post(Mockito.any(ItemStateEvent.class));
            Mockito.verify(eventPublisher, Mockito.times(wantedCommandCalls * 5))
                    .post(Mockito.any(ItemCommandEvent.class));
        } else {
            Mockito.verify(eventPublisher, Mockito.times(wantedStateCalls)).post(Mockito.any(ItemStateEvent.class));
            Mockito.verify(eventPublisher, Mockito.times(wantedCommandCalls)).post(Mockito.any(ItemCommandEvent.class));
        }
    }

    @Test
    public void testForName(TestInfo testInfo) {
        Assumptions.assumeTrue(testInfo.getTestClass().orElseThrow().getSimpleName().contains("GroupItem"),
                "not a GroupItem test");
        Assertions.assertNotNull(groupForNameMethod(GROUP_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> groupForNameMethod(ITEM_NON_EXISTING));
        Assertions.assertTrue(groupForNameOptionalMethod(GROUP_NAME).isPresent());
        Assertions.assertFalse(groupForNameOptionalMethod(ITEM_NON_EXISTING).isPresent());
    }

    protected abstract <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(
            String name);

    protected abstract <T extends JRuleGroupItem<? extends JRuleItem>> T groupForNameMethod(String name);

    @Test
    public void testMemberItems(TestInfo testInfo) {
        Assumptions.assumeTrue(testInfo.getTestClass().orElseThrow().getSimpleName().contains("GroupItem"),
                "not a GroupItem test");
        Assertions.assertEquals(3, groupForNameMethod(GROUP_NAME).memberItems().size());
        Assertions.assertEquals(3, groupForNameMethod(GROUP_NAME).memberItems(false).size());
        Assertions.assertEquals(4, groupForNameMethod(GROUP_NAME).memberItems(true).size());
    }
}
