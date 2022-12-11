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
import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleInternalDimmerItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalDimmerItem extends JRuleInternalItem<JRulePercentValue> implements JRuleDimmerItem {

    public JRuleInternalDimmerItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public void sendCommand(int command) {
        JRuleEventHandler.get().sendCommand(name, new JRulePercentValue(command));
    }

    public void postUpdate(int value) {
        JRuleEventHandler.get().postUpdate(name, new JRulePercentValue(value));
    }

    public void sendCommand(boolean command) {
        JRuleEventHandler.get().sendCommand(getName(), new JRulePercentValue(command));
    }

    public void postUpdate(boolean value) {
        JRuleEventHandler.get().postUpdate(getName(), new JRulePercentValue(value));
    }
}
