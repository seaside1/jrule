/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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
import java.util.Objects;

import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.State;

/**
 * The {@link JRuleHsbValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleHsbValue extends JRuleValueBase implements JRuleValue {
    private final HSBType ohType;

    public JRuleHsbValue(String value) {
        this.ohType = new HSBType(value);
    }

    public JRuleHsbValue(float hue, int saturation, int brightness) {
        this.ohType = new HSBType(new DecimalType(hue), new PercentType(saturation), new PercentType(brightness));
    }

    public JRuleHsbValue(BigDecimal hue, BigDecimal saturation, BigDecimal brightness) {
        this.ohType = new HSBType(new DecimalType(hue), new PercentType(saturation), new PercentType(brightness));
    }

    public BigDecimal getHue() {
        return this.ohType.getHue().toBigDecimal();
    }

    public BigDecimal getBrightness() {
        return this.ohType.getBrightness().toBigDecimal();
    }

    public BigDecimal getSaturation() {
        return this.ohType.getSaturation().toBigDecimal();
    }

    @Override
    public State getOhType() {
        return ohType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleHsbValue that = (JRuleHsbValue) o;
        return ohType.equals(that.ohType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ohType);
    }

    @Override
    public String toString() {
        return ohType.toString();
    }
}
