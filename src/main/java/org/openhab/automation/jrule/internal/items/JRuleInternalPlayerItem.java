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

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;

/**
 * The {@link JRuleInternalPlayerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalPlayerItem extends JRuleInternalItem<JRulePlayPauseValue> implements JRulePlayerItem {

    public JRuleInternalPlayerItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public static JRuleInternalPlayerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalPlayerItem.class);
    }

    public JRulePlayPauseValue getState() {
        return JRuleEventHandler.get().getPauseValue(name);
    }

    public void sendCommand(JRulePlayPauseValue command) {
        JRuleEventHandler.get().sendCommand(name, command);
    }

    public void postUpdate(JRulePlayPauseValue state) {
        JRuleEventHandler.get().postUpdate(name, state);
    }
}
