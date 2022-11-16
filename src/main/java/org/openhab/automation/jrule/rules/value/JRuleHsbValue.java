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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The {@link JRuleHsbValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleHsbValue implements JRuleValue {
    private final BigDecimal hue;
    private final BigDecimal saturation;
    private final BigDecimal brightness;

    public JRuleHsbValue(String value) {
        List<String> constituents = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList());
        if (constituents.size() == 3) {
            this.hue = new BigDecimal(constituents.get(0));
            this.saturation = new BigDecimal(constituents.get(1));
            this.brightness = new BigDecimal(constituents.get(2));
        } else {
            throw new IllegalArgumentException(value + " is not a valid HSBType syntax");
        }
    }

    public JRuleHsbValue(float hue, float saturation, float brightness) {
        this.hue = new BigDecimal(hue);
        this.saturation = new BigDecimal(saturation);
        this.brightness = new BigDecimal(brightness);
    }

    public JRuleHsbValue(BigDecimal hue, BigDecimal saturation, BigDecimal brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    @Override
    public String toString() {
        return "JRuleHsbValue [hue=" + hue + ", saturation=" + saturation + ", brightness=" + brightness + "]";
    }

    @Override
    public String asStringValue() {
        return String.format("%s,%s,%s", this.hue.intValueExact(), this.saturation.intValueExact(),
                this.brightness.intValueExact());
    }

    public BigDecimal getHue() {
        return hue;
    }

    public BigDecimal getBrightness() {
        return brightness;
    }

    public BigDecimal getSaturation() {
        return saturation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleHsbValue that = (JRuleHsbValue) o;
        return hue.equals(that.hue) && saturation.equals(that.saturation) && brightness.equals(that.brightness);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hue, saturation, brightness);
    }
}
