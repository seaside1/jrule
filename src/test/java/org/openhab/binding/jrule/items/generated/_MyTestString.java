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
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * The {@link _MyTestString}
 */
@NonNullByDefault
public class _MyTestString implements JRuleCommonTrigger {
    public static final String ITEM = "MyTestString";

    public static String getState() {
        return JRuleItemRegistry.get(ITEM, JRuleStringItem.class).getState();
    }

    public static void sendCommand(String command) {
        JRuleItemRegistry.get(ITEM, JRuleStringItem.class).sendCommand(command);
    }

    public static void postUpdate(String value) {
        JRuleItemRegistry.get(ITEM, JRuleStringItem.class).postUpdate(value);
    }
}
