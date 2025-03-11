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

import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * The {@link JRuleQuantityValueTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleQuantityValueTest {

    @Test
    void asStringValue() {
        JRuleQuantityValue value = new JRuleQuantityValue("23.12 kW");
        String string = value.stringValue();
        JRuleQuantityValue fromString = new JRuleQuantityValue(string);
        Assertions.assertEquals(value, fromString);
    }

    @Test
    void userMethods() {
        JRuleQuantityValue value = new JRuleQuantityValue("23.12 kW");
        Assertions.assertEquals(23.12, value.doubleValue());
        Assertions.assertEquals(23, value.intValue());
        Assertions.assertEquals("23.12", String.format(Locale.ENGLISH, "%.2f", value.floatValue()));
        Assertions.assertEquals(23, value.longValue());
        Assertions.assertEquals("kW", value.unit());
    }

    @TestFactory
    Stream<DynamicTest> as() {
        return Stream.of(
                DynamicTest.dynamicTest("JRuleDecimalValue",
                        () -> cast(new JRuleQuantityValue("23.12 kW"), JRuleDecimalValue.class,
                                t -> Assertions.assertEquals(23.12F, t.floatValue()))),
                DynamicTest.dynamicTest("JRulePercentValue", () -> cast(new JRuleQuantityValue("70 %"),
                        JRulePercentValue.class, t -> Assertions.assertEquals(70, t.intValue()))));
    }

    private <T extends JRuleValue> void cast(JRuleQuantityValue value, Class<T> target, Consumer<T> assertion) {
        assertion.accept((T) value.as(target));
    }
}
