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
package org.openhab.binding.jrule.items;

import org.openhab.binding.jrule.internal.handler.JRuleEventHandler;

/**
 * The {@link JRuleNumberItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleNumberItem extends JRuleItem {

    protected static void sendCommand(String itemName, double value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    protected static void postUpdate(String itemName, double value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    protected static double getState(String name) {
        return JRuleEventHandler.get().getStateFromItemAsDouble(name);
    }
}
