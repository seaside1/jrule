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

/**
 * The {@link JRuleItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleItem {

    protected String itemName;

    public JRuleItem(String itemName) {
        this.itemName = itemName;
    }

    public String getName() {
        return itemName;
    }

    public abstract String getLabel();

    public abstract String getType();

    public abstract String getId();

    public Optional<ZonedDateTime> lastUpdated() {
        return lastUpdated(null);
    }

    public Optional<ZonedDateTime> lastUpdated(String persistenceServiceId) {
        return JRulePersistenceExtensions.lastUpdate(itemName, persistenceServiceId);
    }

    public boolean changedSince(ZonedDateTime timestamp) {
        return changedSince(timestamp, null);
    }

    public boolean changedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.changedSince(itemName, timestamp, persistenceServiceId);
    }

    public boolean updatedSince(ZonedDateTime timestamp) {
        return updatedSince(timestamp, null);
    }

    public boolean updatedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.updatedSince(itemName, timestamp, persistenceServiceId);
    }
}
