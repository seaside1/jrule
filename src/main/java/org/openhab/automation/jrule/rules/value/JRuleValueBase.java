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

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;

/**
 * The {@link JRuleValueBase}. Base for all JRuleValues (sadly except enums)
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleValueBase implements JRuleValue {
    @Override
    public String stringValue() {
        return this.getOhType().toFullString();
    }

    @Override
    public Command toOhCommand() {
        Type ohType = this.getOhType();
        if (ohType instanceof Command) {
            return (Command) ohType;
        }
        throw new IllegalStateException("not a command type");
    }

    @Override
    public State toOhState() {
        Type ohType = this.getOhType();
        if (ohType instanceof State) {
            return (State) ohType;
        }
        throw new IllegalStateException("not a state type");
    }

    protected abstract Type getOhType();

    @Override
    public <T extends JRuleValue> T as(Class<T> target) {
        // noinspection unchecked
        return (T) JRuleEventHandler.get().toValue(toOhState().as(JRuleEventHandler.mapJRuleToOhType(target)));
    }
}
