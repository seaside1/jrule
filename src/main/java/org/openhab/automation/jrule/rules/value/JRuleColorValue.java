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
 * The {@link JRuleColorValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleColorValue implements JRuleValue {

    private final JRuleHsbValue hsbValue;
    private final JRuleXyValue xyValue;
    private final JRuleRgbValue rgbValue;

    public JRuleColorValue(JRuleHsbValue hsbValue, JRuleRgbValue rgbValue, JRuleXyValue xyValue) {
        this.hsbValue = hsbValue;
        this.xyValue = xyValue;
        this.rgbValue = rgbValue;
    }

    public JRuleColorValue(String value) {
        throw new IllegalStateException("not implemented");
    }

    public JRuleHsbValue getHsbValue() {
        return hsbValue;
    }

    public JRuleXyValue getXyValue() {
        return xyValue;
    }

    public JRuleRgbValue getRgbValue() {
        return rgbValue;
    }

    @Override
    public String toString() {
        return "JRuleColorValue [hsbValue=" + hsbValue + ", xyValue=" + xyValue + ", rgbValue=" + rgbValue + "]";
    }
}
