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

/**
 * The {@link JRuleOpenClosedValue} JRule Command
 *
 * @author Timo Litzius - Initial contribution
 */
public enum JRuleOpenClosedValue {
    OPEN,
    CLOSED,
    UNDEF;

    public static JRuleOpenClosedValue getValueFromString(String value) {
        if (value.equals("OPEN")) {
            return OPEN;
        }
        if (value.equals("CLOSED")) {
            return CLOSED;
        }
        return UNDEF;
    }
}
