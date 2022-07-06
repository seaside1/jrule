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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.core.library.types.DecimalType;

/**
 * The {@link JRuleNumberItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleNumberItem extends JRuleItem {

    protected JRuleNumberItem(String itemName) {
        super(itemName);
    }

    public static JRuleNumberItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleNumberItem.class);
    }

    public void sendCommand(double value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(double value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void sendCommand(double value, String unit) {
        JRuleEventHandler.get().sendCommand(itemName, value, unit);
    }

    public void postUpdate(double value, String unit) {
        JRuleEventHandler.get().postUpdate(itemName, value, unit);
    }

    public Double getState() {
        return JRuleEventHandler.get().getStateFromItemAsDouble(itemName);
    }

    public Double getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return Double.parseDouble(JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
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
}
