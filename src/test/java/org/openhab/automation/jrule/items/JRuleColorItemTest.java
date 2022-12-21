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
import org.openhab.automation.jrule.internal.items.JRuleInternalColorItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ColorItem;

import java.util.List;
import java.util.Map;

/**
 * The {@link JRuleColorItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleColorItemTest extends JRuleItemTestBase {

    @Test
    public void testSendCommand() {
        JRuleColorItem item = (JRuleColorItem) getJRuleItem();
        item.sendCommand(new JRuleHsbValue(1, 2, 3));

        // hsb
        Assertions.assertEquals(1, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(2, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(3, item.getStateAsHsb().getBrightness().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // percent
        Assertions.assertEquals(3, item.getStateAsPercent().intValue());

        // send percent
        item.sendCommand(12);
        Assertions.assertEquals(12, item.getStateAsHsb().getBrightness().intValue());

        // send off
        item.sendCommand(false);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAsOnOff());

        // send hsb
        item.sendCommand(new JRuleHsbValue(5, 6, 7));
        Assertions.assertEquals(5, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(6, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(7, item.getStateAsHsb().getBrightness().intValue());

        // verify event calls
        verifyEventTypes(0, 4);
    }

    @Test
    public void testPostUpdate() {
        JRuleColorItem item = (JRuleColorItem) getJRuleItem();
        item.postUpdate(new JRuleHsbValue(1, 2, 3));

        // hsb
        Assertions.assertEquals(1, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(2, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(3, item.getStateAsHsb().getBrightness().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // percent
        Assertions.assertEquals(3, item.getStateAsPercent().intValue());

        // send percent
        item.postUpdate(12);
        Assertions.assertEquals(12, item.getStateAsHsb().getBrightness().intValue());

        // send off
        item.postUpdate(false);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAsOnOff());

        // send hsb
        item.postUpdate(new JRuleHsbValue(5, 6, 7));
        Assertions.assertEquals(5, item.getStateAsHsb().getHue().intValue());
        Assertions.assertEquals(6, item.getStateAsHsb().getSaturation().intValue());
        Assertions.assertEquals(7, item.getStateAsHsb().getBrightness().intValue());

        // verify event calls
        verifyEventTypes(4, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalColorItem("Name", "Label", "Type", "Id", Map.of(), List.of());
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleHsbValue(1, 2, 3);
    }

    @Override
    protected GenericItem getOhItem() {
        return new ColorItem("Name");
    }
}
