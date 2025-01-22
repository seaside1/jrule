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
package org.openhab.automation.jrule.internal.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.Metadata;
import org.openhab.core.items.MetadataKey;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.link.ItemChannelLink;
import org.openhab.core.thing.link.ItemChannelLinkRegistry;

/**
 * The {@link JRuleItemHandler} provides access to item Registry
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleItemHandler {

    private static volatile JRuleItemHandler instance = null;

    private JRuleItemHandler() {
    }

    private ItemRegistry itemRegistry;
    private ItemChannelLinkRegistry itemChannelLinkRegistry;

    private MetadataRegistry metadataRegistry;

    public void setMetadataRegistry(MetadataRegistry metadataRegistry) {
        this.metadataRegistry = metadataRegistry;
    }

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void setItemChannelLinkRegistry(ItemChannelLinkRegistry itemChannelLinkRegistry) {
        this.itemChannelLinkRegistry = itemChannelLinkRegistry;
    }

    public static JRuleItemHandler get() {
        if (instance == null) {
            synchronized (JRuleItemHandler.class) {
                if (instance == null) {
                    instance = new JRuleItemHandler();
                }
            }
        }
        return instance;
    }

    public Item addToRegistry(Item item) {
        return itemRegistry.add(item);
    }

    public boolean itemRegistryContainsItem(String itemName) {
        return itemRegistry.get(itemName) != null;
    }

    public Item addSwitchItem(String name) {
        return addSwitchItem(name, null, null, null);
    }

    public Item addSwitchItem(String name, Boolean value) {
        return addSwitchItem(name, value, null, null);
    }

    public Item addSwitchItem(String name, Boolean value, String label) {
        return addSwitchItem(name, value, label, null);
    }

    public Item addNumberItem(String name) {
        return addNumberItem(name, null, null, null);
    }

    public Item addNumberItem(String name, Double value) {
        return addNumberItem(name, value, null, null);
    }

    public Item addNumberItem(String name, Double value, String label) {
        return addNumberItem(name, value, label, null);
    }

    public Item addNumberItem(String name, Double value, String label, String[] groupNames) {
        final NumberItem numberItem = new NumberItem(name);
        if (value != null) {
            numberItem.setState(new DecimalType(value));
        }
        if (label != null) {
            numberItem.setLabel(label);
        }
        if (groupNames != null && groupNames.length > 0) {
            Arrays.stream(groupNames).forEach(g -> numberItem.addGroupName(g));
        }
        return itemRegistry.add(numberItem);
    }

    public Item addStringItem(String name) {
        return addStringItem(name, null, null, null);
    }

    public Item addStringItem(String name, String value) {
        return addStringItem(name, value, null, null);
    }

    public Item addStringItem(String name, String value, String label) {
        return addStringItem(name, value, label, null);
    }

    public Item addSwitchItem(String name, Boolean value, String label, String[] groupNames) {
        final SwitchItem switchItem = new SwitchItem(name);
        if (value != null) {
            switchItem.setState(OnOffType.from(value));
        }
        if (label != null) {
            switchItem.setLabel(label);
        }
        if (groupNames != null && groupNames.length > 0) {
            Arrays.stream(groupNames).forEach(g -> switchItem.addGroupName(g));
        }
        return itemRegistry.add(switchItem);
    }

    public GroupItem addGroupItem(String name, String label) {
        GroupItem groupItem = new GroupItem(name);
        groupItem.setLabel(label);
        return (GroupItem) itemRegistry.add(groupItem);
    }

    public void removeItem(String name) {
        itemRegistry.remove(name, true);
    }

    public Item addStringItem(String name, String value, String label, String[] groupNames) {
        final StringItem stringItem = new StringItem(name);
        if (value != null) {
            stringItem.setState(new StringType(value));
        }
        if (label != null) {
            stringItem.setLabel(label);
        }
        if (groupNames != null && groupNames.length > 0) {
            Arrays.stream(groupNames).forEach(g -> stringItem.addGroupName(g));
        }
        return itemRegistry.add(stringItem);
    }

    public void linkItemWithChannel(String itemName, ChannelUID uid) {
        ItemChannelLink link = new ItemChannelLink(itemName, uid);
        itemChannelLinkRegistry.add(link);
    }

    public Collection<ItemChannelLink> getChannelLinks(String itemName) {
        return itemChannelLinkRegistry.getLinks(itemName);
    }

    public void unlinkItemFromChannel(String itemName) {
        itemChannelLinkRegistry.removeLinksForItem(itemName);
    }

    public Collection<Item> getItemsWithMetadata(String namespace, String value) {
        Set<Item> itemsWithMatchingMetadata = itemRegistry.getItems().stream().filter(item -> {
            MetadataKey key = new MetadataKey(namespace, item.getName());
            Metadata metadata = metadataRegistry.get(key);
            return metadata != null && metadata.getValue().equals(value);
        }).collect(Collectors.toSet());
        return itemsWithMatchingMetadata;
    }

    public Map<String, Object> getItemMetadataConfiguration(String namespace, String value, Item item) {
        MetadataKey key = new MetadataKey(namespace, item.getName());
        Metadata metadata = metadataRegistry.get(key);
        if (metadata != null && metadata.getValue().equals(value)) {
            return metadata.getConfiguration();
        } else {
            return null;
        }
    }
}
