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
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.internal.items.JRuleInternalCallGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleStringListValue;

/**
 * The {@link JRuleCallGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleCallGroupItemTest extends JRuleCallItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalCallGroupItem("Group", "Label", "Type", "Id");
    }

    @Test
    public void testMemberOfGeneric() {
        List<JRuleStringListValue> set = JRuleCallGroupItem.forName(GROUP_NAME).memberItems().stream()
                .map(JRuleCallItem::getStateAsStringList).collect(Collectors.toList());
        Assertions.assertEquals(3, set.size());
    }
}
