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

import org.openhab.automation.jrule.internal.JRuleLog;
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
 * The {@link JRulePersistenceExtentions}
 *
 * @author Arne Seime - Initial contribution
 */
class JRulePersistenceExtentions {

    private final static Logger logger = LoggerFactory.getLogger(JRulePersistenceExtentions.class);
    private static final String LOG_NAME_PERSISTENCE = "JRulePersistence";

    public static String historicState(String itemName, ZonedDateTime timestamp) {
        return historicState(itemName, timestamp, null);
    }

    public static String historicState(String itemName, ZonedDateTime timestamp, String serviceId) {
        State state = historicStateInternal(itemName, timestamp, serviceId);
        if (state != null) {
            return state.toString();
        }
        return null;
    }

    public static JRuleRawValue historicStateAsRawValue(String itemName, ZonedDateTime timestamp) {
        return historicStateAsRawValue(itemName, timestamp, null);
    }

    public static JRuleRawValue historicStateAsRawValue(String itemName, ZonedDateTime timestamp, String serviceId) {
        State state = historicStateInternal(itemName, timestamp, serviceId);
        if (state != null) {
            RawType raw = (RawType) state;
            return new JRuleRawValue(raw.getMimeType(), raw.getBytes());
        }
        return null;
    }

    private static State historicStateInternal(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            HistoricItem historicState = serviceId == null ? PersistenceExtensions.historicState(item, timestamp)
                    : PersistenceExtensions.historicState(item, timestamp, serviceId);
            if (historicState != null) {
                return historicState.getState();
            }
        }
        return null;
    }

    public static boolean changedSince(String itemName, ZonedDateTime timestamp) {
        return changedSince(itemName, timestamp, null);
    }

    public static boolean changedSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            Boolean changedSince = serviceId == null ? PersistenceExtensions.changedSince(item, timestamp)
                    : PersistenceExtensions.changedSince(item, timestamp, serviceId);
            if (changedSince != null) {
                return changedSince;
            }
        }
        return false;
    }

    public static boolean updatedSince(String itemName, ZonedDateTime timestamp) {
        return updatedSince(itemName, timestamp, null);
    }

    public static boolean updatedSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            return serviceId == null ? PersistenceExtensions.updatedSince(item, timestamp)
                    : PersistenceExtensions.updatedSince(item, timestamp, serviceId);
        }
        return false;
    }

    public static DecimalType maximumSince(String itemName, ZonedDateTime timestamp) {
        return maximumSince(itemName, timestamp, null);
    }

    public static DecimalType maximumSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            HistoricItem historicState = serviceId == null ? PersistenceExtensions.maximumSince(item, timestamp)
                    : PersistenceExtensions.maximumSince(item, timestamp, serviceId);
            if (historicState != null) {
                return historicState.getState().as(DecimalType.class);
            }
        }
        return null;
    }

    public static DecimalType minimumSince(String itemName, ZonedDateTime timestamp) {
        return minimumSince(itemName, timestamp, null);
    }

    public static DecimalType minimumSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            HistoricItem historicState = serviceId == null ? PersistenceExtensions.minimumSince(item, timestamp)
                    : PersistenceExtensions.minimumSince(item, timestamp, serviceId);
            if (historicState != null) {
                return historicState.getState().as(DecimalType.class);
            }
        }
        return null;
    }

    public static DecimalType varianceSince(String itemName, ZonedDateTime timestamp) {
        return varianceSince(itemName, timestamp, null);
    }

    public static DecimalType varianceSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            return serviceId == null ? PersistenceExtensions.varianceSince(item, timestamp)
                    : PersistenceExtensions.varianceSince(item, timestamp, serviceId);
        }
        return null;
    }

    public static DecimalType deviationSince(String itemName, ZonedDateTime timestamp) {
        return deviationSince(itemName, timestamp, null);
    }

    public static DecimalType deviationSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            return serviceId == null ? PersistenceExtensions.deviationSince(item, timestamp)
                    : PersistenceExtensions.deviationSince(item, timestamp, serviceId);
        }
        return null;
    }

    public static DecimalType averageSince(String itemName, ZonedDateTime timestamp) {
        return averageSince(itemName, timestamp, null);
    }

    public static DecimalType averageSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            return serviceId == null ? PersistenceExtensions.averageSince(item, timestamp)
                    : PersistenceExtensions.averageSince(item, timestamp, serviceId);
        }
        return null;
    }

    public static DecimalType sumSince(String itemName, ZonedDateTime timestamp) {
        return sumSince(itemName, timestamp, null);
    }

    public static DecimalType sumSince(String itemName, ZonedDateTime timestamp, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            return serviceId == null ? PersistenceExtensions.sumSince(item, timestamp)
                    : PersistenceExtensions.sumSince(item, timestamp, serviceId);
        }
        return null;
    }

    public static ZonedDateTime lastUpdate(String itemName) {
        return lastUpdate(itemName, null);
    }

    public static ZonedDateTime lastUpdate(String itemName, String serviceId) {
        Item item = getItem(itemName);
        if (item != null) {
            return serviceId == null ? PersistenceExtensions.lastUpdate(item)
                    : PersistenceExtensions.lastUpdate(item, serviceId);
        }
        return null;
    }

    private static Item getItem(String itemName) {
        try {
            ItemRegistry itemRegistry = JRuleEventHandler.get().getItemRegistry();
            if (itemRegistry == null) {
                JRuleLog.error(logger, LOG_NAME_PERSISTENCE, "Item registry is not set can't get LastUpdate");
                return null;
            }

            return itemRegistry.getItem(itemName);

        } catch (ItemNotFoundException e) {
            if (logger.isDebugEnabled()) {
                JRuleLog.debug(logger, LOG_NAME_PERSISTENCE, "Failed to get item: {} in order to getLastUpdate",
                        itemName, e);
            } else {
                JRuleLog.error(logger, LOG_NAME_PERSISTENCE, "Failed to get item: {} in order to getLastUpdate",
                        itemName);
            }
        }
        return null;
    }
}
