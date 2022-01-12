/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
import org.openhab.core.library.types.QuantityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEvent}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleEvent {

    private static final Logger logger = LoggerFactory.getLogger(JRuleEvent.class);

    private final String value;

    private String memberName;

    public JRuleEvent(String value) {
        this.value = value;
    }

    public JRuleEvent(String value, String memberName) {
        this.value = value;
        this.memberName = memberName;
    }

    public String getValue() {
        return value;
    }

    public JRuleOnOffValue getValueAsOnOffValue() {
        return JRuleOnOffValue.getValueFromString(value);
    }

    public JRuleOpenClosedValue getValueAsOpenClosedValue() {
        return JRuleOpenClosedValue.getValueFromString(value);
    }

    public JRuleUpDownValue getValueAsUpDownValue() {
        return JRuleUpDownValue.getValueFromString(value);
    }

    public Double getValueAsDouble() {
        try {
            return QuantityType.valueOf(value).doubleValue();
        } catch (Exception e) {
            logger.warn("Error converting {} to double: {}", value, e.getMessage());
            return null;
        }
    }

    public Integer getValueAsInteger() {
        try {
            return QuantityType.valueOf(value).intValue();
        } catch (Exception e) {
            logger.warn("Error converting {} to int: {}", value, e.getMessage());
            return null;
        }
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public String toString() {
        return "JRuleEvent{" + "value='" + value + '\'' + ", memberName='" + memberName + '\'' + '}';
    }
}
