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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalQuantityGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleQuantityValue;

/**
 * The {@link JRuleQuantityGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleQuantityGroupItem extends JRuleQuantityItem, JRuleGroupItem<JRuleQuantityItem> {
    static JRuleQuantityGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalQuantityGroupItem.class);
    }

    static Optional<JRuleQuantityGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default Set<JRuleQuantityItem> memberItems() {
        return memberItems(false);
    }

    default Set<JRuleQuantityItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream()
                .map(jRuleItem -> (JRuleQuantityItem) jRuleItem).collect(Collectors.toSet());
    }

    default void sendCommand(JRuleQuantityValue command) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void sendCommand(double command, String unit) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRuleQuantityValue(command, unit)));
    }

    default void sendCommand(int command, String unit) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRuleQuantityValue(command, unit)));
    }

    default void postUpdate(JRuleQuantityValue state) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }

    default void postUpdate(double state, String unit) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.postUncheckedUpdate(new JRuleQuantityValue(state, unit)));
    }

    default void postUpdate(int state, String unit) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.postUncheckedUpdate(new JRuleQuantityValue(state, unit)));
    }
}
