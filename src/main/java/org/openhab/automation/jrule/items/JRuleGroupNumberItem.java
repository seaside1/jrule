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
import java.util.Set;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.group.JRuleInternalGroupItem;
import org.openhab.automation.jrule.internal.items.JRulePersistenceExtensions;
import org.openhab.core.library.types.DecimalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleGroupColorItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleGroupNumberItem extends JRuleInternalGroupItem {

    private static final String LOG_NAME = "JRuleGroupNumberItem";
    private static final Logger logger = LoggerFactory.getLogger(JRuleGroupNumberItem.class);

    protected JRuleGroupNumberItem(String itemName) {
        super(itemName);
    }

    public static JRuleGroupNumberItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleGroupNumberItem.class);
    }

    public Double getState() {
        return JRuleEventHandler.get().getStateFromItemAsDouble(name);
    }

    public void sendCommand(double value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(double value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    public void sendCommand(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    public void postUpdate(int value) {
        final Set<String> groupMemberNames = JRuleEventHandler.get().getGroupMemberNames(name);
        groupMemberNames.forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    // Persistence method
    public Double getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId)
                .map(Double::parseDouble).orElse(null);
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
                .map(DecimalType::doubleValue);
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
