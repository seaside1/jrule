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
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.trigger.JRulePlayerTrigger;

/**
 * Automatically Generated Class
 * The {@link _MyTestPlayer}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class _MyTestPlayer implements JRulePlayerTrigger {
    public static final String ITEM = "MyTestPlayer";

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
