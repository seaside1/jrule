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
package org.openhab.automation.jrule.things;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link JRuleThingRegistry} Local registry of JRuleThings
 *
 * @author Arne Seime - Initial contribution
 */

public class JRuleThingRegistry {
    private static final Map<String, JRuleAbstractThing> thingRegistry = new HashMap<>();

    public static void clear() {
        thingRegistry.clear();
    }

    public static <T> T get(String thingName, Class<T> jRuleThingClass) {
        JRuleAbstractThing jRuleThing = thingRegistry.get(thingName);
        if (jRuleThing == null) {
            try {
                Constructor<T> constructor = jRuleThingClass.getDeclaredConstructor(String.class);
                constructor.setAccessible(true);
                jRuleThing = (JRuleAbstractThing) constructor.newInstance(thingName);
                thingRegistry.put(thingName, jRuleThing);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return (T) jRuleThing;
    }
}
