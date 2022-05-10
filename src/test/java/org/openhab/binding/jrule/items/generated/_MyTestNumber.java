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
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * The {@link _MyTestNumber}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class _MyTestNumber implements JRuleCommonTrigger {
    public static final String ITEM = "MyTestNumber";

    public static Double getState() {
        return JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).getState();
    }

    public static void sendCommand(double command) {
        JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).sendCommand(command);
    }

    public static void postUpdate(double value) {
        JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).postUpdate(value);
    }

    public static void sendCommand(double command, String unit) {
        JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).sendCommand(command, unit);
    }

    public static void postUpdate(double value, String unit) {
        JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).postUpdate(value, unit);
    }
}
