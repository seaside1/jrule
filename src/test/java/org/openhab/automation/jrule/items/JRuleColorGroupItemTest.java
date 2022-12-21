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

import org.openhab.automation.jrule.internal.items.JRuleInternalColorGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ColorItem;

import java.util.List;
import java.util.Map;

/**
 * The {@link JRuleColorGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleColorGroupItemTest extends JRuleColorItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalColorGroupItem("Group", "Label", "Type", "Id", Map.of(), List.of());
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
