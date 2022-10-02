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
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.trigger.JRuleContactTrigger;

/**
 * The {@link JRuleContactItem} Items
 *
 * @author Timo Litzius - Initial contribution
 */
public abstract class JRuleContactItem extends JRuleItem implements JRuleContactTrigger {

    protected JRuleContactItem(String itemName) {
        super(itemName);
    }

    public static JRuleContactItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleContactItem.class);
    }

    public JRuleOpenClosedValue getState() {
        return JRuleEventHandler.get().getOpenClosedValue(itemName);
    }

    // Persistence method
    public JRuleOpenClosedValue getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRuleOpenClosedValue.getValueFromString(
                JRulePersistenceExtentions.historicState(itemName, timestamp, persistenceServiceId));
    }
}
