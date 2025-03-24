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

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.items.JRuleInternalStringItem;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

/**
 * The {@link JRuleStringItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleStringItem extends JRuleItem {
    static JRuleStringItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalStringItem.class);
    }

    static Optional<JRuleStringItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends a string command
     * 
     * @param command command to send.
     */
    default void sendCommand(@NonNull JRuleStringValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a string command only if current state is different
     *
     * @param command command to send.
     */
    default void sendCommandIfDifferent(@NonNull JRuleStringValue command) {
        if (!command.equals(getState())) {
            sendUncheckedCommand(command);
        }
    }

    /**
     * Sends a string update
     * 
     * @param state update to send
     */
    default void postUpdate(@NonNull JRuleStringValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a string update only if current state is different
     *
     * @param state update to send
     */
    default void postUpdateIfDifferent(@NonNull JRuleStringValue state) {
        if (!state.equals(getState())) {
            postUncheckedUpdate(state);
        }
    }

    /**
     * Sends a string command
     *
     * @param command string command
     */
    default void sendCommand(@NonNull String command) {
        sendUncheckedCommand(new JRuleStringValue(command));
    }

    /**
     * Sends a string command only if current state is different
     *
     * @param command string command
     */
    default void sendCommandIfDifferent(@NonNull String command) {
        sendCommandIfDifferent(new JRuleStringValue(command));
    }

    /**
     * Sends a string update
     *
     * @param state string command
     */
    default void postUpdate(@NonNull String state) {
        postUncheckedUpdate(new JRuleStringValue(state));
    }

    /**
     * Sends a string update only if current state is different
     *
     * @param state string command
     */
    default void postUpdateIfDifferent(@NonNull String state) {
        postUpdateIfDifferent(new JRuleStringValue(state));
    }
}
