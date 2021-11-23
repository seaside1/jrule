/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.automation.jrule.internal.handler;

import java.time.ZonedDateTime;

import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.persistence.extensions.PersistenceExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRulePersistenceExtentions}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRulePersistenceExtentions {

    private final static Logger logger = LoggerFactory.getLogger(JRulePersistenceExtentions.class);
    private static final String LOG_NAME_PERSISTENCE = "JRulePersistence";

    public static ZonedDateTime getLastUpdate(String itemName) {
        return getLastUpdate(itemName, null);
    }

    public static ZonedDateTime getLastUpdate(String itemName, String serviceId) {
        try {
            ItemRegistry itemRegistry = JRuleEventHandler.get().getItemRegistry();
            if (itemRegistry == null) {
                JRuleLog.error(logger, LOG_NAME_PERSISTENCE, "Item registry is not set can't get LastUpdate");
                return null;
            }
            return serviceId == null ? PersistenceExtensions.lastUpdate(itemRegistry.getItem(itemName))
                    : PersistenceExtensions.lastUpdate(itemRegistry.getItem(itemName), serviceId);
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
