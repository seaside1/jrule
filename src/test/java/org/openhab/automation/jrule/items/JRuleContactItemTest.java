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
import org.openhab.automation.jrule.internal.items.JRuleInternalContactItem;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ContactItem;

/**
 * The {@link JRuleContactItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleContactItemTest extends JRuleItemTestBase {
    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleContactItem item = (JRuleContactItem) getJRuleItem();
        item.postUpdate(JRuleOpenClosedValue.OPEN);

        // open/closed
        Assertions.assertEquals(JRuleOpenClosedValue.OPEN, item.getStateAs(JRuleOpenClosedValue.class));

        // send closed
        item.postUpdate(JRuleOpenClosedValue.CLOSED);
        Assertions.assertEquals(JRuleOpenClosedValue.CLOSED, item.getStateAsOpenClose());

        // verify event calls
        verifyEventTypes(testInfo, 2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalContactItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return JRuleOpenClosedValue.OPEN;
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new ContactItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleContactItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleContactItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleContactItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleContactItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    protected <T extends JRuleGroupItem> T groupForNameMethod(String name) {
        return (T) JRuleContactGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleContactGroupItem.forNameOptional(name);
    }
}
