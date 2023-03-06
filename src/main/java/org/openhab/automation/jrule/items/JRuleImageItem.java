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
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.items.JRuleInternalImageItem;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;

/**
 * The {@link JRuleImageItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleImageItem extends JRuleItem {
    static JRuleImageItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalImageItem.class);
    }

    static Optional<JRuleImageItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    /**
     * Sends a on/off update
     * 
     * @param state update to send
     */
    default void postUpdate(JRuleRawValue state) {
        postUncheckedUpdate(state);
    }

    default JRuleRawValue getStateAsRaw() {
        return JRuleEventHandler.get().getValue(getName(), JRuleRawValue.class);
    }
}
