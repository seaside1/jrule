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
import java.util.Optional;

import javax.measure.Unit;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleQuantityValue;

/**
 * The {@link JRuleNumberItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleNumberItem extends JRuleItem {
    static JRuleNumberItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleNumberItem.class);
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
     * Sends a decimal update
     * 
     * @param state update to send
     */
    default void postUpdate(JRuleDecimalValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a quantity command.
     *
     * @param command command to send
     */
    default void sendCommand(JRuleQuantityValue<?> command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a quantity update.
     *
     * @param state state to send.
     */
    default void postUpdate(JRuleQuantityValue<?> state) {
        postUncheckedUpdate(state);
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
     * Sends a number command.
     *
     * @param command as number via JRuleDecimalValue will be send.
     */
    default void sendCommand(int command) {
        sendUncheckedCommand(new JRuleDecimalValue(command));
    }

    /**
     * Sends a number command with the given unit.
     *
     * @param command as number via JRuleDecimalValue will be send.
     * @param unit unit as string
     */
    default void sendCommand(double command, Unit<?> unit) {
        sendUncheckedCommand(new JRuleQuantityValue<>(command, unit));
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
     * Sends a number update.
     *
     * @param state as number via JRuleDecimalValue will be send.
     */
    default void postUpdate(int state) {
        postUncheckedUpdate(new JRuleDecimalValue(state));
    }

    /**
     * Sends a number command with the given unit.
     *
     * @param state as number via JRuleDecimalValue will be send.
     * @param unit unit as string
     */
    default void postUpdate(double state, Unit<?> unit) {
        postUncheckedUpdate(new JRuleQuantityValue<>(state, unit));
    }

    default Optional<Double> maximumSince(ZonedDateTime timestamp) {
        return maximumSince(timestamp, null);
    }

    Optional<Double> maximumSince(ZonedDateTime timestamp, String persistenceServiceId);

    default Optional<Double> minimumSince(ZonedDateTime timestamp) {
        return minimumSince(timestamp, null);
    }

    Optional<Double> minimumSince(ZonedDateTime timestamp, String persistenceServiceId);

    default Optional<Double> varianceSince(ZonedDateTime timestamp) {
        return varianceSince(timestamp, null);
    }

    Optional<Double> varianceSince(ZonedDateTime timestamp, String persistenceServiceId);

    default Optional<Double> deviationSince(ZonedDateTime timestamp) {
        return deviationSince(timestamp, null);
    }

    Optional<Double> deviationSince(ZonedDateTime timestamp, String persistenceServiceId);

    default Optional<Double> averageSince(ZonedDateTime timestamp) {
        return averageSince(timestamp, null);
    }

    Optional<Double> averageSince(ZonedDateTime timestamp, String persistenceServiceId);

    default Optional<Double> sumSince(ZonedDateTime timestamp) {
        return sumSince(timestamp, null);
    }

    Optional<Double> sumSince(ZonedDateTime timestamp, String persistenceServiceId);

    default JRuleDecimalValue getStateAsDecimal() {
        return JRuleEventHandler.get().getValue(getName(), JRuleDecimalValue.class);
    }

    default JRuleQuantityValue getStateAsQuantity() {
        return JRuleEventHandler.get().getValue(getName(), JRuleQuantityValue.class);
    }
}
