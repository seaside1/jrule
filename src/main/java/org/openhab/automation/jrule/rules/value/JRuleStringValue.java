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

import java.util.Objects;

import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;

/**
 * The {@link JRuleStringValue} JRule Command
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleStringValue extends JRuleValueBase implements JRuleValue {
    private final StringType ohType;

    public JRuleStringValue(String value) {
        this.ohType = new StringType(value);
    }

    @Override
    public State getOhType() {
        return ohType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JRuleStringValue that = (JRuleStringValue) o;
        return ohType.equals(that.ohType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ohType);
    }

    @Override
    public String toString() {
        return ohType.toString();
    }
}
