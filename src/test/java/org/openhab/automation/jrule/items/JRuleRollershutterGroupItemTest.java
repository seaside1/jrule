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
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.internal.items.JRuleInternalRollershutterGroupItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleRollershutterGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleRollershutterGroupItemTest extends JRuleRollershutterItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalRollershutterGroupItem("Group", "Label", "Type", "Id",
                Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom"))),
                List.of("Lighting", "Inside"));
    }

    @Test
    public void testMemberOfGeneric() {
        List<JRulePercentValue> set = JRuleRollershutterGroupItem.forName(GROUP_NAME).memberItems().stream()
                .map(JRuleRollershutterItem::getStateAsPercent).collect(Collectors.toList());
        Assertions.assertEquals(3, set.size());
    }
}
