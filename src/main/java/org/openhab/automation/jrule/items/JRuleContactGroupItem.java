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
import org.openhab.automation.jrule.internal.items.JRuleInternalContactGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;

/**
 * The {@link JRuleContactGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleContactGroupItem extends JRuleContactItem, JRuleGroupItem<JRuleContactItem> {
    static JRuleContactGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalContactGroupItem.class);
    }

    static Optional<JRuleContactGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default Set<JRuleContactItem> memberItems() {
        return memberItems(false);
    }

    default Set<JRuleContactItem> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive).stream()
                .map(jRuleItem -> (JRuleContactItem) jRuleItem).collect(Collectors.toSet());
    }

    default void postUpdate(JRuleOpenClosedValue state) {
        JRuleEventHandler.get().getGroupMemberItems(getName(), false).forEach(i -> i.postUncheckedUpdate(state));
    }
}
