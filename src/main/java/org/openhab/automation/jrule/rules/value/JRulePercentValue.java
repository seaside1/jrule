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

import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.State;

/**
 * The {@link JRulePercentValue}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRulePercentValue extends JRuleValueBase implements JRuleValue {
    private final PercentType ohType;

    public JRulePercentValue(int value) {
        this.ohType = new PercentType(value);
    }

    public JRulePercentValue(double value) {
        this.ohType = new PercentType(new BigDecimal(value));
    }

    public JRulePercentValue(String value) {
        this.ohType = new PercentType(value);
    }

    public JRulePercentValue(boolean value) {
        this.ohType = new PercentType(value ? 100 : 0);
    }

    public double doubleValue() {
        return this.ohType.doubleValue();
    }

    public float floatValue() {
        return this.ohType.floatValue();
    }

    public int intValue() {
        return this.ohType.intValue();
    }

    public long longValue() {
        return this.ohType.longValue();
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
        JRulePercentValue that = (JRulePercentValue) o;
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
