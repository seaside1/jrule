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
public class JRuleXyValue {

    private final float x;
    private final float y;
    private final float yY;

    @Override
    public String toString() {
        return "JRuleXyValue [x=" + x + ", y=" + y + ", yY=" + yY + "]";
    }

    public JRuleXyValue(float x, float y, float yY) {
        this.x = x;
        this.y = y;
        this.yY = yY;
    }

    public float getyY() {
        return yY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
