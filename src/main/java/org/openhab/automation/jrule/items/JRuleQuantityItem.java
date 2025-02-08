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
import org.openhab.automation.jrule.internal.items.JRuleInternalQuantityItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleQuantityValue;

/**
 * The {@link JRuleQuantityItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleQuantityItem extends JRuleNumberItem {
    static JRuleQuantityItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalQuantityItem.class);
    }

    static Optional<JRuleQuantityItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends a quantity command.
     *
     * @param command command to send
     */
    default void sendCommand(@NonNull JRuleQuantityValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a quantity command only if current state is different
     *
     * @param command command to send
     */
    default void sendCommandIfDifferent(@NonNull JRuleQuantityValue command) {
        if(!command.equals(getState())) {
            sendUncheckedCommand(command);
        }
    }

    /**
     * Sends a quantity update.
     *
     * @param state state to send.
     */
    default void postUpdate(@NonNull JRuleQuantityValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a quantity update only if current state is different
     *
     * @param state state to send.
     */
    default void postUpdateIfDifferent(@NonNull JRuleQuantityValue state) {
        if(!state.equals(getState())) {
            postUncheckedUpdate(state);
        }
    }

    /**
     * Sends a number command with the given unit.
     *
     * @param command as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void sendCommand(double command, String unit) {
        sendUncheckedCommand(new JRuleQuantityValue(command, unit));
    }

    /**
     * Sends a number command with the given unit only if current state is different
     *
     * @param command as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void sendCommandIfDifferent(double command, String unit) {
        sendCommandIfDifferent(new JRuleQuantityValue(command, unit));
    }

    /**
     * Sends a number command with the given unit.
     *
     * @param state as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void postUpdate(double state, String unit) {
        postUncheckedUpdate(new JRuleQuantityValue(state, unit));
    }

    /**
     * Sends a number uppdate with the given unit only if current state is different
     *
     * @param state as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void postUpdateIfDifferent(double state, String unit) {
        postUpdateIfDifferent(new JRuleQuantityValue(state, unit));
    }

    /**
     * Sends a number command with the given unit.
     *
     * @param command as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void sendCommand(int command, String unit) {
        sendUncheckedCommand(new JRuleQuantityValue(command, unit));
    }

    /**
     * Sends a number command with the given unit only if current state is different
     *
     * @param command as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void sendCommandIfDifferent(int command, String unit) {
        sendCommandIfDifferent(new JRuleQuantityValue(command, unit));
    }

    /**
     * Sends a number command with the given unit.
     *
     * @param state as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void postUpdate(int state, String unit) {
        postUncheckedUpdate(new JRuleQuantityValue(state, unit));
    }

    /**
     * Sends a number command with the given unit only if current state is different
     *
     * @param state as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void postUpdateIfDifferent(int state, String unit) {
        postUpdateIfDifferent(new JRuleQuantityValue(state, unit));
    }

    /**
     * Sends a number command without any unit.
     *
     * @param command as number via JRuleDecimalValue will be sent.
     */
    default void sendCommand(double command) {
        sendUncheckedCommand(new JRuleDecimalValue(command));
    }

    /**
     * Sends a number command without any unit only if current state is different
     *
     * @param command as number via JRuleDecimalValue will be sent.
     */
    default void sendCommandIfDifferent(double command) {
        if(getStateAsDecimal() != null && Math.abs(getStateAsDecimal().doubleValue() - command) < 0.0001) {
            sendUncheckedCommand(new JRuleDecimalValue(command));
        }
    }

    /**
     * Sends a number command without any unit.
     *
     * @param state as number via JRuleDecimalValue will be sent.
     */
    default void postUpdate(double state) {
        postUncheckedUpdate(new JRuleDecimalValue(state));
    }

    /**
     * Sends a number command without any unit only if current state is different
     *
     * @param state as number via JRuleDecimalValue will be sent.
     */
    default void postUpdateIfDifferent(double state) {
        if(getStateAsDecimal() != null && Math.abs(getStateAsDecimal().doubleValue() - state) < 0.0001) {
            postUncheckedUpdate(new JRuleDecimalValue(state));
        }
    }

    /**
     * Sends a number command without any unit.
     *
     * @param command as number via JRuleDecimalValue will be sent.
     */
    default void sendCommand(int command) {
        sendUncheckedCommand(new JRuleDecimalValue(command));
    }

    /**
     * Sends a number command without any unit only if current state is different
     *
     * @param command as number via JRuleDecimalValue will be sent.
     */
    default void sendCommandIfDifferent(int command) {
        if(getStateAsDecimal() != null && Math.abs(getStateAsDecimal().doubleValue() - command) < 0.0001) {
            sendUncheckedCommand(new JRuleDecimalValue(command));
        }
    }

    /**
     * Sends a number command without any unit.
     *
     * @param state as number via JRuleDecimalValue will be sent.
     */
    default void postUpdate(int state) {
        postUncheckedUpdate(new JRuleDecimalValue(state));
    }

    /**
     * Sends a number command without any unit.
     *
     * @param state as number via JRuleDecimalValue will be sent.
     */
    default void postUpdateIfDifferent(int state) {
        if(getStateAsDecimal() != null && Math.abs(getStateAsDecimal().doubleValue() - state) < 0.0001) {
            postUncheckedUpdate(new JRuleDecimalValue(state));
        }
    }

    default JRuleDecimalValue getStateAsDecimal() {
        return new JRuleDecimalValue(getStateAsQuantity().doubleValue());
    }

    default JRuleQuantityValue getStateAsQuantity() {
        return JRuleEventHandler.get().getValue(getName(), JRuleQuantityValue.class);
    }
}
