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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The {@link JRuleStringValueTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleStringValueTest {

    @Test
    void asStringValue() {
        JRuleStringValue value = new JRuleStringValue("hallo");
        String string = value.stringValue();
        JRuleStringValue fromString = new JRuleStringValue(string);
        Assertions.assertEquals(value, fromString);
    }
}
