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

    protected JRuleStringItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleStringItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleStringItem.class);
    }

    public String getState() {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    public void sendCommand(String value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public void postUpdate(String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }
}
