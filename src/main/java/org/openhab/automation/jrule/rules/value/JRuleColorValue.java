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
package org.openhab.automation.jrule.rules.value;

/**
 * The {@link JRuleColorValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleColorValue {

    private JRuleHsbValue hsbValue;
    private JRuleXyValue xyValue;
    private JRuleRgbValue rgbValue;

    public JRuleColorValue(JRuleHsbValue hsbValue, JRuleRgbValue rgbValue, JRuleXyValue xyValue) {
        this.hsbValue = hsbValue;
        this.xyValue = xyValue;
        this.rgbValue = rgbValue;
    }

    private JRuleColorValue(JRuleHsbValue hsbValue) {
        this.hsbValue = hsbValue;
        xyValue = null;
        rgbValue = null;
    }

    private JRuleColorValue(JRuleRgbValue rgbValue) {
        this.rgbValue = rgbValue;
        xyValue = null;
        hsbValue = null;
    }

    private JRuleColorValue(JRuleXyValue xyValue) {
        this.xyValue = xyValue;
        rgbValue = null;
        hsbValue = null;
    }

    public static JRuleColorValue fromHsb(int hue, int saturation, int brightness) {
        return new JRuleColorValue(new JRuleHsbValue(hue, saturation, brightness));
    }

    public static JRuleColorValue fromRgb(int red, int green, int blue) {
        return new JRuleColorValue(new JRuleRgbValue(red, green, blue));
    }

    public static JRuleColorValue fromXy(float x, float y, float yY) {
        return new JRuleColorValue(new JRuleXyValue(x, y, yY));
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
