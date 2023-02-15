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
import org.openhab.automation.jrule.internal.items.JRuleInternalPlayerGroupItem;
import org.openhab.automation.jrule.rules.value.*;

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

    default void sendCommand(JRulePlayPauseValue command) {
        memberItems().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void postUpdate(JRulePlayPauseValue state) {
        memberItems().forEach(i -> i.postUncheckedUpdate(state));
    }

    default void sendCommand(JRuleRewindFastforwardValue command) {
        memberItems().forEach(i -> i.sendUncheckedCommand(command));
    }

    default void sendCommand(JRuleNextPreviousValue command) {
        memberItems().forEach(i -> i.sendUncheckedCommand(command));
    }
}
