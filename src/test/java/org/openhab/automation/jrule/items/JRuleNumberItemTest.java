/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalNumberItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.NumberItem;

/**
 * The {@link JRuleNumberItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleNumberItemTest extends JRuleItemTestBase {
    @Test
    public void testSendCommand(TestInfo testInfo) {
        JRuleNumberItem item = (JRuleNumberItem) getJRuleItem();
        item.sendCommand(17);

        // decimal
        Assertions.assertEquals(17, item.getStateAsDecimal().intValue());

        // send jrule-decimal
        item.sendCommand(new JRuleDecimalValue(22));
        Assertions.assertEquals(22, item.getStateAsDecimal().intValue());

        // verify event calls
        verifyEventTypes(testInfo, 0, 2);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleNumberItem item = (JRuleNumberItem) getJRuleItem();
        item.postUpdate(17);

        // decimal
        Assertions.assertEquals(17, item.getStateAsDecimal().intValue());

        // send jrule-decimal
        item.postUpdate(new JRuleDecimalValue(22));
        Assertions.assertEquals(22, item.getStateAsDecimal().intValue());

        // verify event calls
        verifyEventTypes(testInfo, 2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalNumberItem(ITEM_NAME, "Label", "Type", "Id",
                Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom"))),
                List.of("Lighting", "Inside"));
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleDecimalValue(75);
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new NumberItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleNumberItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleNumberItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleNumberItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleNumberItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> T groupForNameMethod(String name) {
        return (T) JRuleNumberGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleNumberGroupItem.forNameOptional(name);
    }
}
