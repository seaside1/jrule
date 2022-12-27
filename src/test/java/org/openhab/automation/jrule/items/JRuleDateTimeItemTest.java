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
    public void testSendCommand() {
        JRuleDateTimeItem item = (JRuleDateTimeItem) getJRuleItem();
        ZonedDateTime now = ZonedDateTime.now();
        item.sendCommand(new JRuleDateTimeValue(now));

        // JRuleDateTimeValue
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // send plain
        item.sendCommand(now);
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // verify event calls
        verifyEventTypes(0, 2);
    }

    @Test
    public void testPostUpdate() {
        JRuleDateTimeItem item = (JRuleDateTimeItem) getJRuleItem();
        ZonedDateTime now = ZonedDateTime.now();
        item.postUpdate(new JRuleDateTimeValue(now));

        // JRuleDateTimeValue
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // send plain
        item.postUpdate(now);
        Assertions.assertEquals(now.withFixedOffsetZone(), item.getStateAsDateTime().getValue().withFixedOffsetZone());

        // verify event calls
        verifyEventTypes(2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalDateTimeItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleDateTimeValue(ZonedDateTime.now());
    }

    @Override
    protected GenericItem getOhItem() {
        return new DateTimeItem("Name");
    }
}
