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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
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

    public static void clear() {
        itemRegistry.clear();
    }

    static {
        typeMap.put(GroupItem.TYPE, JRuleGroupItem.class);
        typeMap.put(CoreItemFactory.CALL, JRuleCallItem.class);
        typeMap.put(CoreItemFactory.CONTACT, JRuleContactItem.class);
        typeMap.put(CoreItemFactory.COLOR, JRuleColorItem.class);
        typeMap.put(CoreItemFactory.DATETIME, JRuleDateTimeItem.class);
        typeMap.put(CoreItemFactory.DIMMER, JRuleDimmerItem.class);
        typeMap.put(CoreItemFactory.IMAGE, JRuleImageItem.class);
        typeMap.put(CoreItemFactory.LOCATION, JRuleLocationItem.class);
        typeMap.put(CoreItemFactory.NUMBER, JRuleNumberItem.class);
        typeMap.put(CoreItemFactory.PLAYER, JRulePlayerItem.class);
        typeMap.put(CoreItemFactory.ROLLERSHUTTER, JRuleRollershutterItem.class);
        typeMap.put(CoreItemFactory.STRING, JRuleStringItem.class);
        typeMap.put(CoreItemFactory.SWITCH, JRuleSwitchItem.class);

        groupTypeMap.put(CoreItemFactory.CALL, JRuleGroupCallItem.class);
        groupTypeMap.put(CoreItemFactory.CONTACT, JRuleGroupContactItem.class);
        groupTypeMap.put(CoreItemFactory.COLOR, JRuleGroupColorItem.class);
        groupTypeMap.put(CoreItemFactory.DATETIME, JRuleGroupDateTimeItem.class);
        groupTypeMap.put(CoreItemFactory.DIMMER, JRuleGroupDimmerItem.class);
        groupTypeMap.put(CoreItemFactory.IMAGE, JRuleGroupImageItem.class);
        groupTypeMap.put(CoreItemFactory.LOCATION, JRuleGroupLocationItem.class);
        groupTypeMap.put(CoreItemFactory.NUMBER, JRuleGroupNumberItem.class);
        groupTypeMap.put(CoreItemFactory.PLAYER, JRuleGroupPlayerItem.class);
        groupTypeMap.put(CoreItemFactory.ROLLERSHUTTER, JRuleGroupRollershutterItem.class);
        groupTypeMap.put(CoreItemFactory.STRING, JRuleGroupStringItem.class);
        groupTypeMap.put(CoreItemFactory.SWITCH, JRuleGroupSwitchItem.class);
    }

    public static JRuleItem get(String itemName) throws JRuleItemNotFoundException {
        JRuleItem jruleItem = itemRegistry.get(itemName);
        if (jruleItem == null) {
            Item item = verifyThatItemExist(itemName);

            Class<? extends JRuleItem> jRuleItemClass = typeMap.get(item.getType());
            if (jRuleItemClass == JRuleGroupItem.class && item instanceof GroupItem) {
                String baseItemType = Optional.ofNullable(((GroupItem) item).getBaseItem()).map(Item::getType)
                        .orElse(CoreItemFactory.STRING);

                jRuleItemClass = groupTypeMap.get(baseItemType);
            }

            try {
                Constructor<? extends JRuleItem> constructor = jRuleItemClass.getDeclaredConstructor(String.class);
                constructor.setAccessible(true);
                jruleItem = constructor.newInstance(itemName);
                itemRegistry.put(itemName, jruleItem);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return jruleItem;
    }

    public static <T> T get(String itemName, Class<? extends JRuleItem> jRuleItemClass)
            throws JRuleItemNotFoundException {
        JRuleItem jruleItem = itemRegistry.get(itemName);
        if (jruleItem == null) {
            verifyThatItemExist(itemName);

            try {
                Constructor<? extends JRuleItem> constructor = jRuleItemClass.getDeclaredConstructor(String.class);
                constructor.setAccessible(true);
                jruleItem = constructor.newInstance(itemName);
                itemRegistry.put(itemName, jruleItem);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
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
}
