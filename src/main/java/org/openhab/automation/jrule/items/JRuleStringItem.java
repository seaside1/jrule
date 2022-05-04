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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;

/**
 * The {@link JRuleStringItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleStringItem extends JRuleItem {

    private final String itemName;

    private JRuleStringItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleStringItem forName(String itemName) {
        return new JRuleStringItem(itemName);
    }

    public String getItemState() {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    public void sendItemCommand(String value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postItemUpdate(String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public static String getState(String itemName) {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    public static void sendCommand(String itemName, String value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void postUpdate(String itemName, String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }
}
