/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.automation.jrule.rules;

/**
 * The {@link JRuleOnOffValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public enum JRuleOnOffValue {
    ON,
    OFF,
    UNDEF;

    public static JRuleOnOffValue getValueFromString(String value) {
        if (value.equals("ON")) {
            return ON;
        }
        if (value.equals("OFF")) {
            return OFF;
        }
        return UNDEF;
    }
}
