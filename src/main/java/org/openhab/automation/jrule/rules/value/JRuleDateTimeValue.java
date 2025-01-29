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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.types.State;

/**
 * The {@link JRuleDateTimeValue}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleDateTimeValue extends JRuleValueBase implements JRuleValue {
    private final DateTimeType ohType;

    public JRuleDateTimeValue(ZonedDateTime value) {
        this.ohType = new DateTimeType(value);
    }

    public JRuleDateTimeValue(String fullString) {
        this.ohType = new DateTimeType(fullString);
    }

    public JRuleDateTimeValue(Date date) {
        this.ohType = new DateTimeType(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public ZonedDateTime getValue() {
        return ohType.getZonedDateTime();
    }

    @Override
    protected State getOhType() {
        return ohType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleDateTimeValue that = (JRuleDateTimeValue) o;
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
