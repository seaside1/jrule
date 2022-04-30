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
package org.openhab.automation.jrule.rules;

import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEvent}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleEvent {

    private static final Logger logger = LoggerFactory.getLogger(JRuleEvent.class);

    private final JRuleEventState state;
    private final JRuleEventState oldState;

    private String memberName;

    private String itemName;

    public JRuleEvent(String value) {
        this(value, null, null, null);
    }

    public JRuleEvent(String value, String oldValue, String itemName, String memberName) {
        this.itemName = itemName;
        this.state = new JRuleEventState(value);
        this.oldState = new JRuleEventState(oldValue);
        this.memberName = memberName;
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

    public String getMemberName() {
        return memberName;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String toString() {
        return String.format("JRuleEvent{itemName='%s', state=%s, oldState=%s, memberName='%s'", itemName, state,
                oldState, memberName);
    }
}
