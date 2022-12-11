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
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleColorItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleColorItem extends JRuleItem<JRuleHsbValue> {
    static JRuleColorItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleColorItem.class);
    }

    /**
     * Sends an ON or OFF
     * 
     * @param command true will send an JRuleOnOffValue.ON, false an OFF
     */
    void sendCommand(boolean command);

    /**
     * Sends an ON or OFF
     * 
     * @param value true will send an JRuleOnOffValue.ON, false an OFF.
     */
    void postUpdate(boolean value);

    /**
     * Sends an update in percent.
     * 
     * @param value update in percent via JRulePercentValue will be send.
     */
    void postUpdate(int value);

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRuleHsbValue.class;
    }
}
