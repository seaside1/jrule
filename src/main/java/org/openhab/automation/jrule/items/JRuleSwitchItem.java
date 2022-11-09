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

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleSwitchItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleSwitchItem extends JRuleItem<JRuleOnOffValue> {
    String ON = "ON";
    String OFF = "OFF";

    static JRuleSwitchItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleSwitchItem.class);
    }

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRuleOnOffValue.class;
    }
}
