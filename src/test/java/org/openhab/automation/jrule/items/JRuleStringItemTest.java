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
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalStringItem;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.StringItem;

/**
 * The {@link JRuleStringItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleStringItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand() {
        JRuleStringItem item = (JRuleStringItem) getJRuleItem();
        item.sendCommand("abc");

        // JRuleStringValue
        Assertions.assertEquals("abc", item.getStateAsString());

        // send cde
        item.sendCommand(new JRuleStringValue("cde"));
        Assertions.assertEquals("cde", item.getStateAsString());

        // verify event calls
        verifyEventTypes(0, 2);
    }

    @Test
    public void testPostUpdate() {
        JRuleStringItem item = (JRuleStringItem) getJRuleItem();
        item.postUpdate("abc");

        // JRuleStringValue
        Assertions.assertEquals("abc", item.getStateAsString());

        // send cde
        item.postUpdate(new JRuleStringValue("cde"));
        Assertions.assertEquals("cde", item.getStateAsString());

        // verify event calls
        verifyEventTypes(2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalStringItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleStringValue("abc");
    }

    @Override
    protected GenericItem getOhItem() {
        return new StringItem("Name");
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleStringItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleStringItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleStringItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleStringItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
