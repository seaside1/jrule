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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.rules.value.JRuleValue;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * The {@link JRuleInternalItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleInternalItem<T extends JRuleValue> implements JRuleItem<T> {
    protected String name;
    protected String label;
    protected String type;
    protected String id;

    public JRuleInternalItem(String name) {
        this.name = name;
    }

    @Override
    public String getStateAsString() {
        return JRuleEventHandler.get().getStringValue(name);
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
}
