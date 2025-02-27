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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.items.metadata.JRuleMetadataRegistry;
import org.openhab.automation.jrule.persistence.JRuleHistoricState;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.persistence.HistoricItem;
import org.openhab.core.persistence.extensions.PersistenceExtensions;
import org.openhab.core.types.State;
import org.openhab.core.types.TimeSeries;

/**
 * The {@link JRuleInternalItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleInternalItem implements JRuleItem {
    protected final String name;
    protected final String label;
    protected final String type;
    protected final String id;
    protected final JRuleMetadataRegistry metadataRegistry;
    protected final List<String> tags;

    public JRuleInternalItem(String name, String label, String type, String id, JRuleMetadataRegistry metadataRegistry,
            List<String> tags) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.id = id;
        this.metadataRegistry = metadataRegistry;
        this.tags = tags;
    }

    @Override
    public String getStateAsString() {
        JRuleStringValue value = JRuleEventHandler.get().getValue(name, JRuleStringValue.class);
        if (value == null) {
            return null;
        }
        return value.stringValue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Map<String, JRuleItemMetadata> getMetadata() {
        return metadataRegistry.getAllMetadata(name);
    }

    @Override
    public void addMetadata(String namespace, JRuleItemMetadata metadata, boolean override) {
        this.metadataRegistry.addMetadata(namespace, name, metadata, override);
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleInternalItem that = (JRuleInternalItem) o;
        return name.equals(that.name) && Objects.equals(label, that.label) && type.equals(that.type)
                && id.equals(that.id);
    }

    protected Double getNumericValue(State state) {
        if (state instanceof DecimalType) {
            return ((DecimalType) state).doubleValue();
        } else if (state instanceof QuantityType<?>) {
            return ((QuantityType<?>) state).doubleValue();
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, label, type, id);
    }

    @Override
    public String toString() {
        return "%s:%s".formatted(name, type);
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

    private Optional<JRuleHistoricState> mapHistoricItem(HistoricItem historicItem) {
        return Optional.ofNullable(historicItem)
                .map(h -> new JRuleHistoricState(JRuleEventHandler.get().toValue(historicItem.getState()),
                        historicItem.getTimestamp()));
    }

    @Override
    public void persist(@Nullable String serviceId) {
        Item item = getItem(name);
        PersistenceExtensions.persist(item);
    }

    @Override
    public void persist(ZonedDateTime timestamp, JRuleValue state, @Nullable String serviceId) {
        Item item = getItem(name);
        PersistenceExtensions.persist(item, timestamp, state.toOhState(), serviceId);
    }

    @Override
    public void persist(ZonedDateTime timestamp, String stateString, @Nullable String serviceId) {
        Item item = getItem(name);
        PersistenceExtensions.persist(item, timestamp, stateString, serviceId);
    }

    @Override
    public void persist(TimeSeries timeSeries, @Nullable String serviceId) {
        Item item = getItem(name);
        PersistenceExtensions.persist(item, timeSeries, serviceId);
    }

    @Deprecated
    @Override
    public Optional<JRuleHistoricState> historicState(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.historicState(item, timestamp, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> persistedState(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.persistedState(item, timestamp, serviceId));
    }

    @Override
    public Optional<ZonedDateTime> lastUpdate(@Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.lastUpdate(item, serviceId));
    }

    @Override
    public Optional<ZonedDateTime> nextUpdate(@Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.nextUpdate(item, serviceId));
    }

    @Override
    public Optional<ZonedDateTime> lastChange(@Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.lastChange(item, serviceId));
    }

    @Override
    public Optional<ZonedDateTime> nextChange(@Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.nextChange(item, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> previousState(@Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.previousState(item, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> previousState(boolean skipEqual, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.previousState(item, skipEqual, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> nextState(@Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.nextState(item, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> nextState(boolean skipEqual, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.nextState(item, skipEqual, serviceId));
    }

    @Override
    public Optional<Boolean> changedBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.changedBetween(item, begin, end, serviceId));
    }

    @Override
    public Optional<Boolean> changedUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.changedUntil(item, timestamp, serviceId));
    }

    @Override
    public Optional<Boolean> changedSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.changedSince(item, timestamp, serviceId));
    }

    @Override
    public Optional<Boolean> updatedBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.updatedBetween(item, begin, end, serviceId));
    }

    @Override
    public Optional<Boolean> updatedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.updatedSince(item, timestamp, persistenceServiceId));
    }

    @Override
    public Optional<Boolean> updatedUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.updatedUntil(item, timestamp, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> maximumSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.maximumSince(item, timestamp, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> maximumUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.maximumUntil(item, timestamp, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> maximumBetween(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.maximumBetween(item, begin, end, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> minimumSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.minimumSince(item, timestamp, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> minimumUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.minimumUntil(item, timestamp, serviceId));
    }

    @Override
    public Optional<JRuleHistoricState> minimumBetween(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        Item item = getItem(name);
        return mapHistoricItem(PersistenceExtensions.minimumBetween(item, begin, end, serviceId));
    }

    @Override
    public Optional<JRuleValue> varianceBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.varianceBetween(item, begin, end, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> varianceSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.varianceSince(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> sumSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.sumSince(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> varianceUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.varianceUntil(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> deviationBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.deviationBetween(item, begin, end, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> deviationSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.deviationSince(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> deviationUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.deviationUntil(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> averageBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.averageBetween(item, begin, end, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> averageSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.averageSince(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> averageUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.averageUntil(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> medianBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        throw new IllegalStateException("available in oh 4.3");
        // Item item = getItem(name);
        // return Optional.ofNullable(PersistenceExtensions.medianBetween(item, begin, end, serviceId))
        // .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> medianSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        throw new IllegalStateException("available in oh 4.3");
        // Item item = getItem(name);
        // return Optional.ofNullable(PersistenceExtensions.medianSince(item, timestamp, serviceId))
        // .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> medianUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        throw new IllegalStateException("available in oh 4.3");
        // Item item = getItem(name);
        // return Optional.ofNullable(PersistenceExtensions.medianUntil(item, timestamp, serviceId))
        // .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> deltaSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.deltaSince(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> deltaUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.deltaUntil(item, timestamp, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Override
    public Optional<JRuleValue> deltaBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.deltaBetween(item, begin, end, serviceId))
                .map(v -> JRuleEventHandler.get().toValue(v));
    }

    @Deprecated
    @Override
    public Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.evolutionRate(item, timestamp, serviceId))
                .map(v -> new JRuleDecimalValue(v.doubleValue()));
    }

    @Override
    public Optional<JRuleDecimalValue> evolutionRateSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.evolutionRateSince(item, timestamp, serviceId))
                .map(v -> new JRuleDecimalValue(v.doubleValue()));
    }

    @Override
    public Optional<JRuleDecimalValue> evolutionRateUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.evolutionRateUntil(item, timestamp, serviceId))
                .map(v -> new JRuleDecimalValue(v.doubleValue()));
    }

    @Deprecated
    @Override
    public Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.evolutionRate(item, begin, end, serviceId))
                .map(v -> new JRuleDecimalValue(v.doubleValue()));
    }

    @Override
    public Optional<JRuleDecimalValue> evolutionRateBetween(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.evolutionRateBetween(item, begin, end, serviceId))
                .map(v -> new JRuleDecimalValue(v.doubleValue()));
    }

    @Override
    public Optional<Long> countBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.countBetween(item, begin, end, serviceId));
    }

    @Override
    public Optional<Long> countSince(ZonedDateTime begin, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.countSince(item, begin, serviceId));
    }

    @Override
    public Optional<Long> countUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.countUntil(item, timestamp, serviceId));
    }

    @Override
    public Optional<Long> countStateChangesSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.countStateChangesSince(item, timestamp, serviceId));
    }

    @Override
    public Optional<Long> countStateChangesUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.countStateChangesUntil(item, timestamp, serviceId));
    }

    @Override
    public Optional<Long> countStateChangesBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.countStateChangesBetween(item, begin, end, serviceId));
    }

    @Override
    public Optional<List<JRuleHistoricState>> getAllStatesSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.getAllStatesSince(item, timestamp, serviceId))
                .map(i -> StreamSupport.stream(i.spliterator(), false).map(this::mapHistoricItem)
                        .filter(Optional::isPresent).map(Optional::get).toList());
    }

    @Override
    public Optional<List<JRuleHistoricState>> getAllStatesUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.getAllStatesUntil(item, timestamp, serviceId))
                .map(i -> StreamSupport.stream(i.spliterator(), false).map(this::mapHistoricItem)
                        .filter(Optional::isPresent).map(Optional::get).toList());
    }

    @Override
    public Optional<List<JRuleHistoricState>> getAllStatesBetween(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        Item item = getItem(name);
        return Optional.ofNullable(PersistenceExtensions.getAllStatesBetween(item, begin, end, serviceId))
                .map(i -> StreamSupport.stream(i.spliterator(), false).map(this::mapHistoricItem)
                        .filter(Optional::isPresent).map(Optional::get).toList());
    }

    @Override
    public void removeAllStatesBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId) {
        Item item = getItem(name);
        PersistenceExtensions.removeAllStatesBetween(item, begin, end, serviceId);
    }

    @Override
    public void removeAllStatesSince(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        PersistenceExtensions.removeAllStatesSince(item, timestamp, serviceId);
    }

    @Override
    public void removeAllStatesUntil(ZonedDateTime timestamp, @Nullable String serviceId) {
        Item item = getItem(name);
        PersistenceExtensions.removeAllStatesUntil(item, timestamp, serviceId);
    }
}
