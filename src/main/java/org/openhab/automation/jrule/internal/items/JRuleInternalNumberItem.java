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
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.core.library.types.DecimalType;

/**
 * The {@link JRuleInternalNumberItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalNumberItem extends JRuleInternalItem<JRuleDecimalValue> implements JRuleNumberItem {

    protected JRuleInternalNumberItem(String name, String label, String type, String id) {
        super(name, label, type, id);
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

    public Optional<Double> maximumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.maximumSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> minimumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.minimumSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> varianceSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.varianceSince(name, timestamp, persistenceServiceId)
                .map(decimalType -> decimalType.doubleValue());
    }

    public Optional<Double> deviationSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.deviationSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> averageSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.averageSince(name, timestamp, persistenceServiceId)
                .map(DecimalType::doubleValue);
    }

    public Optional<Double> sumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.sumSince(name, timestamp, persistenceServiceId).map(DecimalType::doubleValue);
    }
}
