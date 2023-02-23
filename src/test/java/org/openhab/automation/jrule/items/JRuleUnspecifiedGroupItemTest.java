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

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.internal.items.JRuleInternalSwitchGroupItem;
import org.openhab.automation.jrule.internal.items.JRuleInternalUnspecifiedGroupItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.SwitchItem;

/**
 * The {@link JRuleUnspecifiedGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleUnspecifiedGroupItemTest extends JRuleItemTestBase {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalSwitchGroupItem(GROUP_NAME, "Label", "Type", "Id",
                Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom"))),
                List.of("Lighting", "Inside"));
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return JRuleOnOffValue.ON;
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new SwitchItem(name);
    }

    @Override
    protected <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRuleSwitchGroupItem.forNameOptional(name);
    }

    @Override
    protected <T extends JRuleGroupItem<? extends JRuleItem>> T groupForNameMethod(String name) {
        return (T) JRuleSwitchGroupItem.forName(name);
    }

    @Test
    public void testMemberOfGeneric() {
        List<String> set = JRuleUnspecifiedGroupItem.forName(GROUP_NAME).memberItems().stream().map(JRuleItem::getType)
                .collect(Collectors.toList());
        Assertions.assertEquals(3, set.size());
    }

    @Test
    public void testGroupItemsOfGroup() {
        JRuleInternalUnspecifiedGroupItem item = new JRuleInternalUnspecifiedGroupItem(GROUP_NAME_2, "Label", "Type",
                "Id", new HashMap<>(), new ArrayList<>());
        Assertions.assertEquals(1, item.getGroupItems().size());
    }

    @Test
    public void testGroupItemsOfGroupRecursive() {
        JRuleInternalUnspecifiedGroupItem item = new JRuleInternalUnspecifiedGroupItem(SUB_ITEM_NAME, "Label", "Type",
                "Id", new HashMap<>(), new ArrayList<>());

        List<JRuleGroupItem<? extends JRuleItem>> groupItems = new ArrayList<>(item.getGroupItems(true));
        Assertions.assertEquals(2, groupItems.size());
        Assertions.assertEquals(GROUP_NAME_2, groupItems.get(0).getName());
        Assertions.assertEquals(GROUP_NAME, groupItems.get(1).getName());
    }
}
