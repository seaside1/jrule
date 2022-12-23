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

import java.util.Set;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleRefreshValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleGroupItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleGroupItem extends JRuleItem {
    @Deprecated
    default Set<String> members() {
        return JRuleEventHandler.get().getGroupMemberNames(getName(), false);
    }

    default Set<? extends JRuleItem> memberItems() {
        return memberItemsGeneric(false);
    }

    default Set<? extends JRuleItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive);
    }

    default Set<JRuleItem> memberItemsGeneric() {
        return memberItemsGeneric(false);
    }

    default Set<JRuleItem> memberItemsGeneric(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive);
    }

    default void sendUncheckedCommand(JRuleValue command) {
        memberItemsGeneric().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUncheckedUpdate(JRuleValue state) {
        memberItemsGeneric().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void postUpdate(JRuleRefreshValue state) {
        memberItemsGeneric().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void postNullUpdate() {
        memberItemsGeneric().forEach(i -> i.postUpdate(null));
    }

    @Override
    default boolean isGroup() {
        return true;
    }
}
