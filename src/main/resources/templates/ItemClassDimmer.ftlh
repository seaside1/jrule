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
package ${package};

import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleDimmerTrigger;

/**
 * Automatically Generated Class
 * for Item ${ItemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${ItemClass} implements JRuleDimmerTrigger {

    public static final String ITEM = "${ItemName}";
    
    public static int getState() {
        return JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).getState();
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).sendCommand(command);
    }

    public static void sendCommand(int command) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).postUpdate(value);
    }

    public static void postUpdate(int value) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).postUpdate(value);
    }
}