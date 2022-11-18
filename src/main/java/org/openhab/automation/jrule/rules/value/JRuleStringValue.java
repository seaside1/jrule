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
package org.openhab.automation.jrule.rules.value;

import java.util.Objects;

/**
 * The {@link JRuleStringValue} JRule Command
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleStringValue implements JRuleValue {
    private final String value;

    public JRuleStringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static JRuleStringValue getValueFromString(String value) {
        return new JRuleStringValue(value);
    }

    @Override
    public String asStringValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleStringValue that = (JRuleStringValue) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public String stringValue() {
        return this.value;
    }
}
