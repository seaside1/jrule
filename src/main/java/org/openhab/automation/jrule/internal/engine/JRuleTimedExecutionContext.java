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
package org.openhab.automation.jrule.internal.engine;

import java.lang.reflect.Method;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRulePrecondition;

/**
 * The {@link JRuleTimedExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleTimedExecutionContext extends JRuleExecutionContext {
    public JRuleTimedExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method, String ruleName,
            boolean jRuleEventPresent, JRulePrecondition[] preconditions) {
        super(jRule, logName, loggingTags, ruleName, method, jRuleEventPresent, preconditions);
    }
}
