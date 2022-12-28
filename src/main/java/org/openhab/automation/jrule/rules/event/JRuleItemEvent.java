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
package org.openhab.automation.jrule.rules.event;

import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleItemEvent}
 *
 * @author Robert Delbrück
 */
public class JRuleItemEvent extends JRuleEvent {
    private final String itemName;
    private final String memberName;
    private final JRuleValue state;
    private final JRuleValue oldState;

    public JRuleItemEvent(String itemName, String memberName, JRuleValue state, JRuleValue oldState) {
        this.itemName = itemName;
        this.memberName = memberName;
        this.state = state;
        this.oldState = oldState;
    }

    public JRuleValue getState() {
        return state;
    }

    public JRuleValue getOldState() {
        return oldState;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String toString() {
        return String.format("JRuleEvent [state=%s, oldState=%s, itemName=%s]", state, oldState, itemName);
    }
}
