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

import org.openhab.automation.jrule.internal.items.JRuleInternalDimmerGroupItem;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.DimmerItem;

/**
 * The {@link JRulePercentGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRulePercentGroupItemTest extends JRulePercentItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalDimmerGroupItem("Group", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return new JRulePercentValue(75);
    }

    @Override
    protected GenericItem getOhItem() {
        return new DimmerItem("Name");
    }
}
