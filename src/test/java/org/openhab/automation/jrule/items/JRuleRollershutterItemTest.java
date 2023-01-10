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
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.items.JRuleInternalRollershutterItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.RollershutterItem;

import java.util.List;
import java.util.Map;

/**
 * The {@link JRuleRollershutterItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleRollershutterItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand(TestInfo testInfo) {
        JRuleRollershutterItem item = (JRuleRollershutterItem) getJRuleItem();
        item.sendCommand(0);

        // percent
        Assertions.assertEquals(0, item.getStateAsPercent().intValue());

        // up/down
        Assertions.assertEquals(JRuleUpDownValue.UP, item.getStateAs(JRuleUpDownValue.class));

        // send down
        item.sendCommand(JRuleUpDownValue.DOWN);
        Assertions.assertEquals(100, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleUpDownValue.DOWN, item.getStateAs(JRuleUpDownValue.class));

        // send percent
        item.sendCommand(new JRulePercentValue(22));
        Assertions.assertEquals(22, item.getStateAsPercent().intValue());
        Assertions.assertThrows(JRuleRuntimeException.class, () -> item.getStateAs(JRuleUpDownValue.class));

        // verify event calls
        verifyEventTypes(testInfo, 0, 3);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleRollershutterItem item = (JRuleRollershutterItem) getJRuleItem();
        item.postUpdate(17);

        // percent
        Assertions.assertEquals(17, item.getStateAsPercent().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // send down
        item.postUpdate(JRuleUpDownValue.DOWN);
        Assertions.assertEquals(100, item.getStateAsPercent().intValue());

        // send percent
        item.postUpdate(new JRulePercentValue(22));
        Assertions.assertEquals(22, item.getStateAsPercent().intValue());

        // verify event calls
        verifyEventTypes(testInfo, 3, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalRollershutterItem("Name", "Label", "Type", "Id", Map.of(), List.of());
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRulePercentValue(75);
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new RollershutterItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleRollershutterItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class,
                () -> JRuleRollershutterItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleRollershutterItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleRollershutterItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    protected <T extends JRuleGroupItem> T groupForNameMethod(String name) {
        return (T) JRuleRollershutterGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleRollershutterGroupItem.forNameOptional(name);
    }
}
