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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.openhab.automation.jrule.internal.engine.JRuleInvocationCallback;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleTimerEvent;
import org.openhab.core.events.AbstractEvent;

/**
 * The {@link JRuleTimeTimerExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleTimeTimerExecutionContext extends JRuleTimedExecutionContext {
    private final Optional<Integer> hour;
    private final Optional<Integer> minute;
    private final Optional<Integer> second;

    public JRuleTimeTimerExecutionContext(String uid, String logName, String[] loggingTags,
            JRuleInvocationCallback invocationCallback, List<JRulePreconditionContext> preconditionContextList,
            Optional<Integer> hour, Optional<Integer> minute, Optional<Integer> second) {
        super(uid, logName, loggingTags, invocationCallback, preconditionContextList);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        return false;
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return new JRuleTimerEvent();
    }

    public Optional<Integer> getHour() {
        return hour;
    }

    public Optional<Integer> getMinute() {
        return minute;
    }

    public Optional<Integer> getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "JRuleTimeTimerExecutionContext{" + "hour=" + hour + ", minute=" + minute + ", second=" + second
                + ", logName='" + logName + '\'' + ", uid=" + uid + ", invocationCallback=" + invocationCallback
                + ", loggingTags=" + Arrays.toString(loggingTags) + ", preconditionContextList="
                + preconditionContextList + '}';
    }
}
