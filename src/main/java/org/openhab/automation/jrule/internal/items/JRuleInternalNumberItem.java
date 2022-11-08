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
package org.openhab.automation.jrule.internal.items;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.core.library.types.DecimalType;

/**
 * The {@link JRuleInternalNumberItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleInternalNumberItem extends JRuleInternalItem<JRuleDecimalValue> implements JRuleNumberItem {

    protected JRuleInternalNumberItem(String itemName) {
        super(itemName);
    }

    public static JRuleInternalNumberItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalNumberItem.class);
    }

    public void sendCommand(double value) {
        JRuleEventHandler.get().sendCommand(name, value);
    }

    public void postUpdate(double value) {
        JRuleEventHandler.get().postUpdate(name, value);
    }

    public void sendCommand(double value, String unit) {
        JRuleEventHandler.get().sendCommand(name, value, unit);
    }

    public void postUpdate(double value, String unit) {
        JRuleEventHandler.get().postUpdate(name, value, unit);
    }

    public Double getState() {
        return JRuleEventHandler.get().getStateFromItemAsDouble(name);
    }

    public Optional<Double> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(Double::parseDouble);
    }

    public Optional<Double> maximumSince(ZonedDateTime timestamp) {
        return maximumSince(timestamp, null);
    }

    public Optional<Double> maximumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.maximumSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> minimumSince(ZonedDateTime timestamp) {
        return minimumSince(timestamp, null);
    }

    public Optional<Double> minimumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.minimumSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> varianceSince(ZonedDateTime timestamp) {
        return varianceSince(timestamp, null);
    }

    public Optional<Double> varianceSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.varianceSince(name, timestamp, persistenceServiceId)
                .map(decimalType -> decimalType.doubleValue());
    }

    public Optional<Double> deviationSince(ZonedDateTime timestamp) {
        return deviationSince(timestamp, null);
    }

    public Optional<Double> deviationSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.deviationSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> averageSince(ZonedDateTime timestamp) {
        return averageSince(timestamp, null);
    }

    public Optional<Double> averageSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.averageSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> sumSince(ZonedDateTime timestamp) {
        return sumSince(timestamp, null);
    }

    public Optional<Double> sumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.sumSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }
}
