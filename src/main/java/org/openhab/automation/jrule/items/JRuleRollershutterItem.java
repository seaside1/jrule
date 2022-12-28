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
package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.*;

/**
 * The {@link JRuleRollershutterItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleRollershutterItem extends JRuleItem {
    String STOP = JRuleStopMoveValue.STOP.stringValue();
    String MOVE = JRuleStopMoveValue.MOVE.stringValue();
    String UP = JRuleUpDownValue.UP.stringValue();
    String DOWN = JRuleUpDownValue.DOWN.stringValue();

    static JRuleRollershutterItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleRollershutterItem.class);
    }

    /**
     * Sends a percent command
     * 
     * @param command command to send.
     */
    default void sendCommand(JRulePercentValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a percent update
     * 
     * @param state update to send
     */
    default void postUpdate(JRulePercentValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a percent command.
     * 
     * @param command as number via JRulePercentValue will be send.
     */
    default void sendCommand(int command) {
        sendUncheckedCommand(new JRulePercentValue(command));
    }

    /**
     * Sends a up/down command.
     *
     * @param command command to send.
     */
    default void sendCommand(JRuleUpDownValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a up/down update.
     *
     * @param state state to send.
     */
    default void postUpdate(JRuleUpDownValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * Sends a stop/move command.
     *
     * @param command command to send.
     */
    default void sendCommand(JRuleStopMoveValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a percent update.
     * 
     * @param state as number via JRulePercentValue will be send.
     */
    default void postUpdate(int state) {
        postUncheckedUpdate(new JRulePercentValue(state));
    }

    default JRulePercentValue getStateAsPercent() {
        return JRuleEventHandler.get().getValue(getName(), JRulePercentValue.class);
    }
}
