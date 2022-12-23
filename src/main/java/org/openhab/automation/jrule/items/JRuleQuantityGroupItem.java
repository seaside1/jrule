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

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.items.JRuleInternalQuantityGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleQuantityValue;

/**
 * The {@link JRuleQuantityGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleQuantityGroupItem extends JRuleQuantityItem, JRuleGroupItem {
    static JRuleQuantityGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalQuantityGroupItem.class);
    }

    static Optional<JRuleQuantityGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default void sendCommand(JRuleQuantityValue command) {
        memberItemsGeneric().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void sendCommand(double command, String unit) {
        memberItemsGeneric().forEach(i -> i.sendUncheckedCommand(new JRuleQuantityValue(command, unit)));
    }

    default void postUpdate(JRuleQuantityValue state) {
        memberItemsGeneric().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void postUpdate(double state, String unit) {
        memberItemsGeneric().forEach(i -> i.postUncheckedUpdate(new JRuleQuantityValue(state, unit)));
    }
}
