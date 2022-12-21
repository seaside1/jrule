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
import org.openhab.automation.jrule.internal.items.JRuleInternalNumberItem;
import org.openhab.automation.jrule.rules.value.*;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.NumberItem;

import java.util.Map;

/**
 * The {@link JRuleNumberItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleNumberItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand() {
        JRuleNumberItem item = (JRuleNumberItem) getJRuleItem();
        item.sendCommand(17);

        // decimal
        Assertions.assertEquals(17, item.getStateAsDecimal().intValue());

        // send quantity
        item.sendCommand(new JRuleQuantityValue<>("12mV"));
        Assertions.assertEquals(12, item.getStateAsDecimal().intValue());

        // send jrule-decimal
        item.sendCommand(new JRuleDecimalValue(22));
        Assertions.assertEquals(22, item.getStateAsDecimal().intValue());

        // verify event calls
        verifyEventTypes(0, 3);
    }

    @Test
    public void testPostUpdate() {
        JRuleNumberItem item = (JRuleNumberItem) getJRuleItem();
        item.postUpdate(17);

        // decimal
        Assertions.assertEquals(17, item.getStateAsDecimal().intValue());

        // send quantity
        item.postUpdate(new JRuleQuantityValue<>("12mV"));
        Assertions.assertEquals(12, item.getStateAsDecimal().intValue());

        // send jrule-decimal
        item.postUpdate(new JRuleDecimalValue(22));
        Assertions.assertEquals(22, item.getStateAsDecimal().intValue());

        // verify event calls
        verifyEventTypes(3, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalNumberItem("Name", "Label", "Type", "Id", Map.of());
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleDecimalValue(75);
    }

    @Override
    protected GenericItem getOhItem() {
        return new NumberItem("Name");
    }
}
