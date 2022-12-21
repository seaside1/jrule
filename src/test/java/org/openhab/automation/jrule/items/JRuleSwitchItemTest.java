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
import org.openhab.automation.jrule.internal.items.JRuleInternalSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.SwitchItem;

import java.util.Map;

/**
 * The {@link JRuleSwitchItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleSwitchItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand() {
        JRuleSwitchItem item = (JRuleSwitchItem) getJRuleItem();
        item.sendCommand(true);

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getState());

        // send off
        item.sendCommand(JRuleOnOffValue.OFF);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(0, 2);
    }

    @Test
    public void testPostUpdate() {
        JRuleSwitchItem item = (JRuleSwitchItem) getJRuleItem();
        item.postUpdate(true);

        // on/off
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getStateAs(JRuleOnOffValue.class));
        Assertions.assertEquals(JRuleOnOffValue.ON, item.getState());

        // send off
        item.postUpdate(JRuleOnOffValue.OFF);
        Assertions.assertEquals(JRuleOnOffValue.OFF, item.getStateAs(JRuleOnOffValue.class));

        // verify event calls
        verifyEventTypes(2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalSwitchItem("Name", "Label", "Type", "Id", Map.of());
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return JRuleOnOffValue.ON;
    }

    @Override
    protected GenericItem getOhItem() {
        return new SwitchItem("Name");
    }
}
