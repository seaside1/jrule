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
import org.openhab.automation.jrule.internal.items.JRuleInternalDimmerGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

/**
 * The {@link JRuleDimmerGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleDimmerGroupItem<I extends JRuleDimmerItem> extends JRuleDimmerItem, JRuleSwitchGroupItem<I> {
    static JRuleDimmerGroupItem<JRuleDimmerItem> forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalDimmerGroupItem.class);
    }

    static Optional<JRuleDimmerGroupItem<JRuleDimmerItem>> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default void sendCommand(JRulePercentValue command) {
        this.memberItems().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRulePercentValue state) {
        this.memberItems().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(int command) {
        this.memberItems().forEach(i -> i.sendUncheckedCommand(new JRulePercentValue(command)));
    }

    default void sendCommand(JRuleIncreaseDecreaseValue command) {
        this.memberItems().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRuleIncreaseDecreaseValue state) {
        this.memberItems().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void postUpdate(int state) {
        this.memberItems().forEach(i -> i.postUncheckedUpdate(new JRulePercentValue(state)));
    }
}
