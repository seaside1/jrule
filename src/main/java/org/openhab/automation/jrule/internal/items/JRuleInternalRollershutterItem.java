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

import java.util.List;
import java.util.Map;

import org.openhab.automation.jrule.items.JRuleRollershutterItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;

/**
 * The {@link JRuleInternalRollershutterItem} Items
 *
 * @author Timo Litzius - Initial contribution
 */
public class JRuleInternalRollershutterItem extends JRuleInternalItem implements JRuleRollershutterItem {

    public JRuleInternalRollershutterItem(String name, String label, String type, String id,
            Map<String, JRuleItemMetadata> metadata, List<String> tags) {
        super(name, label, type, id, metadata, tags);
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
                .map(DecimalType::doubleValue);
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
