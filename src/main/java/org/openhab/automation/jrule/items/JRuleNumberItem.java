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

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleNumberItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleNumberItem extends JRuleItem<JRuleDecimalValue> {
    static JRuleNumberItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleNumberItem.class);
    }

    /**
     * Sends a number command.
     * 
     * @param command as number via JRuleDecimalValue will be send.
     */
    void sendCommand(double command);

    /**
     * Sends a number command with the given unit.
     * 
     * @param command as number via JRuleDecimalValue will be send.
     * @param unit unit as string
     */
    void sendCommand(double command, String unit);

    /**
     * Sends a number command.
     * 
     * @param command as number via JRuleDecimalValue will be send.
     */
    void sendCommand(int command);

    /**
     * Sends a number command with the given unit.
     * 
     * @param value as number via JRuleDecimalValue will be send.
     * @param unit unit as string
     */
    void postUpdate(double value, String unit);

    /**
     * Sends a number update.
     * 
     * @param value as number via JRuleDecimalValue will be send.
     */
    void postUpdate(double value);

    /**
     * Sends a number update.
     * 
     * @param value as number via JRuleDecimalValue will be send.
     */
    void postUpdate(int value);

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRuleDecimalValue.class;
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
}
