/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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
package org.openhab.automation.jrule.items;

import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalColorItem;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;

/**
 * The {@link JRuleColorItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleColorItem extends JRuleDimmerItem {
    static JRuleColorItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalColorItem.class);
    }

    static Optional<JRuleColorItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends a hsb command
     *
     * @param command command to send.
     */
    default void sendCommand(JRuleHsbValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a hsb update
     *
     * @param state update to send
     */
    default void postUpdate(JRuleHsbValue state) {
        postUncheckedUpdate(state);
    }

    default JRuleHsbValue getStateAsHsb() {
        return JRuleEventHandler.get().getValue(getName(), JRuleHsbValue.class);
    }
}
