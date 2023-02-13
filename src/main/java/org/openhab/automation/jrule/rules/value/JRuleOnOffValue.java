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

import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRuleOnOffValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public enum JRuleOnOffValue implements JRuleValue {
    ON(OnOffType.ON),
    OFF(OnOffType.OFF);

    private final OnOffType ohType;

    JRuleOnOffValue(OnOffType ohType) {
        this.ohType = ohType;
    }

    public static JRuleOnOffValue getValueFromString(String value) {
        switch (value) {
            case "ON":
                return ON;
            case "OFF":
                return OFF;
        }
        return null;
    }

    public static JRuleValue valueOf(boolean command) {
        return command ? ON : OFF;
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

    @Override
    public String toString() {
        return ohType.toString();
    }
}
