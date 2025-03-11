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

import java.util.Objects;

import org.openhab.core.library.types.QuantityType;
import org.openhab.core.types.State;

import tech.units.indriya.format.SimpleUnitFormat;

/**
 * The {@link JRuleQuantityValue}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleQuantityValue extends JRuleValueBase implements JRuleValue {
    private final QuantityType<?> ohType;

    public JRuleQuantityValue(String value) {
        this.ohType = new QuantityType<>(value);
    }

    public JRuleQuantityValue(Number value, String unit) {
        this.ohType = new QuantityType<>(value, SimpleUnitFormat.getInstance().parse(unit));
    }

    @Override
    public State getOhType() {
        return ohType;
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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleQuantityValue that = (JRuleQuantityValue) o;
        return ohType.equals(that.ohType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ohType);
    }

    public String unit() {
        return this.ohType.getUnit().toString();
    }

    @Override
    public String toString() {
        return ohType.toString();
    }
}
