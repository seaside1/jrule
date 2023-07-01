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
import java.util.Set;
import java.util.stream.Collectors;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalNumberGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;

/**
 * The {@link JRuleNumberGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleNumberGroupItem extends JRuleNumberItem, JRuleGroupItem<JRuleNumberItem> {
    static JRuleNumberGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalNumberGroupItem.class);
    }

    static Optional<JRuleNumberGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default Set<JRuleNumberItem> memberItems() {
        return memberItems(false);
    }

    default Set<JRuleNumberItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream()
                .map(jRuleItem -> (JRuleNumberItem) jRuleItem).collect(Collectors.toSet());
    }

    default void sendCommand(JRuleDecimalValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleDecimalValue state) {
        JRuleEventHandler.get().postUpdate(getName(), state);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(double command) {
        JRuleEventHandler.get().sendCommand(getName(), new JRuleDecimalValue(command));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRuleDecimalValue(command)));
    }

    default void sendCommand(int command) {
        JRuleEventHandler.get().sendCommand(getName(), new JRuleDecimalValue(command));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRuleDecimalValue(command)));
    }

    default void postUpdate(double state) {
        JRuleEventHandler.get().postUpdate(getName(), new JRuleDecimalValue(state));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.postUncheckedUpdate(new JRuleDecimalValue(state)));
    }

    default void postUpdate(int state) {
        JRuleEventHandler.get().postUpdate(getName(), new JRuleDecimalValue(state));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.postUncheckedUpdate(new JRuleDecimalValue(state)));
    }
}
