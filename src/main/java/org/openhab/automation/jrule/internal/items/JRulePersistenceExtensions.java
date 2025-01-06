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
package org.openhab.automation.jrule.internal.items;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.persistence.*;
import org.openhab.core.persistence.extensions.PersistenceExtensions;
import org.openhab.core.types.State;

/**
 * The {@link JRulePersistenceExtensions}
 *
 * @author Arne Seime - Initial contribution
 */
class JRulePersistenceExtensions {
    public static void persist(String itemName, ZonedDateTime timestamp, JRuleValue state, String serviceId) {
        Item item = getItem(itemName);
        JRuleEngine jRuleEngine = JRuleEngine.get();
        PersistenceServiceRegistry persistenceServiceRegistry = jRuleEngine.getPersistenceServiceRegistry();
        PersistenceService persistenceService = persistenceServiceRegistry
                .get(Optional.ofNullable(serviceId).orElse(persistenceServiceRegistry.getDefaultId()));
        if (persistenceService instanceof ModifiablePersistenceService modifiablePersistenceService) {
            modifiablePersistenceService.store(item, timestamp, state.toOhState());
        } else {
            throw new JRuleRuntimeException("cannot persist item state, persistence service (%s) not modifiable"
                    .formatted(persistenceService.getId()));
        }
    }

    public static Optional<JRuleValue> stateAt(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        JRuleEngine jRuleEngine = JRuleEngine.get();
        PersistenceServiceRegistry persistenceServiceRegistry = jRuleEngine.getPersistenceServiceRegistry();
        PersistenceService persistenceService = persistenceServiceRegistry
                .get(Optional.ofNullable(serviceId).orElse(persistenceServiceRegistry.getDefaultId()));
        if (persistenceService instanceof QueryablePersistenceService queryablePersistenceService) {
            FilterCriteria filter = new FilterCriteria();
            filter.setBeginDate(timestamp.minusSeconds(1));
            filter.setEndDate(timestamp.plusSeconds(1));
            filter.setItemName(item.getName());
            filter.setPageSize(1);
            filter.setOrdering(FilterCriteria.Ordering.DESCENDING);
            Iterable<HistoricItem> result = queryablePersistenceService.query(filter);
            if (result.iterator().hasNext()) {
                return Optional.of(result.iterator().next()).map(HistoricItem::getState)
                        .map(state -> JRuleEventHandler.get().toValue(state));
            } else {
                return Optional.empty();
            }
        } else {
            throw new JRuleRuntimeException("cannot get state at for item, persistence service (%s) not queryable"
                    .formatted(persistenceService.getId()));
        }
    }

    public static Optional<JRuleValue> historicState(String itemName, ZonedDateTime timestamp) {
        return historicState(itemName, timestamp, null);
    }

    public static Optional<JRuleValue> historicState(String itemName, ZonedDateTime timestamp, String serviceId) {
        return historicStateInternal(itemName, timestamp, serviceId)
                .map(state -> JRuleEventHandler.get().toValue(state));
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

    public static Optional<State> varianceSince(String itemName, ZonedDateTime timestamp)
            throws JRuleItemNotFoundException {
        return varianceSince(itemName, timestamp, null);
    }

    public static Optional<State> varianceSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.varianceSince(item, timestamp)
                : PersistenceExtensions.varianceSince(item, timestamp, serviceId));
    }

    public static Optional<State> deviationSince(String itemName, ZonedDateTime timestamp) {
        return deviationSince(itemName, timestamp, null);
    }

    public static Optional<State> deviationSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.deviationSince(item, timestamp)
                : PersistenceExtensions.deviationSince(item, timestamp, serviceId));
    }

    public static Optional<State> averageSince(String itemName, ZonedDateTime timestamp) {
        return averageSince(itemName, timestamp, null);
    }

    public static Optional<State> averageSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.averageSince(item, timestamp)
                : PersistenceExtensions.averageSince(item, timestamp, serviceId));
    }

    public static Optional<State> sumSince(String itemName, ZonedDateTime timestamp) {
        return sumSince(itemName, timestamp, null);
    }

    public static Optional<State> sumSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        State state;
        if (serviceId == null) {
            state = PersistenceExtensions.sumSince(item, timestamp);
        } else {
            state = PersistenceExtensions.sumSince(item, timestamp, serviceId);
        }
        return state != null ? Optional.of(state) : Optional.empty();
    }

    public static Optional<ZonedDateTime> lastUpdate(String itemName) {
        return lastUpdate(itemName, null);
    }

    public static Optional<ZonedDateTime> lastUpdate(String itemName, String serviceId) {
        Item item = getItem(itemName);
        ZonedDateTime state;
        if (serviceId == null) {
            state = PersistenceExtensions.lastUpdate(item);
        } else {
            state = PersistenceExtensions.lastUpdate(item, serviceId);
        }
        return state != null ? Optional.of(state) : Optional.empty();
    }

    public static Optional<State> previousState(String itemName, boolean skipEquals) {
        return previousState(itemName, skipEquals, null);
    }

    public static Optional<State> previousState(String itemName, boolean skipEquals, String serviceId) {
        Item item = getItem(itemName);
        return Optional.ofNullable(serviceId == null ? PersistenceExtensions.previousState(item, skipEquals)
                : PersistenceExtensions.previousState(item, skipEquals, serviceId)).map(HistoricItem::getState);
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
