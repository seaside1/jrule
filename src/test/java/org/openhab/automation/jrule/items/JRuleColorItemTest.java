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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalColorItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ColorItem;

/**
 * The {@link JRuleColorItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleColorItemTest extends JRuleItemTestBase {

    @Test
    public void testSendCommand(TestInfo testInfo) {
        JRuleColorItem item = (JRuleColorItem) getJRuleItem();
        item.sendCommand(new JRuleHsbValue(1, 2, 3));

        // hsb
        Assertions.assertEquals(1, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(2, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(3, item.getStateAsHsb().getBrightness().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // percent
        Assertions.assertEquals(3, item.getStateAsPercent().intValue());

        // send percent
        item.sendCommand(12);
        Assertions.assertEquals(12, item.getStateAsHsb().getBrightness().intValue());

        // send off
        item.sendCommand(false);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAsOnOff());

        // send hsb
        item.sendCommand(new JRuleHsbValue(5, 6, 7));
        Assertions.assertEquals(5, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(6, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(7, item.getStateAsHsb().getBrightness().intValue());

        // verify event calls
        verifyEventTypes(testInfo, 0, 4);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleColorItem item = (JRuleColorItem) getJRuleItem();
        item.postUpdate(new JRuleHsbValue(1, 2, 3));

        // hsb
        Assertions.assertEquals(1, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(2, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(3, item.getStateAsHsb().getBrightness().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // percent
        Assertions.assertEquals(3, item.getStateAsPercent().intValue());

        // send percent
        item.postUpdate(12);
        Assertions.assertEquals(12, item.getStateAsHsb().getBrightness().intValue());

        // send off
        item.postUpdate(false);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAsOnOff());

        // send hsb
        item.postUpdate(new JRuleHsbValue(5, 6, 7));
        Assertions.assertEquals(5, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(6, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(7, item.getStateAsHsb().getBrightness().intValue());

        // verify event calls
        verifyEventTypes(testInfo, 4, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalColorItem(ITEM_NAME, "Label", "Type", "Id", mock, List.of("Lighting", "Inside"));
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleHsbValue(1, 2, 3);
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new ColorItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleColorItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleColorItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleColorItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleColorItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    @Test
    public void testForNameAsDimmer() {
        Assertions.assertNotNull(JRuleDimmerItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleDimmerItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleDimmerItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleDimmerItem.forNameOptional(ITEM_NON_EXISTING).isPresent());

        JRuleDimmerItem item = JRuleDimmerItem.forName(ITEM_NAME);
        item.sendCommand(55);
        Assertions.assertEquals(55, item.getStateAsPercent().intValue());
        item.sendCommand(77);
        Assertions.assertEquals(77, item.getStateAsPercent().intValue());
    }

    @Test
    public void testForNameAsSwitch() {
        Assertions.assertNotNull(JRuleSwitchItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleSwitchItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleSwitchItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleSwitchItem.forNameOptional(ITEM_NON_EXISTING).isPresent());

        JRuleSwitchItem item = JRuleSwitchItem.forName(ITEM_NAME);
        item.sendCommand(false);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAsOnOff());
        item.sendCommand(true);
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAsOnOff());
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> T groupForNameMethod(String name) {
        return (T) JRuleColorGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleColorGroupItem.forNameOptional(name);
    }
}
