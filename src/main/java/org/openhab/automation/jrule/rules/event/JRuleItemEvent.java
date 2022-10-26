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

import org.openhab.automation.jrule.rules.JRuleEventState;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;

/**
 * The {@link JRuleItemEvent}
 *
 * @author Robert Delbrück
 */
public class JRuleItemEvent extends JRuleEvent {
    private final String itemName;
    private final String memberName;
    private final JRuleEventState state;
    private final JRuleEventState oldState;

    public JRuleItemEvent(String itemName, String memberName, JRuleEventState state, JRuleEventState oldState) {
        this.itemName = itemName;
        this.memberName = memberName;
        this.state = state;
        this.oldState = oldState;
    }

    public JRuleEventState getState() {
        return state;
    }

    public JRuleEventState getOldState() {
        return oldState;
    }

    @Deprecated
    public String getValue() {
        return state.getValue();
    }

    public String getMemberName() {
        return memberName;
    }

    @Deprecated
    public JRuleOnOffValue getValueAsOnOffValue() {
        return state.getValueAsOnOffValue();
    }

    @Deprecated
    public JRuleOpenClosedValue getValueAsOpenClosedValue() {
        return state.getValueAsOpenClosedValue();
    }

    @Deprecated
    public JRuleUpDownValue getValueAsUpDownValue() {
        return state.getValueAsUpDownValue();
    }

    @Deprecated
    public Double getValueAsDouble() {
        return state.getValueAsDouble();
    }

    @Deprecated
    public Integer getValueAsInteger() {
        return state.getValueAsInteger();
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String toString() {
        return String.format("JRuleEvent [state=%s, oldState=%s, itemName=%s]", state, oldState, itemName);
    }
}
