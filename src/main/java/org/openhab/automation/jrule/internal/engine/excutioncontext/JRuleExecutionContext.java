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
import java.time.Duration;
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
    protected final String logName;
    protected final JRule rule;
    protected final Method method;
    protected final String[] loggingTags;
    protected final List<JRulePreconditionContext> preconditionContextList;
    protected final Duration timedLock;
    private final Duration delayed;

    // If this rule is enabled or disabled in openHAB rules engine
    private boolean enabled = false;

    public JRuleExecutionContext(JRule rule, String logName, String[] loggingTags, Method method,
            List<JRulePreconditionContext> preconditionContextList, Duration timedLock, Duration delayed) {
        this.logName = logName;
        this.loggingTags = loggingTags;
        this.rule = rule;
        this.method = method;
        this.preconditionContextList = preconditionContextList;
        this.timedLock = timedLock;
        this.delayed = delayed;
    }

    public JRule getRule() {
        return rule;
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

    @Override
    public String toString() {
        return "JRuleExecutionContext{" + "logName='" + logName + '\'' + ", jRule=" + rule + ", method=" + method
                + ", loggingTags=" + Arrays.toString(loggingTags) + ", preconditionContextList="
                + preconditionContextList + '}';
    }

    public Duration getTimedLock() {
        return timedLock;
    }

    public Duration getDelayed() {
        return delayed;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class JRuleAdditionalCheckData {

    }
}
