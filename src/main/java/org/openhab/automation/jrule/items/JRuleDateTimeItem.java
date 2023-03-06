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

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalDateTimeItem;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;

/**
 * The {@link JRuleDateTimeItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDateTimeItem extends JRuleItem {
    static JRuleDateTimeItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalDateTimeItem.class);
    }

    static Optional<JRuleDateTimeItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends a date-time command
     * 
     * @param command command to send.
     */
    default void sendCommand(JRuleDateTimeValue command) {
        sendUncheckedCommand(command);
    }

    /**
     * Sends a date-time update
     * 
     * @param state update to send
     */
    default void postUpdate(JRuleDateTimeValue state) {
        postUncheckedUpdate(state);
    }

    /**
     * sends a date-time command
     * 
     * @param command date to send
     */
    default void sendCommand(Date command) {
        sendUncheckedCommand(new JRuleDateTimeValue(command));
    }

    /**
     * sends a date-time command
     * 
     * @param command date to send
     */
    default void sendCommand(ZonedDateTime command) {
        sendUncheckedCommand(new JRuleDateTimeValue(command));
    }

    /**
     * sends a date-time update
     * 
     * @param state date to send
     */
    default void postUpdate(Date state) {
        postUncheckedUpdate(new JRuleDateTimeValue(state));
    }

    /**
     * sends a date-time update
     * 
     * @param state date to send
     */
    default void postUpdate(ZonedDateTime state) {
        postUncheckedUpdate(new JRuleDateTimeValue(state));
    }

    default JRuleDateTimeValue getStateAsDateTime() {
        return JRuleEventHandler.get().getValue(getName(), JRuleDateTimeValue.class);
    }
}
