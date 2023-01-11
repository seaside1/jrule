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
import org.openhab.automation.jrule.internal.items.JRuleInternalSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.SwitchItem;

/**
 * The {@link JRuleSwitchItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleSwitchItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand(TestInfo testInfo) {
        JRuleSwitchItem item = (JRuleSwitchItem) getJRuleItem();
        item.sendCommand(true);

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getState());

        // send off
        item.sendCommand(JRuleOnOffValue.OFF);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(testInfo, 0, 2);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleSwitchItem item = (JRuleSwitchItem) getJRuleItem();
        item.postUpdate(true);

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getState());

        // send off
        item.postUpdate(JRuleOnOffValue.OFF);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(testInfo, 2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalSwitchItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return JRuleOnOffValue.ON;
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new SwitchItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleSwitchItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleSwitchItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleSwitchItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleSwitchItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    @Test
    public void testForNameOptional() {
        JRuleSwitchItem.forNameOptional(ITEM_NAME).ifPresent(item -> item.sendCommand(true));
    }

    protected <T extends JRuleGroupItem> T groupForNameMethod(String name) {
        return (T) JRuleSwitchGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleSwitchGroupItem.forNameOptional(name);
    }
}
