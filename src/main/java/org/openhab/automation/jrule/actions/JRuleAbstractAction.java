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
package org.openhab.automation.jrule.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openhab.core.model.script.scoping.ActionClassLoader;

/**
 * The {@link JRuleAbstractAction}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleAbstractAction {
    protected Object invokeMethod(String clazzName, String methodName, Class<?>[] classes, Object... args) {
        try {
            ActionClassLoader cl = new ActionClassLoader(JRuleAbstractAction.class.getClassLoader());
            Class<?> clazz = Class.forName(clazzName, true, cl);
            Method method = clazz.getDeclaredMethod(methodName, classes);
            return method.invoke(null, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("method not found", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("error invoking method", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("cannot access method", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("cannot found class", e);
        }
    }
}
