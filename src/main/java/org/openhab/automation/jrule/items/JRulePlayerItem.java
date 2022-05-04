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
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;

/**
 * The {@link JRulePlayerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRulePlayerItem extends JRuleItem {

    public static final String TRIGGER_RECEIVED_UPDATE_ON = "received update PLAY";
    public static final String TRIGGER_RECEIVED_UPDATE_OFF = "received update PAUSE";
    public static final String TRIGGER_RECEIVED_COMMAND_ON = "received command PLAY";
    public static final String TRIGGER_RECEIVED_COMMAND_OFF = "received command PAUSE";
    public static final String TRIGGER_CHANGED_FROM_ON_TO_OFF = "Changed from PLAY to PAUSE";
    public static final String TRIGGER_CHANGED_FROM_OFF_TO_ON = "Changed from PAUSE to PLAY";
    public static final String TRIGGER_CHANGED_FROM_ON = "Changed from PLAY";
    public static final String TRIGGER_CHANGED_FROM_OFF = "Changed from PAUSE";
    public static final String TRIGGER_CHANGED_TO_OFF = "Changed to PAUSE";
    public static final String TRIGGER_CHANGED_TO_ON = "Changed to PLAY";

    private final String itemName;

    protected JRulePlayerItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRulePlayerItem forName(String itemName) {
        return new JRulePlayerItem(itemName);
    }

    public JRulePlayPauseValue getItemState() {
        return JRuleEventHandler.get().getPauseValue(itemName);
    }

    public void sendItemCommand(JRulePlayPauseValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void postItemUpdate(JRulePlayPauseValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    public static JRulePlayPauseValue getState(String itemName) {
        return JRuleEventHandler.get().getPauseValue(itemName);
    }

    public static void sendCommand(String itemName, JRulePlayPauseValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public static void postUpdate(String itemName, JRulePlayPauseValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }
}
