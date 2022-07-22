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

/**
 * The {@link JRuleItemRegistry} Items
 *
 * @author Gerhard Riegler - Initial contribution
 */

public class JRuleItemRegistry {
    private static final Map<String, JRuleItem> itemRegistry = new HashMap<>();

    public static void clear() {
        itemRegistry.clear();
    }

    public static <T> T get(String itemName, Class<T> jRuleItemClass) {
        JRuleItem jruleItem = itemRegistry.get(itemName);
        if (jruleItem == null) {
            try {
                Constructor<T> constructor = jRuleItemClass.getDeclaredConstructor(String.class);
                constructor.setAccessible(true);
                jruleItem = (JRuleItem) constructor.newInstance(itemName);
                itemRegistry.put(itemName, jruleItem);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return (T) jruleItem;
    }
}
