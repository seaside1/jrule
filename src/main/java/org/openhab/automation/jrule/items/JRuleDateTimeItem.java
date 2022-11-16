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

import java.time.ZonedDateTime;
import java.util.Date;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleDateTimeItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDateTimeItem extends JRuleItem<JRuleDateTimeValue> {
    static JRuleDateTimeItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleDateTimeItem.class);
    }

    void sendCommand(Date date);

    void sendCommand(ZonedDateTime value);

    void postUpdate(Date date);

    void postUpdate(ZonedDateTime value);

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRuleDateTimeValue.class;
    }
}
