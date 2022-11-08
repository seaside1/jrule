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
public class JRuleRgbValue implements JRuleValue {

    @Override
    public String toString() {
        return "JRuleRgbValue [red=" + red + ", green=" + green + ", blue=" + blue + "]";
    }

    private final int red;
    private final int green;
    private final int blue;

    public JRuleRgbValue(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
