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
import org.openhab.automation.jrule.internal.items.JRuleInternalSwitchGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * The {@link JRuleSwitchGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleSwitchGroupItem extends JRuleSwitchItem, JRuleGroupItem {
    static JRuleSwitchGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalSwitchGroupItem.class);
    }

    static Optional<JRuleSwitchGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default Set<JRuleSwitchItem> memberItems() {
        return memberItems(false);
    }

    default Set<JRuleSwitchItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream().map(i -> (JRuleSwitchItem) i)
                .collect(Collectors.toSet());
    }

    default void sendCommand(JRuleOnOffValue command) {
        memberItemsGeneric().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleOnOffValue state) {
        memberItemsGeneric().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(boolean command) {
        memberItemsGeneric().forEach(i -> i.sendUncheckedCommand(JRuleOnOffValue.valueOf(command)));
    }

    default void postUpdate(boolean state) {
        memberItemsGeneric().forEach(i -> i.postUncheckedUpdate(JRuleOnOffValue.valueOf(state)));
    }
}
