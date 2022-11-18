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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Objects;

/**
 * The {@link JRuleDecimalValue}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleDecimalValue implements JRuleValue {
    private final BigDecimal value;

    public JRuleDecimalValue(BigDecimal value) {
        this.value = value;
    }

    public JRuleDecimalValue(String value) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        df.setParseBigDecimal(true);
        ParsePosition position = new ParsePosition(0);
        BigDecimal parsedValue = (BigDecimal) df.parseObject(value, position);
        if (parsedValue != null && position.getErrorIndex() == -1 && position.getIndex() >= value.length()) {
            this.value = parsedValue;
        } else {
            throw new NumberFormatException("Invalid BigDecimal value: " + value);
        }
    }

    public JRuleDecimalValue(double value) {
        this.value = new BigDecimal(value);
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String asStringValue() {
        return this.value.toPlainString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleDecimalValue that = (JRuleDecimalValue) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

    public float floatValue() {
        return this.value.floatValue();
    }

    public int intValue() {
        return this.value.intValue();
    }
}
