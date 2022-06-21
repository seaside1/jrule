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

import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * The {@link JRuleItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleItem implements JRuleCommonTrigger {

    protected String itemName;

    public JRuleItem(String itemName) {
        this.itemName = itemName;
    }

    public String getName() {
        return null; // Method overridden by generated item
    }

    public String getLabel() {
        return null; // Method overridden by generated item
    }

    public String getType() {
        return null; // Method overridden by generated item
    }

    public ZonedDateTime lastUpdated() {
        return lastUpdated(null);
    }

    public ZonedDateTime lastUpdated(String persistenceServiceId) {
        return JRulePersistenceExtentions.lastUpdate(itemName, persistenceServiceId);
    }

    public Boolean changedSince(ZonedDateTime timestamp) {
        return changedSince(timestamp, null);
    }

    public Boolean changedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtentions.changedSince(itemName, timestamp, persistenceServiceId);
    }

    public Boolean updatedSince(ZonedDateTime timestamp) {
        return updatedSince(timestamp, null);
    }

    public Boolean updatedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtentions.updatedSince(itemName, timestamp, persistenceServiceId);
    }
}
