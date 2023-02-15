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

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalDimmerItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.DimmerItem;

/**
 * The {@link JRuleDimmerItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleDimmerItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand(TestInfo testInfo) {
        JRuleDimmerItem item = (JRuleDimmerItem) getJRuleItem();
        item.sendCommand(17);

        // percent
        Assertions.assertEquals(17, item.getStateAsPercent().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // send off
        item.sendCommand(false);
        Assertions.assertEquals(0, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // send percent
        item.sendCommand(new JRulePercentValue(22));
        Assertions.assertEquals(22, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(testInfo, 0, 3);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleDimmerItem item = (JRuleDimmerItem) getJRuleItem();
        item.postUpdate(17);

        // percent
        Assertions.assertEquals(17, item.getStateAsPercent().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // send off
        item.postUpdate(false);
        Assertions.assertEquals(0, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // send percent
        item.postUpdate(new JRulePercentValue(22));
        Assertions.assertEquals(22, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(testInfo, 3, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalDimmerItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRulePercentValue(75);
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new DimmerItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleDimmerItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleDimmerItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleDimmerItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleDimmerItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
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
        return (T) JRuleDimmerGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleDimmerGroupItem.forNameOptional(name);
    }
}
