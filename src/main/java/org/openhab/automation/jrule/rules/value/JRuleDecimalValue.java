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

    public JRuleDecimalValue(int value) {
        this.value = new BigDecimal(value);
    }

    public BigDecimal getValue() {
        return value;
    }
}
