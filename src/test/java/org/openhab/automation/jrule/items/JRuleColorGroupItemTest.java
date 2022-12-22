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
import org.openhab.automation.jrule.internal.items.JRuleInternalColorGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ColorItem;

/**
 * The {@link JRuleColorGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleColorGroupItemTest extends JRuleColorItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalColorGroupItem("Group", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleHsbValue(1, 2, 3);
    }

    @Override
    protected GenericItem getOhItem() {
        return new ColorItem("Name");
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleColorGroupItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleColorGroupItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleColorGroupItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleColorGroupItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
