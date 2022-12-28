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
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * The {@link JRuleSwitchItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleSwitchItem extends JRuleItem {
    String ON = "ON";
    String OFF = "OFF";

    static JRuleSwitchItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleSwitchItem.class);
    }

    /**
     * Sends a on/off command
     * 
     * @param command command to send.
     */
    default void sendCommand(JRuleOnOffValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a on/off update
     * 
     * @param state update to send
     */
    default void postUpdate(JRuleOnOffValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a on/off command
     * 
     * @param command command to send.
     */
    default void sendCommand(boolean command) {
        sendUncheckedCommand(JRuleOnOffValue.valueOf(command));
    }

    /**
     * Sends a on/off update
     * 
     * @param state update to send
     */
    default void postUpdate(boolean state) {
        postUncheckedUpdate(JRuleOnOffValue.valueOf(state));
    }

    default JRuleOnOffValue getStateAsOnOff() {
        return JRuleEventHandler.get().getValue(getName(), JRuleOnOffValue.class);
    }
}
