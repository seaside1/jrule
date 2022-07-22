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

import org.openhab.automation.jrule.internal.JRuleItemUtil;

/**
 * The {@link JRuleColorValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleColorValue {

    private final JRuleHsbValue hsbValue;
    private final JRuleXyValue xyValue;
    private final JRuleRgbValue rgbValue;

    public JRuleColorValue(JRuleHsbValue hsbValue, JRuleRgbValue rgbValue, JRuleXyValue xyValue) {
        this.hsbValue = hsbValue;
        this.xyValue = xyValue;
        this.rgbValue = rgbValue;
    }

    public static JRuleColorValue fromHsb(int hue, int saturation, int brightness) {
        return JRuleItemUtil.getColorValueHsb(hue, saturation, brightness);
    }

    public static JRuleColorValue fromRgb(int red, int green, int blue) {
        return JRuleItemUtil.getColorValueRgb(red, green, blue);
    }

    public static JRuleColorValue fromXy(float x, float y) {
        return JRuleItemUtil.getColorValueXy(x, y);
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
