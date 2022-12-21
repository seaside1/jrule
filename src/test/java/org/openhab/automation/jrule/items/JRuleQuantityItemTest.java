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
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.items.JRuleInternalNumberItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleQuantityValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.NumberItem;

/**
 * The {@link JRuleQuantityItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleQuantityItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand() {
        JRuleNumberItem item = (JRuleNumberItem) getJRuleItem();
        item.sendCommand(17);

        // decimal
        Assertions.assertEquals(17, item.getStateAsDecimal().intValue());

        // send quantity
        item.sendCommand(new JRuleQuantityValue<>("12mV"));
        Assertions.assertEquals(12, item.getStateAsDecimal().intValue());
        Assertions.assertEquals("mV", item.getStateAsQuantity().unit());

        // send jrule-decimal
        item.sendCommand(new JRuleDecimalValue(2200));
        Assertions.assertEquals(2200, item.getStateAsDecimal().intValue());
        Assertions.assertThrows(JRuleRuntimeException.class, () -> item.getStateAsQuantity().unit());

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
        Assertions.assertEquals("mV", item.getStateAsQuantity().unit());

        // send jrule-decimal
        item.postUpdate(new JRuleDecimalValue(2200));
        Assertions.assertEquals(2200, item.getStateAsDecimal().intValue());
        Assertions.assertThrows(JRuleRuntimeException.class, () -> item.getStateAsQuantity().unit());

        // verify event calls
        verifyEventTypes(3, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalNumberItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleDecimalValue(75);
    }

    @Override
    protected GenericItem getOhItem() {
        return new NumberItem("Number:ElectricPotential", "Name");
    }
}
