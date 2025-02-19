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
import org.openhab.core.library.types.PointType;
import org.openhab.core.types.State;

/**
 * The {@link JRuleStringValue} JRule Command
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRulePointValue extends JRuleValueBase implements JRuleValue {
    private final PointType ohType;

    public JRulePointValue(BigDecimal latitude, BigDecimal longitude, BigDecimal altitude) {
        this.ohType = new PointType(new DecimalType(latitude), new DecimalType(longitude), new DecimalType(altitude));
    }

    public JRulePointValue(BigDecimal latitude, BigDecimal longitude) {
        this.ohType = new PointType(new DecimalType(latitude), new DecimalType(longitude));
    }

    public JRulePointValue(String value) {
        this.ohType = new PointType(value);
    }

    public JRulePointValue(double latitude, double longitude) {
        this.ohType = new PointType(new DecimalType(latitude), new DecimalType(longitude));
    }

    public JRulePointValue(double latitude, double longitude, double altitude) {
        this.ohType = new PointType(new DecimalType(latitude), new DecimalType(longitude), new DecimalType(altitude));
    }

    public BigDecimal getLatitude() {
        return this.ohType.getLatitude().toBigDecimal();
    }

    public BigDecimal getLongitude() {
        return this.ohType.getLongitude().toBigDecimal();
    }

    public BigDecimal getAltitude() {
        return this.ohType.getAltitude().toBigDecimal();
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
        JRulePointValue that = (JRulePointValue) o;
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
