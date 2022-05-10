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
package org.openhab.binding.jrule.items.generated;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.automation.jrule.items.JRuleColorItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * The {@link _MyTestColor}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class _MyTestColor implements JRuleCommonTrigger {
    public static final String ITEM = "MyTestColor";

    public static JRuleColorValue getState() {
        return JRuleItemRegistry.get(ITEM, JRuleColorItem.class).getState();
    }

    public static JRuleOnOffValue getOnOffState() {
        return JRuleItemRegistry.get(ITEM, JRuleColorItem.class).getOnOffState();
    }

    public static int getPercentState() {
        return JRuleItemRegistry.get(ITEM, JRuleColorItem.class).getPercentState();
    }

    public static void sendCommand(JRuleColorValue command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void sendCommand(int command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleColorValue value) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).postUpdate(value);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).postUpdate(value);
    }

    public static void postUpdate(int value) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).postUpdate(value);
    }
}
