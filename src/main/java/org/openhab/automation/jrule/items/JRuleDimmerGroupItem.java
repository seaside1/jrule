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
import org.openhab.automation.jrule.internal.items.JRuleInternalDimmerGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleDimmerGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDimmerGroupItem extends JRuleDimmerItem, JRuleSwitchGroupItem {
    static JRuleDimmerGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalDimmerGroupItem.class);
    }

    static Optional<JRuleDimmerGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default Set<? extends JRuleDimmerItem> memberItems() {
        return memberItems(false);
    }

    default Set<? extends JRuleDimmerItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream()
                .map(jRuleItem -> (JRuleDimmerItem) jRuleItem).collect(Collectors.toSet());
    }

    default void sendCommand(JRulePercentValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRulePercentValue state) {
        JRuleEventHandler.get().postUpdate(getName(), state);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(int command) {
        JRuleEventHandler.get().sendCommand(getName(), new JRulePercentValue(command));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRulePercentValue(command)));
    }

    default void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleIncreaseDecreaseValue state) {
        JRuleEventHandler.get().postUpdate(getName(), state);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }

    default void postUpdate(int state) {
        JRuleEventHandler.get().postUpdate(getName(), new JRulePercentValue(state));
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.postUncheckedUpdate(new JRulePercentValue(state)));
    }
}
