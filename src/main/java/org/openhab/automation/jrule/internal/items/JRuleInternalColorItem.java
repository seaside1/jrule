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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleColorItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleInternalColorItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalColorItem extends JRuleInternalItem<JRuleHsbValue> implements JRuleColorItem {

    public JRuleInternalColorItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(name, command);
    }

    public void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleEventHandler.get().sendCommand(name, command);
    }

    public void postUpdate(JRuleHsbValue value) {
        JRuleEventHandler.get().postUpdate(name, value);
    }

    public void postUpdate(JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(name, state);
    }

    public void postUpdate(int value) {
        JRuleEventHandler.get().postUpdate(name, new JRulePercentValue(value));
    }

    public void sendCommand(boolean command) {
        JRuleEventHandler.get().sendCommand(getName(), JRuleOnOffValue.valueOf(command));
    }

    public void postUpdate(boolean command) {
        JRuleEventHandler.get().postUpdate(getName(), JRuleOnOffValue.valueOf(command));
    }
}
