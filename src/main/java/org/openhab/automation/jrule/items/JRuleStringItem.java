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
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleStringItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleStringItem extends JRuleItem<JRuleStringValue> {
    static JRuleStringItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleStringItem.class);
    }

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRuleStringValue.class;
    }

    void sendCommand(String state);

    void postUpdate(String state);
}
