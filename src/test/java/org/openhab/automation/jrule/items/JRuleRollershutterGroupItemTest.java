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
import org.openhab.automation.jrule.internal.items.JRuleInternalRollershutterGroupItem;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.RollershutterItem;

/**
 * The {@link JRuleRollershutterGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleRollershutterGroupItemTest extends JRuleRollershutterItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalRollershutterGroupItem("Group", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRulePercentValue(75);
    }

    @Override
    protected GenericItem getOhItem() {
        return new RollershutterItem("Name");
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleRollershutterGroupItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class,
                () -> JRuleRollershutterGroupItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleRollershutterGroupItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleRollershutterGroupItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
