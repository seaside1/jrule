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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.test_utils.JRuleItemTestUtils;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.MetadataRegistry;

/**
 * The {@link JRuleItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemTest {

    @Test
    public void testForName() throws ItemNotFoundException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        MetadataRegistry metadataRegistry = Mockito.mock(MetadataRegistry.class);
        JRuleItemRegistry.setMetadataRegistry(metadataRegistry);

        Map<Item, Class<? extends JRuleItem>> items = JRuleItemTestUtils.getAllDummyItems();

        ItemRegistry itemRegistry = Mockito.mock(ItemRegistry.class);
        Mockito.when(itemRegistry.getItem(Mockito.anyString())).thenAnswer(invocationOnMock -> {
            Object itemName = invocationOnMock.getArgument(0);
            return items.keySet().stream().filter(item -> item.getName().equals(itemName)).findFirst().orElseThrow();
        });
        JRuleEventHandler.get().setItemRegistry(itemRegistry);

        items.forEach((ohItem, value) -> {
            JRuleItem item = JRuleItem.forName(ohItem.getName());
            Assertions.assertNotNull(item);
            Assertions.assertEquals(ohItem.getName(), item.getName());
            Assertions.assertTrue(value.isAssignableFrom(item.getClass()),
                    String.format("value '%s' is assignable from '%s'", value, item.getClass()));
        });
    }
}
