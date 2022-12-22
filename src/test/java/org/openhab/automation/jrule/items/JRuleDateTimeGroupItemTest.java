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

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalDateTimeGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.DateTimeItem;

/**
 * The {@link JRuleDateTimeGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleDateTimeGroupItemTest extends JRuleDateTimeItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalDateTimeGroupItem("Group", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleDateTimeValue(ZonedDateTime.now());
    }

    @Override
    protected GenericItem getOhItem() {
        return new DateTimeItem("Name");
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleDateTimeGroupItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class,
                () -> JRuleDateTimeGroupItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleDateTimeGroupItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleDateTimeGroupItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }
}
