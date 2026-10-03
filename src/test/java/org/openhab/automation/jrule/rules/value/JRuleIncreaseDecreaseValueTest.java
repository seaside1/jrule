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
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.core.library.types.IncreaseDecreaseType;

/**
 * The {@link JRuleIncreaseDecreaseValueTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleIncreaseDecreaseValueTest {

    @Test
    void asStringValue() {
        JRuleIncreaseDecreaseValue value = JRuleIncreaseDecreaseValue.INCREASE;
        String string = value.stringValue();
        JRuleIncreaseDecreaseValue fromString = JRuleIncreaseDecreaseValue.getValueFromString(string);
        Assertions.assertEquals(value, fromString);
    }

    /**
     * An incoming IncreaseDecreaseType has to reach the rule. Without a commandMapping entry the conversion
     * threw "cannot find mapping for oh type", which the event bus swallowed as a warning - a dimmer command
     * from a wall switch then triggered no rule at all.
     */
    @Test
    void fromOhCommand() {
        Assertions.assertEquals(JRuleIncreaseDecreaseValue.INCREASE,
                JRuleEventHandler.get().toValue(IncreaseDecreaseType.INCREASE));
        Assertions.assertEquals(JRuleIncreaseDecreaseValue.DECREASE,
                JRuleEventHandler.get().toValue(IncreaseDecreaseType.DECREASE));
    }
}
