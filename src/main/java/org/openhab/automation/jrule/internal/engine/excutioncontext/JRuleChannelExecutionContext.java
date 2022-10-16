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
 * The {@link JRuleChannelExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleChannelExecutionContext extends JRuleExecutionContext {
    private final String channel;
    private final String event;

    public JRuleChannelExecutionContext(JRule jRule, String logName, String[] loggingTags, String ruleName,
            Method method, boolean eventParameterPresent, JRulePrecondition[] preconditions, String channel,
            String event) {
        super(jRule, logName, loggingTags, ruleName, method, eventParameterPresent, preconditions);
        this.channel = channel;
        this.event = event;
    }

    public String getChannel() {
        return channel;
    }

    public String getEvent() {
        return this.event;
    }
}
