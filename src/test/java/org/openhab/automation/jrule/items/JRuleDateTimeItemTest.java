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
import java.util.Optional;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalDateTimeItem;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.DateTimeItem;

/**
 * The {@link JRuleDateTimeItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleDateTimeItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand(TestInfo testInfo) {
        JRuleDateTimeItem item = (JRuleDateTimeItem) getJRuleItem();
        ZonedDateTime now = ZonedDateTime.now();
        item.sendCommand(new JRuleDateTimeValue(now));

        // JRuleDateTimeValue
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // send plain
        item.sendCommand(now);
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // verify event calls
        verifyEventTypes(testInfo, 0, 2);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleDateTimeItem item = (JRuleDateTimeItem) getJRuleItem();
        ZonedDateTime now = ZonedDateTime.now();
        item.postUpdate(new JRuleDateTimeValue(now));

        // JRuleDateTimeValue
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // send plain
        item.postUpdate(now);
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // verify event calls
        verifyEventTypes(testInfo, 2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalDateTimeItem("Name", "Label", "Type", "Id", Map.of(), List.of());
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleDateTimeValue(ZonedDateTime.now());
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new DateTimeItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleDateTimeItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleDateTimeItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleDateTimeItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleDateTimeItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    protected <T extends JRuleGroupItem> T groupForNameMethod(String name) {
        return (T) JRuleDateTimeGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleDateTimeGroupItem.forNameOptional(name);
    }
}
