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
    default void sendCommand(JRuleQuantityValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a quantity update.
     *
     * @param state state to send.
     */
    default void postUpdate(JRuleQuantityValue state) {
        postUncheckedUpdate(state);
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
     * Sends a number command with the given unit.
     *
     * @param state as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void postUpdate(double state, String unit) {
        postUncheckedUpdate(new JRuleQuantityValue(state, unit));
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
     * Sends a number command with the given unit.
     *
     * @param state as number via JRuleQuantityValue will be sent.
     * @param unit of value as a string
     */
    default void postUpdate(int state, String unit) {
        postUncheckedUpdate(new JRuleQuantityValue(state, unit));
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
     * Sends a number command without any unit.
     *
     * @param state as number via JRuleDecimalValue will be sent.
     */
    default void postUpdate(double state) {
        postUncheckedUpdate(new JRuleDecimalValue(state));
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
     * Sends a number command without any unit.
     *
     * @param state as number via JRuleDecimalValue will be sent.
     */
    default void postUpdate(int state) {
        postUncheckedUpdate(new JRuleDecimalValue(state));
    }

    default JRuleDecimalValue getStateAsDecimal() {
        return new JRuleDecimalValue(getStateAsQuantity().doubleValue());
    }

    default JRuleQuantityValue getStateAsQuantity() {
        return JRuleEventHandler.get().getValue(getName(), JRuleQuantityValue.class);
    }
}
