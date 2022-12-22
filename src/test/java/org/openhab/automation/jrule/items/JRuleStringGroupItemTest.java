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
import org.openhab.automation.jrule.internal.items.JRuleInternalStringGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.StringItem;

/**
 * The {@link JRuleStringGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleStringGroupItemTest extends JRuleStringItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalStringGroupItem("Group", "Label", "Type", "Id");
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
        Assertions.assertNotNull(JRuleStringGroupItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class,
                () -> JRuleStringGroupItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleStringGroupItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleStringGroupItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
