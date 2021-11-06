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
package org.openhab.automation.jrule.internal;

import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleRgbValue;
import org.openhab.automation.jrule.rules.value.JRuleXyValue;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleItemUtil} Utilities
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleItemUtil {

    private static final Logger logger = LoggerFactory.getLogger(JRuleItemUtil.class);

    public static JRuleColorValue getColorValueFromHsbType(HSBType hsbValue) {
        final JRuleHsbValue jRuleHsbValue = new JRuleHsbValue(hsbValue.getHue().intValue(),
                hsbValue.getSaturation().intValue(), hsbValue.getBrightness().intValue());
        final JRuleRgbValue jRuleRgbValue = new JRuleRgbValue(hsbValue.getRed().intValue(),
                hsbValue.getGreen().intValue(), hsbValue.getBlue().intValue());
        PercentType[] xyY = hsbValue.toXY();
        final JRuleXyValue jRuleXyValue = new JRuleXyValue(xyY[0].floatValue(), xyY[1].floatValue(),
                xyY[2].floatValue());
        return new JRuleColorValue(jRuleHsbValue, jRuleRgbValue, jRuleXyValue);
    }

    public static JRuleColorValue getColorValueFromState(State state) {
        HSBType hsbValue = null;
        try {
            hsbValue = HSBType.valueOf(state.toFullString());
        } catch (IllegalArgumentException x) {
            logger.error("Failed to parse state: {}", state.toFullString());
            return null;
        }
        return getColorValueFromHsbType(hsbValue);
    }

    public static JRuleColorValue getColorValueHsb(int hue, int saturation, int brightness) {
        return getColorValueFromHsbType(getHsbType(hue, saturation, brightness));
    }

    public static JRuleColorValue getColorValueXy(float x, float y) {
        return getColorValueFromHsbType(HSBType.fromXY(x, y));
    }

    public static JRuleColorValue getColorValueRgb(int red, int green, int blue) {
        return getColorValueFromHsbType(HSBType.fromRGB(red, green, blue));
    }

    public static HSBType getHsbType(int hue, int saturation, int brightness) {
        return new HSBType(new DecimalType(hue), new PercentType(saturation), new PercentType(brightness));
    }

    public static HSBType getHsbType(JRuleColorValue colorValue) {
        final JRuleHsbValue hsbValue = colorValue.getHsbValue();
        if (hsbValue != null) {
            return getHsbType(hsbValue.getHue(), hsbValue.getSaturation(), hsbValue.getBrightness());
        }

        final JRuleRgbValue rgbValue = colorValue.getRgbValue();
        if (rgbValue != null) {
            return HSBType.fromRGB(rgbValue.getRed(), rgbValue.getGreen(), rgbValue.getBlue());
        }

        final JRuleXyValue xyValue = colorValue.getXyValue();
        if (xyValue != null) {
            return HSBType.fromXY(xyValue.getX(), xyValue.getY());
        }
        return null;
    }
}
