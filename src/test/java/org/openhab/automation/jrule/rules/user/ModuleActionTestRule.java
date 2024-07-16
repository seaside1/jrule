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
package org.openhab.automation.jrule.rules.user;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openhab.automation.jrule.rules.*;
import org.openhab.automation.jrule.rules.event.JRuleEvent;

/**
 * Module actions test
 *
 * @author Arne Seime - Initial contribution
 */
public class ModuleActionTestRule extends JRule {

    @JRuleName("Call module action")
    @JRuleWhenItemReceivedCommand(item = "ModuleActionCommandItem")
    public void callModuleAction(JRuleEvent event)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Generated file so must use reflection to get hold of the generated class
        Class<?> moduleActionClass = Class
                .forName("org.openhab.automation.jrule.generated.moduleactions.JRuleModuleActions");
        Method method = moduleActionClass.getMethod("coreItemCommandAction", String.class, String.class);
        method.invoke(null, "ModuleActionRecipientItem", "CommandToSend");
    }
}
