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

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRulePrecondition;

/**
 * The {@link JRuleExecutionContext}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleExecutionContext {
    private final String logName;
    protected final String ruleName;
    protected final JRule jRule;
    protected final Method method;
    protected final boolean eventParameterPresent;
    private final String[] loggingTags;

    public JRulePrecondition[] getPreconditions() {
        return preconditions;
    }

    private final JRulePrecondition[] preconditions;

    public JRuleExecutionContext(JRule jRule, String logName, String[] loggingTags, String ruleName, Method method,
            boolean eventParameterPresent, JRulePrecondition[] preconditions) {
        this.logName = logName;
        this.loggingTags = loggingTags;
        this.jRule = jRule;
        this.ruleName = ruleName;
        this.method = method;
        this.eventParameterPresent = eventParameterPresent;
        this.preconditions = preconditions;
    }

    public String getRuleName() {
        return ruleName;
    }

    public JRule getJrule() {
        return jRule;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isEventParameterPresent() {
        return eventParameterPresent;
    }

    public String getLogName() {
        return logName;
    }

    public String[] getLoggingTags() {
        return loggingTags;
    }
}
