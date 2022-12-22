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
import org.openhab.automation.jrule.internal.items.JRuleInternalCallGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleStringListValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.CallItem;

/**
 * The {@link JRuleCallGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleCallGroupItemTest extends JRuleCallItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalCallGroupItem("Group", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleStringListValue(List.of("123"));
    }

    @Override
    protected GenericItem getOhItem() {
        return new CallItem(ITEM_NAME);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleCallGroupItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleCallGroupItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleCallGroupItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleCallGroupItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
