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
import org.openhab.automation.jrule.internal.items.JRuleInternalStringGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

/**
 * The {@link JRuleStringGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleStringGroupItem extends JRuleStringItem, JRuleGroupItem<JRuleStringItem> {
    static JRuleStringGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalStringGroupItem.class);
    }

    static Optional<JRuleStringGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default Set<JRuleStringItem> memberItems() {
        return memberItems(false);
    }

    default Set<JRuleStringItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream()
                .map(jRuleItem -> (JRuleStringItem) jRuleItem).collect(Collectors.toSet());
    }

    default void sendCommand(JRuleStringValue command) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleStringValue state) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(String command) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(i -> i.sendUncheckedCommand(new JRuleStringValue(command)));
    }

    default void postUpdate(String state) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false)
                .forEach(m -> m.postUncheckedUpdate(new JRuleStringValue(state)));
    }
}
