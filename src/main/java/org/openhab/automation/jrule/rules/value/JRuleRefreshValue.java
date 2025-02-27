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

import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;

/**
 * The {@link JRuleRefreshValue} JRule Command
 *
 * @author Robert Delbrück - Initial contribution
 */
public enum JRuleRefreshValue implements JRuleValue {
    REFRESH(RefreshType.REFRESH);

    private final RefreshType ohType;

    JRuleRefreshValue(RefreshType ohType) {
        this.ohType = ohType;
    }

    public static JRuleRefreshValue getValueFromString(String value) {
        return switch (value) {
            case "REFRESH" -> REFRESH;
            default -> null;
        };
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
