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

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;

/**
 * The {@link JRuleContactItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleContactItem extends JRuleItem {
    String OPEN = JRuleOpenClosedValue.OPEN.stringValue();
    String CLOSED = JRuleOpenClosedValue.CLOSED.stringValue();

    static JRuleContactItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleContactItem.class);
    }

    /**
     * Sends a open/close update
     * 
     * @param state update to send
     */
    default void postUpdate(JRuleOpenClosedValue state) {
        postUncheckedUpdate(state);
    }

    default JRuleOpenClosedValue getStateAsOpenClose() {
        return JRuleEventHandler.get().getValue(getName(), JRuleOpenClosedValue.class);
    }
}
