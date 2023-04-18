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
package org.openhab.automation.jrule.internal.engine.excutioncontext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleTimerEvent;
import org.openhab.core.events.AbstractEvent;

/**
 * The {@link JRuleTimedCronExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleTimedCronExecutionContext extends JRuleTimedExecutionContext {
    private final String cron;

    public JRuleTimedCronExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            List<JRulePreconditionContext> preconditionContextList, String cron) {
        super(jRule, logName, loggingTags, method, preconditionContextList);
        this.cron = cron;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        return false;
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return new JRuleTimerEvent();
    }

    public String getCron() {
        return cron;
    }

    @Override
    public String toString() {
        return "JRuleTimedCronExecutionContext{" + "cron='" + cron + '\'' + ", logName='" + logName + '\'' + ", jRule="
                + rule + ", method=" + method + ", loggingTags=" + Arrays.toString(loggingTags)
                + ", preconditionContextList=" + preconditionContextList + '}';
    }
}
