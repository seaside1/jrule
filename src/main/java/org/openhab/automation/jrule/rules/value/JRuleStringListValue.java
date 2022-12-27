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

import java.util.List;
import java.util.Objects;

import org.openhab.core.library.types.StringListType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRuleStringListValue} JRule Command
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleStringListValue implements JRuleValue {
    private final StringListType ohType;

    public JRuleStringListValue(String value) {
        this.ohType = new StringListType(value);
    }

    public JRuleStringListValue(List<String> value) {
        this.ohType = new StringListType(value);
    }

    public JRuleStringListValue(String... value) {
        this.ohType = new StringListType(value);
    }

    @Override
    public Command toOhCommand() {
        throw new IllegalStateException("not a command type");
    }

    @Override
    public State toOhState() {
        return this.ohType;
    }

    public String stringValue() {
        return this.ohType.toFullString();
    }

    public String getValue(int index) {
        return this.ohType.getValue(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleStringListValue that = (JRuleStringListValue) o;
        return ohType.equals(that.ohType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ohType);
    }
}
