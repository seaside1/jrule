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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.JRuleOnOffValue;

/**
 * The {@link JRuleGroupItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleGroupItem extends JRuleItem {

    protected static String getState(String itemName) {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    protected static JRuleOnOffValue getStateAsOnOffValue(String itemName) {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    public static void sendCommand(String itemName, JRuleOnOffValue value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void sendCommand(String itemName, String value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void postUpdate(String itemName, String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public static void postUpdate(String itemName, JRuleOnOffValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }
}
