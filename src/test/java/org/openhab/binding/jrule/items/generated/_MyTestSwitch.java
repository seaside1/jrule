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
package org.openhab.binding.jrule.items.generated;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleSwitchTrigger;

/**
 * Automatically Generated Class
 * The {@link _MyTestSwitch}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class _MyTestSwitch implements JRuleSwitchTrigger {
    public static final String ITEM = "MyTestSwitch";

    public static JRuleOnOffValue getState() {
        return JRuleItemRegistry.get(ITEM, JRuleSwitchItem.class).getState();
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleSwitchItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleSwitchItem.class).postUpdate(value);
    }
}
