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

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleSwitchItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleSwitchItem extends JRuleItem {
    String ON = "ON";
    String OFF = "OFF";

    static JRuleSwitchItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalSwitchItem.class);
    }

    static Optional<JRuleSwitchItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends an on/off command
     * 
     * @param command command to send.
     */
    default void sendCommand(@NonNull JRuleOnOffValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends an on/off command only if current state is different
     *
     * @param command command to send.
     */
    default void sendCommandIfDifferent(@NonNull JRuleOnOffValue command) {
        if (!command.equals(getState())) {
            sendUncheckedCommand(command);
        }
    }

    /**
     * Sends an on/off update
     * 
     * @param state update to send
     */
    default void postUpdate(@NonNull JRuleOnOffValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends an on/off update only if current state is different
     *
     * @param state update to send
     */
    default void postUpdateIfDifferent(@NonNull JRuleOnOffValue state) {
        if (!state.equals(getState())) {
            postUncheckedUpdate(state);
        }
    }

    /**
     * Sends an on/off command
     * 
     * @param command command to send.
     */
    default void sendCommand(boolean command) {
        sendUncheckedCommand(JRuleOnOffValue.valueOf(command));
    }

    /**
     * Sends an on/off command
     *
     * @param command command to send.
     */
    default void sendCommandIfDifferent(boolean command) {
        JRuleValue newValue = JRuleOnOffValue.valueOf(command);
        sendCommandIfDifferent((JRuleOnOffValue) newValue);
    }

    /**
     * Sends an on/off update
     * 
     * @param state update to send
     */
    default void postUpdate(boolean state) {
        postUncheckedUpdate(JRuleOnOffValue.valueOf(state));
    }

    /**
     * Sends an on/off update
     *
     * @param state update to send
     */
    default void postUpdateIfDifferent(boolean state) {
        JRuleValue newValue = JRuleOnOffValue.valueOf(state);
        postUpdateIfDifferent((JRuleOnOffValue) newValue);
    }

    default JRuleOnOffValue getStateAsOnOff() {
        return JRuleEventHandler.get().getValue(getName(), JRuleOnOffValue.class);
    }
}
