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
package org.openhab.automation.jrule.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.openhab.core.model.script.actions.Things;
import org.openhab.core.thing.binding.ThingActions;

/**
 * The {@link JRuleAbstractAction}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleAbstractAction {
    private final ThingActions thingActions;
    private final String scope;
    private final String thingUID;

    protected JRuleAbstractAction(String scope, String thingUID) {
        this.scope = scope;
        this.thingUID = thingUID;
        thingActions = Objects.requireNonNull(Things.getActions(scope, thingUID),
                String.format("action for '%s' with uid '%s' could not be found", scope, thingUID));
    }

    protected Object invokeMethod(String methodName, Class<?>[] classes, Object... args) {
        try {
            Method method = thingActions.getClass().getDeclaredMethod(methodName, classes);
            return method.invoke(thingActions, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("method not found", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("error invoking method", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("cannot access method", e);
        }
    }

    public String getThingUID() {
        return thingUID;
    }

    public String getScope() {
        return scope;
    }
}
