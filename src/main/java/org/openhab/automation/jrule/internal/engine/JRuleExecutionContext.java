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

/**
 * The {@link JRuleExecutionContext}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleExecutionContext {
    private static final String FROM_PREFIX = " from ";
    private static final String TO_PREFIX = " to ";
    private static final String SPACE = " ";
    private final String logName;
    private final String trigger;
    private final String ruleName;
    private final String itemClass;
    private final String itemName;
    private final String from;
    private final String to;
    private final Double gt;
    private final Double gte;
    private final Double lt;
    private final Double lte;
    private final Double eq;
    private final String update;
    private final JRule jRule;
    private final Method method;
    private final boolean eventParameterPresent;

    public JRuleExecutionContext(JRule jRule, String logName, String trigger, String from, String to, String update,
            String ruleName, String itemClass, String itemName, Method method, boolean eventParameterPresent, Double lt,
            Double lte, Double gt, Double gte, Double eq) {
        this.logName = logName;
        this.gt = gt;
        this.gte = gte;
        this.lt = lt;
        this.lte = lte;
        this.eq = eq;
        this.jRule = jRule;
        this.trigger = trigger;
        this.from = from;
        this.to = to;
        this.update = update;
        this.ruleName = ruleName;
        this.itemClass = itemClass;
        this.itemName = itemName;
        this.method = method;
        this.eventParameterPresent = eventParameterPresent;
    }

    public JRuleExecutionContext(JRule jRule, String logName, Method method, String ruleName,
            boolean jRuleEventPresent) {
        this(jRule, logName, null, null, null, null, ruleName, null, null, method, jRuleEventPresent, null, null, null,
                null, null);
    }

    @Override
    public String toString() {
        return "JRuleExecutionContext [trigger=" + trigger + ", ruleName=" + ruleName + ", itemClass=" + itemClass
                + ", itemName=" + itemName + ", from=" + from + ", to=" + to + ", gt=" + gt + ", gte=" + gte + ", lt="
                + lt + ", lte=" + lte + ", eq=" + eq + ", update=" + update + ", jRule=" + jRule + ", method=" + method
                + ", eventParameterPresent=" + eventParameterPresent + "]";
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
        if (update != null && !update.isEmpty()) {
            return buildUpdateString(trigger, update);
        }
        return trigger;
    }

    private String buildUpdateString(String trigger, String update) {
        final StringBuilder builder = new StringBuilder();
        builder.append(trigger);
        builder.append(SPACE);
        builder.append(update);
        return builder.toString();
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

    public String getItemClass() {
        return itemClass;
    }

    public String getItemName() {
        return itemName;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getUpdate() {
        return update;
    }

    public JRule getjRule() {
        return jRule;
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

    public Double getGt() {
        return gt;
    }

    public Double getGte() {
        return gte;
    }

    public Double getLt() {
        return lt;
    }

    public Double getLte() {
        return lte;
    }

    public Double getEq() {
        return eq;
    }

    public boolean isNumericOperation() {
        return lte != null || lt != null || gt != null || gte != null;
    }

    public String getLogName() {
        return logName;
    }
}
