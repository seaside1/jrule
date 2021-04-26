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
import org.openhab.binding.jrule.rules.JRuleOnOffValue;

/**
 * The {@link JRuleSwitchItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleSwitchItem extends JRuleItem {

    public static final String TRIGGER_RECEIVED_UPDATE_ON = "received update ON";
    public static final String TRIGGER_RECEIVED_UPDATE_OFF = "received update OFF";
    public static final String TRIGGER_RECEIVED_COMMAND_ON = "received command ON";
    public static final String TRIGGER_RECEIVED_COMMAND_OFF = "received command OFF";
    public static final String TRIGGER_CHANGED_FROM_ON_TO_OFF = "Changed from ON to OFF";
    public static final String TRIGGER_CHANGED_FROM_ON = "Changed from ON";
    public static final String TRIGGER_CHANGED_FROM_OFF = "Changed from OFF";
    public static final String TRIGGER_CHANGED_TO_OFF = "Changed to OFF";
    public static final String TRIGGER_CHANGED_TO_ON = "Changed to ON";
    public static final String TRIGGER_CHANGED_FROM_OFF_TO_ON = "Changed from OFF to ON";

    protected static JRuleOnOffValue getState(String itemName) {
        return JRuleEventHandler.get().getOnOffValue(itemName);
    }

    protected static void sendCommand(String itemName, JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    protected static void postUpdate(String itemName, JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }
}
