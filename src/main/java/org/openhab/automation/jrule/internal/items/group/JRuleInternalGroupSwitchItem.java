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
package org.openhab.automation.jrule.internal.items.group;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalItem;
import org.openhab.automation.jrule.items.JRuleGroupSwitchItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * The {@link JRuleInternalGroupSwitchItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleInternalGroupSwitchItem extends JRuleInternalItem<JRuleOnOffValue> implements JRuleGroupSwitchItem {

    protected JRuleInternalGroupSwitchItem(String itemName) {
        super(itemName);
    }

    public static JRuleInternalGroupSwitchItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalGroupSwitchItem.class);
    }

    @Override
    public JRuleOnOffValue getState() {
        return JRuleEventHandler.get().getOnOffValue(getName());
    }
}
