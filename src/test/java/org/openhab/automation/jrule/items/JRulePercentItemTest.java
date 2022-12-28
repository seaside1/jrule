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
import org.openhab.automation.jrule.internal.items.JRuleInternalDimmerItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.DimmerItem;

/**
 * The {@link JRulePercentItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRulePercentItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand() {
        JRuleDimmerItem item = (JRuleDimmerItem) getJRuleItem();
        item.sendCommand(17);

        // percent
        Assertions.assertEquals(17, item.getStateAsPercent().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // send off
        item.sendCommand(false);
        Assertions.assertEquals(0, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // send percent
        item.sendCommand(new JRulePercentValue(22));
        Assertions.assertEquals(22, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(0, 3);
    }

    @Test
    public void testPostUpdate() {
        JRuleDimmerItem item = (JRuleDimmerItem) getJRuleItem();
        item.postUpdate(17);

        // percent
        Assertions.assertEquals(17, item.getStateAsPercent().intValue());

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // send off
        item.postUpdate(false);
        Assertions.assertEquals(0, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // send percent
        item.postUpdate(new JRulePercentValue(22));
        Assertions.assertEquals(22, item.getStateAsPercent().intValue());
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(3, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalDimmerItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRulePercentValue(75);
    }

    @Override
    protected GenericItem getOhItem() {
        return new DimmerItem("Name");
    }
}
