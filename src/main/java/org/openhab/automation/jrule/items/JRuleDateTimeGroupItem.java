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
import org.openhab.automation.jrule.internal.items.JRuleInternalDateTimeGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;

/**
 * The {@link JRuleDateTimeGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDateTimeGroupItem extends JRuleDateTimeItem, JRuleGroupItem<JRuleDateTimeItem> {
    static JRuleDateTimeGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalDateTimeGroupItem.class);
    }

    static Optional<JRuleDateTimeGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default void sendCommand(JRuleDateTimeValue command) {
        memberItems().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleDateTimeValue state) {
        memberItems().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(Date command) {
        memberItems().forEach(i -> i.sendUncheckedCommand(new JRuleDateTimeValue(command)));
    }

    default void sendCommand(ZonedDateTime command) {
        memberItems().forEach(i -> i.sendUncheckedCommand(new JRuleDateTimeValue(command)));
    }

    default void postUpdate(Date state) {
        memberItems().forEach(i -> i.postUncheckedUpdate(new JRuleDateTimeValue(state)));
    }

    default void postUpdate(ZonedDateTime state) {
        memberItems().forEach(i -> i.postUncheckedUpdate(new JRuleDateTimeValue(state)));
    }
}
