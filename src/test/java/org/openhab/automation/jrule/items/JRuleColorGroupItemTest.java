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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.internal.items.JRuleInternalColorGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;

/**
 * The {@link JRuleColorGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleColorGroupItemTest extends JRuleColorItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalColorGroupItem(GROUP_NAME, "Label", "Type", "Id", mock, List.of("Lighting", "Inside"));
    }

    @Test
    public void testMemberOfGeneric() {
        List<JRuleHsbValue> set = JRuleColorGroupItem.forName(GROUP_NAME).memberItems().stream()
                .map(JRuleColorItem::getStateAsHsb).toList();
        Assertions.assertEquals(3, set.size());
    }
}
