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
import org.openhab.automation.jrule.internal.items.JRuleInternalPlayerGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleNextPreviousValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleRewindFastforwardValue;

/**
 * The {@link JRulePlayerGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRulePlayerGroupItem extends JRulePlayerItem, JRuleGroupItem<JRulePlayerItem> {
    static JRulePlayerGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalPlayerGroupItem.class);
    }

    static Optional<JRulePlayerGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default Set<JRulePlayerItem> memberItems() {
        return memberItems(false);
    }

    default Set<JRulePlayerItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream()
                .map(jRuleItem -> (JRulePlayerItem) jRuleItem).collect(Collectors.toSet());
    }

    default void sendCommand(JRulePlayPauseValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRulePlayPauseValue state) {
        JRuleEventHandler.get().postUpdate(getName(), state);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(JRuleRewindFastforwardValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }

    default void sendCommand(JRuleNextPreviousValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.sendUncheckedCommand(command));
    }
}
