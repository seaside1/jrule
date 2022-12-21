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
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

import java.util.Map;

/**
 * The {@link JRuleInternalSwitchItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalSwitchItem extends JRuleInternalItem implements JRuleSwitchItem {
    public JRuleInternalSwitchItem(String name, String label, String type, String id, Map<String, String> metadata) {
        super(name, label, type, id, metadata, tags);
    }

    public void sendCommand(boolean command) {
        JRuleEventHandler.get().sendCommand(getName(), JRuleOnOffValue.valueOf(command));
    }

    public void postUpdate(boolean state) {
        JRuleEventHandler.get().postUpdate(getName(), JRuleOnOffValue.valueOf(state));
    }

    @Override
    public JRuleOnOffValue getState() {
        return JRuleEventHandler.get().getValue(getName(), JRuleOnOffValue.class);
    }
}
