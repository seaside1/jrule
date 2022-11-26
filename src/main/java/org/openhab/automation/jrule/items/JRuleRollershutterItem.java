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
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleRollershutterItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleRollershutterItem extends JRuleItem<JRulePercentValue> {
    static JRuleRollershutterItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleRollershutterItem.class);
    }

    void sendCommand(int value);

    void sendCommand(boolean command);

    void postUpdate(boolean command);

    void postUpdate(int value);

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRulePercentValue.class;
    }
}
