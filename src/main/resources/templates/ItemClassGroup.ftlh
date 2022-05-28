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

import java.util.Set;

import org.openhab.automation.jrule.items.JRuleGroupItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * for Item ${ItemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${ItemClass} implements JRuleCommonTrigger {

    public static final String ITEM = "${ItemName}";
    
    public static String getState() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getState();
    }

    public static JRuleOnOffValue getStateAsOnOffValue() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getStateAsOnOffValue();
    }

    public static JRulePlayPauseValue getStateAsPlayPauseValue() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getStateAsPlayPauseValue();
    }

    public static JRuleUpDownValue getStateAsUpDownValue() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getStateAsUpDownValue();
    }

    public static void sendCommand(String command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(command);
    }

    public static void sendCommandToAll(String command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommandToAll(command);
    }

    public static Set<String> members() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).members();
    }

    public static void postUpdate(String value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }

    public static void sendCommand(JRulePlayPauseValue command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(command);
    }

    public static void postUpdate(JRulePlayPauseValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }

    public static void sendCommand(JRuleUpDownValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(value);
    }

    public static void postUpdate(JRuleUpDownValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }
}
