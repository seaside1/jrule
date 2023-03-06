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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalImageItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ImageItem;

/**
 * The {@link JRuleImageItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleImageItemTest extends JRuleItemTestBase {
    @Test
    public void testPostUpdate(TestInfo testInfo) {
        JRuleImageItem item = (JRuleImageItem) getJRuleItem();
        item.postUpdate(new JRuleRawValue("jpeg", new byte[16]));

        // verify
        Assertions.assertArrayEquals(new byte[16], item.getStateAsRaw().getData());
        Assertions.assertEquals("jpeg", item.getStateAsRaw().getMimeType());

        // verify event calls
        verifyEventTypes(testInfo, 1, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalImageItem(ITEM_NAME, "Label", "Type", "Id",
                Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom"))),
                List.of("Lighting", "Inside"));
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleRawValue("jpeg", new byte[16]);
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new ImageItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRuleImageItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRuleImageItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRuleImageItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRuleImageItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> T groupForNameMethod(String name) {
        return (T) JRuleImageGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleImageGroupItem.forNameOptional(name);
    }
}
