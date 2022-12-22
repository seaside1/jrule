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
import org.openhab.automation.jrule.internal.items.JRuleInternalNumberGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.NumberItem;

/**
 * The {@link JRuleNumberGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleNumberGroupItemTest extends JRuleNumberItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalNumberGroupItem("Group", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleDecimalValue(75);
    }

    @Override
    protected GenericItem getOhItem() {
        return new NumberItem("Name");
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleNumberGroupItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class,
                () -> JRuleNumberGroupItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleNumberGroupItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleNumberGroupItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
