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
import java.util.Arrays;
import java.util.List;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.core.events.AbstractEvent;

/**
 * The {@link JRuleExecutionContext}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleExecutionContext {
    private final String logName;
    protected final JRule jRule;
    protected final Method method;
    private final String[] loggingTags;
    private final List<JRulePreconditionContext> preconditionContextList;

    public JRuleExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            List<JRulePreconditionContext> preconditionContextList) {
        this.logName = logName;
        this.loggingTags = loggingTags;
        this.jRule = jRule;
        this.method = method;
        this.preconditionContextList = preconditionContextList;
    }

    public JRule getJrule() {
        return jRule;
    }

    public Method getMethod() {
        return method;
    }

    public boolean hasEventParameterPresent() {
        return Arrays.stream(method.getParameters())
                .anyMatch(param -> (JRuleEvent.class.isAssignableFrom(param.getType())));
    }

    public String getLogName() {
        return logName;
    }

    public String[] getLoggingTags() {
        return loggingTags;
    }

    public abstract boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData);

    public abstract JRuleEvent createJRuleEvent(AbstractEvent event);

    public List<JRulePreconditionContext> getPreconditionContextList() {
        return preconditionContextList;
    }

    public static class JRuleAdditionalCheckData {

    }
}
