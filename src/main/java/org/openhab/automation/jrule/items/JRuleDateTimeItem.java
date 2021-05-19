/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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

import java.util.Date;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;

/**
 * The {@link JRuleDateTimeItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleDateTimeItem extends JRuleItem {

    protected static void sendCommand(String itemName, Date date) {
        JRuleEventHandler.get().sendCommand(itemName, date);
    }

    protected static void postUpdate(String itemName, Date date) {
        JRuleEventHandler.get().postUpdate(itemName, date);
    }

    protected static Date getState(String name) {
        return JRuleEventHandler.get().getStateFromItemAsDate(name);
    }
}
