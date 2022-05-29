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
import org.openhab.core.library.types.DecimalType;

/**
 * The {@link JRuleItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleItem implements JRuleCommonTrigger {
    public static final String TRIGGER_CHANGED = "Changed";
    public static final String TRIGGER_RECEIVED_COMMAND = "received command";
    public static final String TRIGGER_RECEIVED_UPDATE = "received update";

    protected String itemName;

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

    public Double maximumSince(ZonedDateTime timestamp) {
        return maximumSince(timestamp, null);
    }

    public Double maximumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        DecimalType state = JRulePersistenceExtentions.maximumSince(itemName, timestamp, persistenceServiceId);
        if (state != null) {
            return state.doubleValue();
        } else {
            return null;
        }
    }

    public Double minimumSince(ZonedDateTime timestamp) {
        return minimumSince(timestamp, null);
    }

    public Double minimumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        DecimalType state = JRulePersistenceExtentions.minimumSince(itemName, timestamp, persistenceServiceId);
        if (state != null) {
            return state.doubleValue();
        } else {
            return null;
        }
    }

    public Double varianceSince(ZonedDateTime timestamp) {
        return varianceSince(timestamp, null);
    }

    public Double varianceSince(ZonedDateTime timestamp, String persistenceServiceId) {
        DecimalType state = JRulePersistenceExtentions.varianceSince(itemName, timestamp, persistenceServiceId);
        if (state != null) {
            return state.doubleValue();
        } else {
            return null;
        }
    }

    public Double deviationSince(ZonedDateTime timestamp) {
        return deviationSince(timestamp, null);
    }

    public Double deviationSince(ZonedDateTime timestamp, String persistenceServiceId) {
        DecimalType state = JRulePersistenceExtentions.deviationSince(itemName, timestamp, persistenceServiceId);
        if (state != null) {
            return state.doubleValue();
        } else {
            return null;
        }
    }

    public Double averageSince(ZonedDateTime timestamp) {
        return averageSince(timestamp, null);
    }

    public Double averageSince(ZonedDateTime timestamp, String persistenceServiceId) {
        DecimalType state = JRulePersistenceExtentions.averageSince(itemName, timestamp, persistenceServiceId);
        if (state != null) {
            return state.doubleValue();
        } else {
            return null;
        }
    }

    public Double sumSince(ZonedDateTime timestamp) {
        return sumSince(timestamp, null);
    }

    public Double sumSince(ZonedDateTime timestamp, String persistenceServiceId) {
        DecimalType state = JRulePersistenceExtentions.sumSince(itemName, timestamp, persistenceServiceId);
        if (state != null) {
            return state.doubleValue();
        } else {
            return null;
        }
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
}
