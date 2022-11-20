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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;

import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRuleDateTimeValue}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleDateTimeValue implements JRuleValue {
    private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]]");
    private static final DateTimeFormatter PARSER_TZ = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]]z");
    private static final DateTimeFormatter PARSER_TZ_RFC = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]]Z");
    private static final DateTimeFormatter PARSER_TZ_ISO = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]]X");
    private static final DateTimeFormatter FORMATTER_TZ_RFC = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]Z");

    private ZonedDateTime value;

    public JRuleDateTimeValue(ZonedDateTime value) {
        this.value = value.toOffsetDateTime().toZonedDateTime();
    }

    public JRuleDateTimeValue(String fullString) {
        try {
            this.value = ZonedDateTime.parse(fullString, PARSER_TZ_RFC);
        } catch (DateTimeParseException var9) {
            try {
                this.value = ZonedDateTime.parse(fullString, PARSER_TZ_ISO);
            } catch (DateTimeParseException var8) {
                try {
                    this.value = ZonedDateTime.parse(fullString, PARSER_TZ);
                } catch (DateTimeParseException var7) {
                    LocalDateTime localDateTime = LocalDateTime.parse(fullString, PARSER);
                    this.value = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
                }
            }
        }
    }

    public JRuleDateTimeValue(Date date) {
        this.value = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public ZonedDateTime getValue() {
        return value;
    }

    @Override
    public String asStringValue() {
        return this.value.format(FORMATTER_TZ_RFC);
    }

    @Override
    public Command toOhCommand() {
        return new DateTimeType(this.value);
    }

    @Override
    public State toOhState() {
        return new DateTimeType(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleDateTimeValue that = (JRuleDateTimeValue) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
