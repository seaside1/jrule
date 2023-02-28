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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openhab.automation.jrule.internal.items.JRuleInternalQuantityItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRuleQuantityValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.NumberItem;

/**
 * The {@link JRuleQuantityItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleQuantityItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand(TestInfo testInfo) {
        JRuleQuantityItem item = (JRuleQuantityItem) getJRuleItem();
        item.sendCommand(17, "V");

        // decimal
        Assertions.assertEquals(17, item.getStateAsDecimal().intValue());
        Assertions.assertEquals(17, item.getStateAsQuantity().intValue());
        Assertions.assertEquals("V", item.getStateAsQuantity().unit());

        // send quantity
        item.sendCommand(new JRuleQuantityValue("12mV"));
        Assertions.assertEquals(12, item.getStateAsDecimal().intValue());
        Assertions.assertEquals("mV", item.getStateAsQuantity().unit());

        // verify event calls
        verifyEventTypes(testInfo, 0, 2);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleQuantityItem item = (JRuleQuantityItem) getJRuleItem();
        item.postUpdate(17, "V");

        // decimal
        Assertions.assertEquals(17, item.getStateAsDecimal().intValue());
        Assertions.assertEquals(17, item.getStateAsQuantity().intValue());
        Assertions.assertEquals("V", item.getStateAsQuantity().unit());

        // send quantity
        item.postUpdate(new JRuleQuantityValue("12mV"));
        Assertions.assertEquals(12, item.getStateAsDecimal().intValue());
        Assertions.assertEquals("mV", item.getStateAsQuantity().unit());

        // verify event calls
        verifyEventTypes(testInfo, 2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalQuantityItem(ITEM_NAME, "Label", "Type", "Id",
                Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom"))),
                List.of("Lighting", "Inside"));
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleQuantityValue(10, "A");
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new NumberItem("Number:ElectricPotential", name);
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> T groupForNameMethod(String name) {
        return (T) JRuleQuantityGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleQuantityGroupItem.forNameOptional(name);
    }
}
