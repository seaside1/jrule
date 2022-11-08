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

    public JRuleDecimalValue(String toFullString) {

    }

    public BigDecimal getValue() {
        return value;
    }
}
