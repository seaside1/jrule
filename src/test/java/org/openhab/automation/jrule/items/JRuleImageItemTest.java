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
import org.openhab.automation.jrule.internal.items.JRuleInternalImageItem;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ImageItem;

import java.util.List;
import java.util.Map;

/**
 * The {@link JRuleImageItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleImageItemTest extends JRuleItemTestBase {
    @Test
    public void testPostUpdate() {
        JRuleImageItem item = (JRuleImageItem) getJRuleItem();
        item.postUpdate(new JRuleRawValue("jpeg", new byte[16]));

        // verify
        Assertions.assertArrayEquals(new byte[16], item.getStateAsRaw().getData());
        Assertions.assertEquals("jpeg", item.getStateAsRaw().getMimeType());

        // verify event calls
        verifyEventTypes(1, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalImageItem("Name", "Label", "Type", "Id", Map.of(), List.of());
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRuleRawValue("jpeg", new byte[16]);
    }

    @Override
    protected GenericItem getOhItem() {
        return new ImageItem("Name");
    }
}
