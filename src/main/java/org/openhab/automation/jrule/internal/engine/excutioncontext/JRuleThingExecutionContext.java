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
 * The {@link JRuleThingExecutionContext} - execution context for thing triggers
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleThingExecutionContext extends JRuleExecutionContext {
    public static final String ANY_THING_UID = "*"; // Wildcard, allows registering rules that listen for all thing
    private static final String FROM_PREFIX = " from ";
    private static final String TO_PREFIX = " to ";

    private final String thing;
    private final String trigger;
    private final String from;
    private final String to;

    public JRuleThingExecutionContext(JRule jRule, String logName, String[] loggingTags, String trigger, String from,
            String to, String ruleName, String thing, Method method, boolean eventParameterPresent,
            JRulePrecondition[] preconditions) {
        super(jRule, logName, loggingTags, ruleName, method, eventParameterPresent, preconditions);
        this.thing = thing;
        this.trigger = trigger;
        this.from = from;
        this.to = to;
    }

    public String getThing() {
        return thing;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getTriggerFullString() {
        if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
            return buildFromToString(trigger, from, to);
        }
        if (from != null && !from.isEmpty()) {
            return buildFromToString(trigger, from, null);
        }
        if (to != null && !to.isEmpty()) {
            return buildFromToString(trigger, null, to);
        }

        return trigger;
    }

    private String buildFromToString(String trigger, String from, String to) {
        final StringBuilder builder = new StringBuilder();
        builder.append(trigger);
        if (from != null) {
            builder.append(FROM_PREFIX);
            builder.append(from);
        }
        if (to != null) {
            builder.append(TO_PREFIX);
            builder.append(to);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "JRuleThingExecutionContext{" + "thing='" + thing + '\'' + ", trigger='" + trigger + '\'' + ", from='"
                + from + '\'' + ", to='" + to + '\'' + '}';
    }
}
