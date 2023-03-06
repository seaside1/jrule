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
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
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

        List<Pair<? extends Item, Class<? extends JRuleItem>>> items = JRuleItemTestUtils.getAllDummyItems();

        ItemRegistry itemRegistry = Mockito.mock(ItemRegistry.class);
        Mockito.when(itemRegistry.getItem(Mockito.anyString())).thenAnswer(invocationOnMock -> {
            Object itemName = invocationOnMock.getArgument(0);
            return items.stream().filter(item -> item.getKey().getName().equals(itemName)).findFirst().orElseThrow();
        });
        JRuleEventHandler.get().setItemRegistry(itemRegistry);

        items.forEach((entry) -> {
            JRuleItem item = JRuleItem.forName(entry.getKey().getName());
            Assertions.assertNotNull(item);
            Assertions.assertEquals(entry.getKey().getName(), item.getName());
            Assertions.assertTrue(entry.getValue().isAssignableFrom(item.getClass()),
                    String.format("value '%s' is assignable from '%s'", entry.getValue(), item.getClass()));
        });
    }
}
