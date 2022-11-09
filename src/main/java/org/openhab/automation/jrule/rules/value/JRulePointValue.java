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
import java.util.stream.Collectors;

/**
 * The {@link JRuleStringValue} JRule Command
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRulePointValue implements JRuleValue {
    private static final BigDecimal CIRCLE = new BigDecimal(360);
    private static final BigDecimal FLAT = new BigDecimal(180);
    private static final BigDecimal RIGHT = new BigDecimal(90);

    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final BigDecimal altitude;

    public JRulePointValue(BigDecimal latitude, BigDecimal longitude, BigDecimal altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public JRulePointValue(String value) {
        List<BigDecimal> elements = Arrays.stream(value.split(",")).map(String::trim).map(BigDecimal::new)
                .collect(Collectors.toList());
        this.latitude = elements.get(0);
        this.longitude = elements.get(1);
        if (elements.size() == 3) {
            this.altitude = elements.get(2);
        } else {
            this.altitude = null;
        }
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }
}
