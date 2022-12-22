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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalCallItem;
import org.openhab.automation.jrule.rules.value.JRuleStringListValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.CallItem;

/**
 * The {@link JRuleCallItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleCallItemTest extends JRuleItemTestBase {
    @Test
    public void testPostUpdate() {
        JRuleCallItem item = (JRuleCallItem) getJRuleItem();
        item.postUpdate(new JRuleStringListValue("123", "345"));

        // verify
        Assertions.assertEquals("123", item.getStateAsStringList().getValue(0));
        Assertions.assertEquals("345", item.getStateAsStringList().getValue(1));

        // send another value
        item.postUpdate(new JRuleStringListValue("999"));
        Assertions.assertEquals("999", item.getStateAsStringList().getValue(0));

        // verify event calls
        verifyEventTypes(2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalCallItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleStringListValue(List.of("123"));
    }

    @Override
    protected GenericItem getOhItem() {
        return new CallItem("Name");
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleCallItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleCallItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleCallItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleCallItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
