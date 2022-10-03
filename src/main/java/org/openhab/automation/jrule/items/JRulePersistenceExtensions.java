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
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.persistence.HistoricItem;
import org.openhab.core.persistence.extensions.PersistenceExtensions;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRulePersistenceExtensions}
 *
 * @author Arne Seime - Initial contribution
 */
class JRulePersistenceExtensions {

    private final static Logger logger = LoggerFactory.getLogger(JRulePersistenceExtensions.class);
    private static final String LOG_NAME_PERSISTENCE = "JRulePersistence";

    public static Optional<String> historicState(String itemName, ZonedDateTime timestamp) {
        return historicState(itemName, timestamp, null);
    }

    public static Optional<String> historicState(String itemName, ZonedDateTime timestamp, String serviceId) {
        return historicStateInternal(itemName, timestamp, serviceId).map(Object::toString);
    }

    public static Optional<JRuleRawValue> historicStateAsRawValue(String itemName, ZonedDateTime timestamp) {
        return historicStateAsRawValue(itemName, timestamp, null);
    }

    public static Optional<JRuleRawValue> historicStateAsRawValue(String itemName, ZonedDateTime timestamp,
            String serviceId) {
        return historicStateInternal(itemName, timestamp, serviceId)
                .map(state -> new JRuleRawValue(((RawType) state).getMimeType(), ((RawType) state).getBytes()));
    }

    private static Optional<State> historicStateInternal(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.historicState(item, timestamp)
                : PersistenceExtensions.historicState(item, timestamp, serviceId)).map(HistoricItem::getState);
    }

    public static boolean changedSince(String itemName, ZonedDateTime timestamp) {
        return changedSince(itemName, timestamp, null);
    }

    public static boolean changedSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return serviceId == null ? PersistenceExtensions.changedSince(item, timestamp)
                : PersistenceExtensions.changedSince(item, timestamp, serviceId);
    }

    public static boolean updatedSince(String itemName, ZonedDateTime timestamp) {
        return updatedSince(itemName, timestamp, null);
    }

    public static boolean updatedSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return serviceId == null ? PersistenceExtensions.updatedSince(item, timestamp)
                : PersistenceExtensions.updatedSince(item, timestamp, serviceId);
    }

    public static Optional<DecimalType> maximumSince(String itemName, ZonedDateTime timestamp) {
        return maximumSince(itemName, timestamp, null);
    }

    public static Optional<DecimalType> maximumSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional
                .ofNullable(serviceId == null ? PersistenceExtensions.maximumSince(item, timestamp)
                        : PersistenceExtensions.maximumSince(item, timestamp, serviceId))
                .map(historicItem -> historicItem.getState().as(DecimalType.class));
    }

    public static Optional<DecimalType> minimumSince(String itemName, ZonedDateTime timestamp) {
        return minimumSince(itemName, timestamp, null);
    }

    public static Optional<DecimalType> minimumSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional
                .ofNullable(serviceId == null ? PersistenceExtensions.minimumSince(item, timestamp)
                        : PersistenceExtensions.minimumSince(item, timestamp, serviceId))
                .map(historicItem -> historicItem.getState().as(DecimalType.class));
    }

    public static Optional<DecimalType> varianceSince(String itemName, ZonedDateTime timestamp)
            throws JRuleItemNotFoundException {
        return varianceSince(itemName, timestamp, null);
    }

    public static Optional<DecimalType> varianceSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.varianceSince(item, timestamp)
                : PersistenceExtensions.varianceSince(item, timestamp, serviceId));
    }

    public static Optional<DecimalType> deviationSince(String itemName, ZonedDateTime timestamp) {
        return deviationSince(itemName, timestamp, null);
    }

    public static Optional<DecimalType> deviationSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.deviationSince(item, timestamp)
                : PersistenceExtensions.deviationSince(item, timestamp, serviceId));
    }

    public static Optional<DecimalType> averageSince(String itemName, ZonedDateTime timestamp) {
        return averageSince(itemName, timestamp, null);
    }

    public static Optional<DecimalType> averageSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.averageSince(item, timestamp)
                : PersistenceExtensions.averageSince(item, timestamp, serviceId));
    }

    public static Optional<DecimalType> sumSince(String itemName, ZonedDateTime timestamp) {
        return sumSince(itemName, timestamp, null);
    }

    public static Optional<DecimalType> sumSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.of(serviceId == null ? PersistenceExtensions.sumSince(item, timestamp)
                : PersistenceExtensions.sumSince(item, timestamp, serviceId));
    }

    public static Optional<ZonedDateTime> lastUpdate(String itemName) {
        return lastUpdate(itemName, null);
    }

    public static Optional<ZonedDateTime> lastUpdate(String itemName, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.lastUpdate(item)
                : PersistenceExtensions.lastUpdate(item, serviceId));
    }

    private static Item getItem(String itemName) {
        try {
            ItemRegistry itemRegistry = JRuleEventHandler.get().getItemRegistry();
            if (itemRegistry == null) {
                throw new IllegalStateException(
                        String.format("Item registry is not set can't get item for name: %s", itemName));
            }

            return itemRegistry.getItem(itemName);
        } catch (ItemNotFoundException e) {
            throw new IllegalStateException(String.format("Failed to get item: %s", itemName), e);
        }
    }
}
