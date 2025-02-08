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
import org.openhab.automation.jrule.internal.items.JRuleInternalNumberItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;

/**
 * The {@link JRuleNumberItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleNumberItem extends JRuleItem {
    static JRuleNumberItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalNumberItem.class);
    }

    static Optional<JRuleNumberItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends a decimal command
     * 
     * @param command command to send.
     */
    default void sendCommand(JRuleDecimalValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a decimal command only if current state is different
     *
     * @param command command to send.
     */
    default void sendCommandIfDifferent(JRuleDecimalValue command) {
        if(!command.equals(getState())) {
            sendUncheckedCommand(command);
        }
    }

    /**
     * Sends a decimal update
     * 
     * @param state update to send
     */
    default void postUpdate(JRuleDecimalValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a decimal update
     *
     * @param state update to send
     */
    default void postUpdateIfDifferent(JRuleDecimalValue state) {
        if (!state.equals(getState())) {
            postUncheckedUpdate(state);
        }
    }

    /**
     * Sends a number command.
     *
     * @param command as number via JRuleDecimalValue will be send.
     */
    default void sendCommand(double command) {
        sendUncheckedCommand(new JRuleDecimalValue(command));
    }

    /**
     * Sends a number command only if current state is different
     *
     * @param command as number via JRuleDecimalValue will be send.
     */
    default void sendCommandIfDifferent(double command) {
        sendCommandIfDifferent(new JRuleDecimalValue(command));
    }

    /**
     * Sends a number command.
     *
     * @param command as number via JRuleDecimalValue will be send.
     */
    default void sendCommand(int command) {
        sendUncheckedCommand(new JRuleDecimalValue(command));
    }

    /**
     * Sends a number command only if current state is different
     *
     * @param command as number via JRuleDecimalValue will be send.
     */
    default void sendCommandIfDifferent(int command) {
        sendCommandIfDifferent(new JRuleDecimalValue(command));
    }

    /**
     * Sends a number update.
     *
     * @param value as number via JRuleDecimalValue will be send.
     */
    default void postUpdate(double value) {
        postUncheckedUpdate(new JRuleDecimalValue(value));
    }

    /**
     * Sends a number update only if current state is different
     *
     * @param value as number via JRuleDecimalValue will be send.
     */
    default void postUpdateIfDifferent(double value) {
        postUpdateIfDifferent(new JRuleDecimalValue(value));
    }

    /**
     * Sends a number update.
     *
     * @param state as number via JRuleDecimalValue will be send.
     */
    default void postUpdate(int state) {
        postUncheckedUpdate(new JRuleDecimalValue(state));
    }

    /**
     * Sends a number update only if current state is different
     *
     * @param value as number via JRuleDecimalValue will be send.
     */
    default void postUpdateIfDifferent(int value) {
        postUpdateIfDifferent(new JRuleDecimalValue(value));
    }

    default JRuleDecimalValue getStateAsDecimal() {
        return JRuleEventHandler.get().getValue(getName(), JRuleDecimalValue.class);
    }
}
