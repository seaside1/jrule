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
package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleColorValue;

/**
 * The {@link JRuleColorItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleColorItem extends JRuleItem {

    public static JRuleColorValue getState(String itemName) {
        return JRuleEventHandler.get().getColorValue(itemName);
    }

    public static void sendCommand(String itemName, JRuleColorValue colorValue) {
        JRuleEventHandler.get().sendCommand(itemName, colorValue);
    }

    public static void postUpdate(String itemName, JRuleColorValue colorValue) {
        JRuleEventHandler.get().postUpdate(itemName, colorValue);
    }
}
