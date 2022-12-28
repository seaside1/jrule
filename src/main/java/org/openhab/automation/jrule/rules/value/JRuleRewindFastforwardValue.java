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

import org.openhab.core.library.types.RewindFastforwardType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRuleRewindFastforwardValue} JRule Command
 *
 * @author Robert Delbrück - Initial contribution
 */
public enum JRuleRewindFastforwardValue implements JRuleValue {
    REWIND(RewindFastforwardType.REWIND),
    FASTFORWARD(RewindFastforwardType.FASTFORWARD);

    private final RewindFastforwardType ohType;

    JRuleRewindFastforwardValue(RewindFastforwardType ohType) {
        this.ohType = ohType;
    }

    public static JRuleRewindFastforwardValue getValueFromString(String value) {
        if (value.equals(REWIND.name())) {
            return REWIND;
        }
        if (value.equals(FASTFORWARD.name())) {
            return FASTFORWARD;
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
        return this.ohType;
    }
}
