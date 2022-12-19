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

/**
 * The {@link JRuleColorGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleColorGroupItem extends JRuleColorItem, JRuleDimmerGroupItem {
    static JRuleColorGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleColorGroupItem.class);
    }

    default void sendCommand(JRuleHsbValue command) {
        memberItems().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleHsbValue state) {
        memberItems().forEach(i -> i.postUncheckedUpdate(state));
    }
}
