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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The {@link JRulePointValueTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRulePointValueTest {

    @Test
    void asStringValue() {
        JRulePointValue value = new JRulePointValue(new BigDecimal("23.12"), new BigDecimal("37.56"));
        String string = value.stringValue();
        JRulePointValue fromString = new JRulePointValue(string);
        Assertions.assertEquals(value, fromString);
    }
}
