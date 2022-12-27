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
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleDimmerItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDimmerItem extends JRuleSwitchItem {
    String INCREASE = JRuleIncreaseDecreaseValue.INCREASE.stringValue();
    String DECREASE = JRuleIncreaseDecreaseValue.DECREASE.stringValue();

    static JRuleDimmerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleDimmerItem.class);
    }

    /**
     * Sends a percent command
     * 
     * @param command command to send.
     */
    default void sendCommand(JRulePercentValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a percent update
     * 
     * @param state update to send
     */
    default void postUpdate(JRulePercentValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a command in percent.
     * 
     * @param command in percent via JRulePercentValue will be send.
     */
    default void sendCommand(int command) {
        sendUncheckedCommand(new JRulePercentValue(command));
    }

    /**
     * Sends a increase/decrease command.
     *
     * @param command command to send.
     */
    default void sendCommand(JRuleIncreaseDecreaseValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends an increase/decrease.
     *
     * @param state update to send.
     */
    default void postUpdate(JRuleIncreaseDecreaseValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends an update in percent.
     * 
     * @param state update in percent via JRulePercentValue will be send.
     */
    default void postUpdate(int state) {
        postUncheckedUpdate(new JRulePercentValue(state));
    }

    default JRulePercentValue getStateAsPercent() {
        return JRuleEventHandler.get().getValue(getName(), JRulePercentValue.class);
    }
}
