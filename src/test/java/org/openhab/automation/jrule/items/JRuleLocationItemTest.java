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
import org.openhab.automation.jrule.internal.items.JRuleInternalLocationItem;
import org.openhab.automation.jrule.rules.value.JRulePointValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.LocationItem;

import java.util.Map;

/**
 * The {@link JRuleLocationItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleLocationItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand() {
        JRuleLocationItem item = (JRuleLocationItem) getJRuleItem();
        item.sendCommand(new JRulePointValue(12.2, 22.17));

        // JRuleStringValue
        Assertions.assertEquals(12.2, item.getStateAsPoint().getLatitude().doubleValue());
        Assertions.assertEquals(22.17, item.getStateAsPoint().getLongitude().doubleValue());

        // send with altitude
        item.sendCommand(new JRulePointValue(56.12, 95.1, 12.1));
        Assertions.assertEquals(56.12, item.getStateAsPoint().getLatitude().doubleValue());
        Assertions.assertEquals(95.1, item.getStateAsPoint().getLongitude().doubleValue());
        Assertions.assertEquals(12.1, item.getStateAsPoint().getAltitude().doubleValue());

        // verify event calls
        verifyEventTypes(0, 2);
    }

    @Test
    public void testPostUpdate() {
        JRuleLocationItem item = (JRuleLocationItem) getJRuleItem();
        item.postUpdate(new JRulePointValue(12.2, 22.17));

        // JRuleStringValue
        Assertions.assertEquals(12.2, item.getStateAsPoint().getLatitude().doubleValue());
        Assertions.assertEquals(22.17, item.getStateAsPoint().getLongitude().doubleValue());

        // send with altitude
        item.postUpdate(new JRulePointValue(56.12, 95.1, 12.1));
        Assertions.assertEquals(56.12, item.getStateAsPoint().getLatitude().doubleValue());
        Assertions.assertEquals(95.1, item.getStateAsPoint().getLongitude().doubleValue());
        Assertions.assertEquals(12.1, item.getStateAsPoint().getAltitude().doubleValue());

        // verify event calls
        verifyEventTypes(2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalLocationItem("Name", "Label", "Type", "Id", Map.of());
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRulePointValue(1, 2);
    }

    @Override
    protected GenericItem getOhItem() {
        return new LocationItem("Name");
    }
}
