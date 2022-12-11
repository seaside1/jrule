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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalColorItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.PercentType;

/**
 * The {@link JRuleColorItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleColorItemTest {
    @Test
    public void testSendCommand() throws ItemNotFoundException {
        ItemRegistry itemRegistry = Mockito.mock(ItemRegistry.class);
        ColorItem ohItem = new ColorItem("Name");
        ohItem.setState(new HSBType(new DecimalType(1), new PercentType(2), new PercentType(3)));
        Mockito.when(itemRegistry.getItem(Mockito.anyString())).thenReturn(ohItem);

        JRuleEventHandler.get().setItemRegistry(itemRegistry);

        JRuleColorItem item = new JRuleInternalColorItem("Name", "Label", "Type", "Id");
        JRuleHsbValue command = new JRuleHsbValue(1, 2, 3);
        item.sendCommand(command);
        JRuleHsbValue state = item.getState();
        Assertions.assertNotNull(state);
        JRuleOnOffValue asOnOffValue = item.getStateAs(JRuleOnOffValue.class);
        Assertions.assertNotNull(asOnOffValue);
        Assertions.assertEquals(JRuleOnOffValue.ON, asOnOffValue);
    }
}
