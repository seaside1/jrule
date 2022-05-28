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

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleRollershutterItem;
import org.openhab.automation.jrule.rules.value.JRuleStopMoveValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * for Item ${ItemName}
 *
 * @author Timo Litzius - Initial contribution
 */
public class ${ItemClass} implements JRuleCommonTrigger {
 
    public static final String ITEM = "${ItemName}";
    
    public static int getState() {
        return JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).getState();
    }

    public static void sendCommand(JRuleUpDownValue command) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleStopMoveValue command) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(command);
    }

    public static void sendCommand(int value) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(value);
    }

    public static void postUpdate(JRuleUpDownValue command) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).postUpdate(command);
    }

    public static void postUpdate(int value) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(value);
    }

}