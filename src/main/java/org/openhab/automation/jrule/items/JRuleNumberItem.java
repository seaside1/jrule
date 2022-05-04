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
 * The {@link JRuleNumberItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleNumberItem extends JRuleItem {

    public final String itemName;

    private JRuleNumberItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleNumberItem forName(String itemName) {
        return new JRuleNumberItem(itemName);
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

    public static void sendCommand(String itemName, double value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    public static void postUpdate(String itemName, double value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public static void sendCommand(String itemName, double value, String unit) {
        JRuleEventHandler.get().sendCommand(itemName, value, unit);
    }

    public static void postUpdate(String itemName, double value, String unit) {
        JRuleEventHandler.get().postUpdate(itemName, value, unit);
    }

    public static Double getState(String itemName) {
        return JRuleEventHandler.get().getStateFromItemAsDouble(itemName);
    }
}
