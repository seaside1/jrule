/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalDimmerItem;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleDimmerItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDimmerItem extends JRuleSwitchItem {
    String INCREASE = "INCREASE";
    String DECREASE = "DECREASE";

    static JRuleDimmerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalDimmerItem.class);
    }

    static Optional<JRuleDimmerItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
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
     * Sends a percent command only if current state is different
     *
     * @param command command to send.
     */
    default void sendCommandIfDifferent(JRulePercentValue command) {
        if (!command.equals(getState())) {
            sendUncheckedCommand(command);
        }
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
     * Sends a percent update only if current state is different
     * 
     * @param state update to send
     */
    default void postUpdateIfDifferent(JRulePercentValue state) {
        if (!state.equals(getState())) {
            postUncheckedUpdate(state);
        }
    }

    /**
     * Sends a command in percent.
     * 
     * @param command in percent via JRulePercentValue will be send.
     */
    default void sendCommand(int command) {
        sendUncheckedCommand(new JRulePercentValue(command));
    }

    default void sendCommandIfDifferent(int command) {
        sendCommandIfDifferent(new JRulePercentValue(command));
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

    default void postUpdateIfDifferent(int state) {
        postUpdateIfDifferent(new JRulePercentValue(state));
    }

    default JRulePercentValue getStateAsPercent() {
        return JRuleEventHandler.get().getValue(getName(), JRulePercentValue.class);
    }
}
