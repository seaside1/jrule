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

import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;

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

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
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

    public Item addNumberItem(String name, int value) {
        final NumberItem numberItem = new NumberItem(name);
        numberItem.setState(new DecimalType(value));
        return itemRegistry.add(numberItem);
    }

    public Item addStringItem(String name, String value) {
        final StringItem stringItem = new StringItem(name);
        stringItem.setState(new StringType(value));
        return itemRegistry.add(stringItem);
    }
}
