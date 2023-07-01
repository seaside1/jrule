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
package org.openhab.automation.jrule.internal.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openhab.core.model.script.actions.Things;
import org.openhab.core.thing.binding.ThingActions;

/**
 *
 * The {@link JRuleAddonActionHandler} is the JRule wrapper class for the telegram action
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleAddonActionHandler {
    private ThingActions thingActions;

    public JRuleAddonActionHandler(ThingActions thingActions) {
        this.thingActions = thingActions;
    }

    public static JRuleAddonActionHandler get(String scope, String thingUid) {
        ThingActions actions = Objects.requireNonNull(Things.getActions(scope, thingUid),
                String.format("action for '%s' with uid '%s' could not be found", scope, thingUid));
        return new JRuleAddonActionHandler(actions);
    }

    public Object doAction(String methodName, Object... params) {
        try {
            Class<?>[] paramClasses = Arrays.stream(params).map(Object::getClass).toArray(Class[]::new);
            Method method = thingActions.getClass().getDeclaredMethod(methodName, paramClasses);
            return method.invoke(thingActions, params);
        } catch (NoSuchMethodException e) {
            String availMethods = Arrays.stream(thingActions.getClass().getDeclaredMethods())
                    .map(JRuleAddonActionHandler::getMethodDescription).collect(Collectors.joining("\n"));
            throw new RuntimeException(String.format("method not found, available: %s", availMethods));
        } catch (InvocationTargetException e) {
            throw new RuntimeException("error invoking method", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("cannot access method", e);
        }
    }

    private static String getMethodDescription(Method method) {
        return method.getName() + "("
                + Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(", ")) + ")";
    }
}
