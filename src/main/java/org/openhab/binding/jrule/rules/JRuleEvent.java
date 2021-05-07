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
package org.openhab.binding.jrule.rules;

/**
 * The {@link JRuleEvent}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleEvent {

    private final String value;

    public JRuleEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Double getValueAsDouble() {
        Double d = null;
        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException x) {
            // ignore
        }
        return d;
    }

    public Integer getValueAsInteger() {
        Integer i = null;
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException x) {
            // ignore
        }
        return i;
    }
}
