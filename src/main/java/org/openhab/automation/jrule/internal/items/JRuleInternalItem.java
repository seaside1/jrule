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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.items.metadata.JRuleMetadataRegistry;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.types.State;

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
    public Optional<ZonedDateTime> lastUpdated(String persistenceServiceId) {
        return JRulePersistenceExtensions.lastUpdate(name, persistenceServiceId);
    }

    @Override
    public boolean changedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.changedSince(name, timestamp, persistenceServiceId);
    }

    @Override
    public boolean updatedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.updatedSince(name, timestamp, persistenceServiceId);
    }

    @Override
    public Optional<JRuleValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.historicState(name, timestamp, persistenceServiceId);
    }

    @Override
    public Optional<JRuleValue> getStateAt(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.stateAt(name, timestamp, persistenceServiceId);
    }

    @Override
    public Optional<State> getPreviousState(boolean skipEquals, String persistenceServiceId) {
        return JRulePersistenceExtensions.previousState(name, skipEquals, persistenceServiceId);
    }

    @Override
    public void persist(JRuleValue state, ZonedDateTime time, String persistenceServiceId) {
        JRulePersistenceExtensions.persist(name, time, state, persistenceServiceId);
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
}
