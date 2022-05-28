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
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.trigger.JRulePlayerTrigger;

/**
 * Automatically Generated Class
 * for Item ${ItemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${ItemClass} implements JRulePlayerTrigger {
 
    public static final String ITEM = "${ItemName}";
    
    public static JRulePlayPauseValue getState() {
        return JRuleItemRegistry.get(ITEM, JRulePlayerItem.class).getState();
    }

    public static void sendCommand(JRulePlayPauseValue command) {
        JRuleItemRegistry.get(ITEM, JRulePlayerItem.class).sendCommand(command);
    }

    public static void postUpdate(JRulePlayPauseValue value) {
        JRuleItemRegistry.get(ITEM, JRulePlayerItem.class).postUpdate(value);
    }
}