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

import org.openhab.automation.jrule.internal.items.JRuleInternalImageGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.ImageItem;

/**
 * The {@link JRuleImageGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleImageGroupItemTest extends JRuleImageItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalImageGroupItem("Group", "Label", "Type", "Id");
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
