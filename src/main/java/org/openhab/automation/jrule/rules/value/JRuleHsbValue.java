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
public class JRuleHsbValue {

    @Override
    public String toString() {
        return "JRuleHsbValue [hue=" + hue + ", saturation=" + saturation + ", brightness=" + brightness + "]";
    }

    private final int hue;
    private final int saturation;
    private final int brightness;

    public JRuleHsbValue(int hue, int saturation, int brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public int getHue() {
        return hue;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getSaturation() {
        return saturation;
    }
}
