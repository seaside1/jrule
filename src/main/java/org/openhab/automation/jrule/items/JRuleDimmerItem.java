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
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleDimmerItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDimmerItem extends JRuleItem<JRulePercentValue> {
    String INCREASE = JRuleIncreaseDecreaseValue.INCREASE.asStringValue();
    String DECREASE = JRuleIncreaseDecreaseValue.DECREASE.asStringValue();

    static JRuleDimmerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleDimmerItem.class);
    }

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRulePercentValue.class;
    }

    void sendCommand(int command);

    void sendCommand(boolean command);

    void postUpdate(boolean command);

    void postUpdate(int value);
}
