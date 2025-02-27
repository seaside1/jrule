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

import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRuleIncreaseDecreaseValue} JRule Command
 *
 * @author Timo Litzius - Initial contribution
 */
public enum JRuleIncreaseDecreaseValue implements JRuleValue {
    INCREASE(IncreaseDecreaseType.INCREASE),
    DECREASE(IncreaseDecreaseType.DECREASE);

    private final IncreaseDecreaseType ohType;

    JRuleIncreaseDecreaseValue(IncreaseDecreaseType ohType) {
        this.ohType = ohType;
    }

    public static JRuleIncreaseDecreaseValue getValueFromString(String value) {
        if (value.equals("INCREASE")) {
            return INCREASE;
        }
        if (value.equals("DECREASE")) {
            return DECREASE;
        }
        return null;
    }

    @Override
    public String stringValue() {
        return name();
    }

    @Override
    public Command toOhCommand() {
        return this.ohType;
    }

    @Override
    public State toOhState() {
        throw new IllegalStateException("not a state type");
    }

    @Override
    public <T extends JRuleValue> T as(Class<T> target) {
        throw new IllegalStateException("cannot cast to '%s'".formatted(target));
    }

    @Override
    public String toString() {
        return ohType.toString();
    }
}
