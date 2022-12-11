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

    /**
     * Sends a command in percent.
     * 
     * @param command in percent via JRulePercentValue will be send.
     */
    void sendCommand(int command);

    /**
     * Sends 0 or 100
     * 
     * @param command true=100 or false=0 via JRulePercentValue will be send.
     */
    void sendCommand(boolean command);

    /**
     * Sends 0 or 100
     * 
     * @param value update true=100 or false=0 via JRulePercentValue will be send.
     */
    void postUpdate(boolean value);

    /**
     * Sends an update in percent.
     * 
     * @param value update in percent via JRulePercentValue will be send.
     */
    void postUpdate(int value);
}
