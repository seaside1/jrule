/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleDimmerTrigger;

/**
 * The {@link JRuleDimmerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleDimmerItem extends JRuleItem implements JRuleDimmerTrigger {

    private final String itemName;

    protected JRuleDimmerItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleDimmerItem forName(String itemName) {
        return JRuleItemRegistry.get(itemName, JRuleDimmerItem.class);
    }

    public int getState() {
        return JRuleEventHandler.get().getStateFromItemAsInt(itemName);
    }

    public void sendCommand(JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    public void sendCommand(int command) {
        JRuleEventHandler.get().sendCommand(itemName, new JRulePercentType(command));
    }

    public void postUpdate(JRuleOnOffValue value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    public void postUpdate(int value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRulePercentType(value));
    }
}
