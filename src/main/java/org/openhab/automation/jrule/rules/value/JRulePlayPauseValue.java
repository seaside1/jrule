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

import org.openhab.core.library.types.PlayPauseType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRulePlayPauseValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public enum JRulePlayPauseValue implements JRuleValue {
    PLAY(PlayPauseType.PLAY),
    PAUSE(PlayPauseType.PAUSE);

    private final PlayPauseType ohType;

    JRulePlayPauseValue(PlayPauseType ohType) {
        this.ohType = ohType;
    }

    public static JRulePlayPauseValue getValueFromString(String value) {
        if (value.equals(PLAY.name())) {
            return PLAY;
        }
        if (value.equals(PAUSE.name())) {
            return PAUSE;
        }
        return null;
    }

    public static JRuleValue valueOf(boolean command) {
        return command ? PLAY : PAUSE;
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
    public <T extends JRuleValue> T as(Class<T> target) {
        throw new IllegalStateException("cannot cast to '%s'".formatted(target));
    }

    @Override
    public String toString() {
        return ohType.toString();
    }
}
