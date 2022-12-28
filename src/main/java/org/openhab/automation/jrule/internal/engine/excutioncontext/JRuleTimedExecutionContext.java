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
package org.openhab.automation.jrule.internal.engine.excutioncontext;

import java.lang.reflect.Method;
import java.util.List;

import org.openhab.automation.jrule.rules.JRule;

/**
 * The {@link JRuleTimedExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleTimedExecutionContext extends JRuleExecutionContext {
    public JRuleTimedExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            List<JRulePreconditionContext> preconditionContextList) {
        super(jRule, logName, loggingTags, method, preconditionContextList, null);
    }
}
