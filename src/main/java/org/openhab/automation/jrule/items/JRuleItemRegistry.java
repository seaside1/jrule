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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.*;
import org.openhab.automation.jrule.items.metadata.JRuleMetadataRegistry;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.*;
import org.openhab.core.library.CoreItemFactory;

/**
 * The {@link JRuleItemRegistry} Items
 *
 * @author Gerhard Riegler - Initial contribution
 */

public class JRuleItemRegistry {
    private static final Map<String, Class<? extends JRuleItem>> typeMap = new HashMap<>();
    private static final Map<String, Class<? extends JRuleItem>> groupTypeMap = new HashMap<>();
    private static final Map<String, JRuleItem> itemRegistry = new HashMap<>();
    private static MetadataRegistry metadataRegistry;

    public static void clear() {
        itemRegistry.clear();
    }

    public static final String ITEM_TYPE_QUANTITY = "Quantity";

    static {
        typeMap.put(GroupItem.TYPE, null);
        typeMap.put(CoreItemFactory.CALL, JRuleInternalCallItem.class);
        typeMap.put(CoreItemFactory.CONTACT, JRuleInternalContactItem.class);
        typeMap.put(CoreItemFactory.COLOR, JRuleInternalColorItem.class);
        typeMap.put(CoreItemFactory.DATETIME, JRuleInternalDateTimeItem.class);
        typeMap.put(CoreItemFactory.DIMMER, JRuleInternalDimmerItem.class);
        typeMap.put(CoreItemFactory.IMAGE, JRuleInternalImageItem.class);
        typeMap.put(CoreItemFactory.LOCATION, JRuleInternalLocationItem.class);
        typeMap.put(CoreItemFactory.NUMBER, JRuleInternalNumberItem.class);
        typeMap.put(ITEM_TYPE_QUANTITY, JRuleInternalQuantityItem.class);
        typeMap.put(CoreItemFactory.PLAYER, JRuleInternalPlayerItem.class);
        typeMap.put(CoreItemFactory.ROLLERSHUTTER, JRuleInternalRollershutterItem.class);
        typeMap.put(CoreItemFactory.STRING, JRuleInternalStringItem.class);
        typeMap.put(CoreItemFactory.SWITCH, JRuleInternalSwitchItem.class);

        groupTypeMap.put(CoreItemFactory.CALL, JRuleInternalCallGroupItem.class);
        groupTypeMap.put(CoreItemFactory.CONTACT, JRuleInternalContactGroupItem.class);
        groupTypeMap.put(CoreItemFactory.COLOR, JRuleInternalColorGroupItem.class);
        groupTypeMap.put(CoreItemFactory.DATETIME, JRuleInternalDateTimeGroupItem.class);
        groupTypeMap.put(CoreItemFactory.DIMMER, JRuleInternalDimmerGroupItem.class);
        groupTypeMap.put(CoreItemFactory.IMAGE, JRuleInternalImageGroupItem.class);
        groupTypeMap.put(CoreItemFactory.LOCATION, JRuleInternalLocationGroupItem.class);
        groupTypeMap.put(CoreItemFactory.NUMBER, JRuleInternalNumberGroupItem.class);
        groupTypeMap.put(ITEM_TYPE_QUANTITY, JRuleInternalQuantityGroupItem.class);
        groupTypeMap.put(CoreItemFactory.PLAYER, JRuleInternalPlayerGroupItem.class);
        groupTypeMap.put(CoreItemFactory.ROLLERSHUTTER, JRuleInternalRollershutterGroupItem.class);
        groupTypeMap.put(CoreItemFactory.STRING, JRuleInternalStringGroupItem.class);
        groupTypeMap.put(CoreItemFactory.SWITCH, JRuleInternalSwitchGroupItem.class);
        groupTypeMap.put(JRuleItemClassGenerator.ITEM_GROUP_TYPE_UNSPECIFIED, JRuleInternalUnspecifiedGroupItem.class);
    }

    public static <T extends JRuleValue> JRuleItem get(String itemName) throws JRuleItemNotFoundException {
        JRuleItem jRuleItem = itemRegistry.get(itemName);
        if (jRuleItem == null) {
            Item item = verifyThatItemExist(itemName);

            Class<? extends JRuleItem> jRuleItemClass = typeMap
                    .get(item.getType().contains(":") ? "Quantity" : item.getType());
            if (item instanceof GroupItem) {
                String baseItemType = Optional.ofNullable(((GroupItem) item).getBaseItem()).map(Item::getType)
                        .or(() -> Optional.of(JRuleItemClassGenerator.ITEM_GROUP_TYPE_UNSPECIFIED))
                        .map(s -> s.contains(":") ? "Quantity" : s)
                        .orElse(JRuleItemClassGenerator.ITEM_GROUP_TYPE_UNSPECIFIED);

                jRuleItemClass = groupTypeMap.get(baseItemType);
            }

            try {
                Constructor<? extends JRuleItem> constructor = jRuleItemClass.getDeclaredConstructor(String.class,
                        String.class, String.class, String.class, JRuleMetadataRegistry.class, List.class);
                constructor.setAccessible(true);
                jRuleItem = constructor.newInstance(itemName, item.getLabel(), item.getType(), item.getUID(),
                        new JRuleMetadataRegistry(metadataRegistry), item.getTags().stream().sorted().toList());
                itemRegistry.put(itemName, jRuleItem);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return jRuleItem;
    }

    public static <T> T get(String itemName, Class<? extends JRuleItem> jRuleItemClass)
            throws JRuleItemNotFoundException {
        JRuleItem jruleItem = itemRegistry.get(itemName);
        if (jruleItem == null) {
            Item item = verifyThatItemExist(itemName);

            try {
                Constructor<? extends JRuleItem> constructor = jRuleItemClass.getDeclaredConstructor(String.class,
                        String.class, String.class, String.class, JRuleMetadataRegistry.class, List.class);
                jruleItem = constructor.newInstance(item.getName(), item.getLabel(), item.getType(), item.getUID(),
                        new JRuleMetadataRegistry(metadataRegistry), item.getTags().stream().sorted().toList());
                itemRegistry.put(itemName, jruleItem);
            } catch (Exception ex) {
                throw new RuntimeException(String.format("cannot create item '%s' for type '%s'", itemName,
                        jRuleItemClass.getSimpleName()), ex);
            }
        }
        return (T) jruleItem;
    }

    private static Item verifyThatItemExist(String itemName) throws JRuleItemNotFoundException {
        try {
            ItemRegistry itemRegistry = JRuleEventHandler.get().getItemRegistry();
            if (itemRegistry == null) {
                throw new IllegalStateException(
                        String.format("Item registry is not set can't get item for name: %s", itemName));
            }
            return itemRegistry.getItem(itemName);
        } catch (ItemNotFoundException e) {
            throw new JRuleItemNotFoundException(String.format("cannot find item for name: %s", itemName), e);
        }
    }

    public static void setMetadataRegistry(MetadataRegistry metadataRegistry) {
        JRuleItemRegistry.metadataRegistry = metadataRegistry;
    }
}
