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

/**
 * The {@link JRulePercentValue}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRulePercentValue implements JRuleValue {
    private final int value;

    public JRulePercentValue(int value) {
        this.value = value;
    }

    public JRulePercentValue(double value) {
        this.value = (int) Math.round(value + 0.5);
    }

    public JRulePercentValue(String toFullString) {

    }

    public int getValue() {
        return value;
    }
}
