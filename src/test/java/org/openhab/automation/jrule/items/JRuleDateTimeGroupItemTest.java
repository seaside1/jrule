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

import org.openhab.automation.jrule.internal.items.JRuleInternalDateTimeGroupItem;

/**
 * The {@link JRuleDateTimeGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleDateTimeGroupItemTest extends JRuleDateTimeItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalDateTimeGroupItem("Group", "Label", "Type", "Id");
    }
}
