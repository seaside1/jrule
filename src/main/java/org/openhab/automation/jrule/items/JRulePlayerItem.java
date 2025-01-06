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
import org.openhab.automation.jrule.internal.items.JRuleInternalPlayerItem;
import org.openhab.automation.jrule.rules.value.JRuleNextPreviousValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleRewindFastforwardValue;

/**
 * The {@link JRulePlayerItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRulePlayerItem extends JRuleItem {
    String PLAY = "PLAY";
    String PAUSE = "PAUSE";
    String NEXT = "NEXT";
    String PREVIOUS = "PREVIOUS";
    String REWIND = "REWIND";
    String FASTFORWARD = "FASTFORWARD";

    static JRulePlayerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalPlayerItem.class);
    }

    static Optional<JRulePlayerItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends a play/pause command
     *
     * @param command command to send.
     */
    default void sendCommand(JRulePlayPauseValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a play/pause update
     *
     * @param state update to send
     */
    default void postUpdate(JRulePlayPauseValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a rewind/fastforward update
     *
     * @param state update to send
     */
    default void postUpdate(JRuleRewindFastforwardValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a rewind/fastforward command.
     *
     * @param command command to send.
     */
    default void sendCommand(JRuleRewindFastforwardValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a next/previous command.
     *
     * @param command command to send.
     */
    default void sendCommand(JRuleNextPreviousValue command) {
        sendUncheckedCommand(command);
    }

    default JRulePlayPauseValue getStateAsPlayPause() {
        return JRuleEventHandler.get().getValue(getName(), JRulePlayPauseValue.class);
    }

    default JRuleRewindFastforwardValue getStateAsRewindFastforward() {
        return JRuleEventHandler.get().getValue(getName(), JRuleRewindFastforwardValue.class);
    }
}
