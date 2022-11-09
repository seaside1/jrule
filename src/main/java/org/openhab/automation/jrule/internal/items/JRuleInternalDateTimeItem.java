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

import java.time.ZonedDateTime;
import java.util.Date;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;

/**
 * The {@link JRuleInternalDateTimeItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalDateTimeItem extends JRuleInternalItem<JRuleDateTimeValue> implements JRuleDateTimeItem {

    protected JRuleInternalDateTimeItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public static JRuleInternalDateTimeItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalDateTimeItem.class);
    }

    public void sendCommand(Date date) {
        JRuleEventHandler.get().sendCommand(name, date);
    }

    public void sendCommand(ZonedDateTime zonedDateTime) {
        JRuleEventHandler.get().sendCommand(name, zonedDateTime);
    }

    public void postUpdate(Date date) {
        JRuleEventHandler.get().postUpdate(name, date);
    }

    public void postUpdate(ZonedDateTime zonedDateTime) {
        JRuleEventHandler.get().postUpdate(name, zonedDateTime);
    }

    public ZonedDateTime getZonedDateTimeState() {
        return JRuleEventHandler.get().getStateFromItemAsZonedDateTime(name);
    }
}
