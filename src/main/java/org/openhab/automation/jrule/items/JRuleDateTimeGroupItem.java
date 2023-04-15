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
import java.util.Set;
import java.util.stream.Collectors;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
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

    default Set<JRuleDateTimeItem> memberItems() {
        return memberItems(false);
    }

    default Set<JRuleDateTimeItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream()
                .map(jRuleItem -> (JRuleDateTimeItem) jRuleItem).collect(Collectors.toSet());
    }

    default void sendCommand(JRuleDateTimeValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleDateTimeValue state) {
        JRuleEventHandler.get().postUpdate(getName(), state);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(Date command) {
        JRuleEventHandler.get().sendCommand(getName(), new JRuleDateTimeValue(command));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRuleDateTimeValue(command)));
    }

    default void sendCommand(ZonedDateTime command) {
        JRuleEventHandler.get().sendCommand(getName(), new JRuleDateTimeValue(command));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRuleDateTimeValue(command)));
    }

    default void postUpdate(Date state) {
        JRuleEventHandler.get().postUpdate(getName(), new JRuleDateTimeValue(state));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.postUncheckedUpdate(new JRuleDateTimeValue(state)));
    }

    default void postUpdate(ZonedDateTime state) {
        JRuleEventHandler.get().postUpdate(getName(), new JRuleDateTimeValue(state));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.postUncheckedUpdate(new JRuleDateTimeValue(state)));
    }
}
