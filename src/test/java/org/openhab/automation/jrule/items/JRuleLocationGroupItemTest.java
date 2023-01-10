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

import org.openhab.automation.jrule.internal.items.JRuleInternalLocationGroupItem;

import java.util.List;
import java.util.Map;

/**
 * The {@link JRuleLocationGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleLocationGroupItemTest extends JRuleLocationItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalLocationGroupItem("Group", "Label", "Type", "Id", Map.of(), List.of());
    }
}
