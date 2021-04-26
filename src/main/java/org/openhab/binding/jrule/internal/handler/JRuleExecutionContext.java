/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.jrule.internal.handler;

import java.lang.reflect.Method;

import org.openhab.binding.jrule.rules.JRule;

/**
 * The {@link JRuleExecutionContext}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleExecutionContext {
    private String trigger;
    private String ruleName;
    private String itemClass;
    private String itemName;
    private String from;
    private String to;
    private String update;
    private JRule jRule;
    private Method method;

    public JRuleExecutionContext(JRule jRule, String trigger, String from, String to, String update, String ruleName,
            String itemClass, String itemName, Method method) {
        this.jRule = jRule;
        this.trigger = trigger;
        this.from = from;
        this.to = to;
        this.update = update;
        this.ruleName = ruleName;
        this.itemClass = itemClass;
        this.itemName = itemName;
        this.method = method;
    }

    @Override
    public String toString() {
        return "JRuleExecutionContext [trigger=" + trigger + ", ruleName=" + ruleName + ", itemClass=" + itemClass
                + ", itemName=" + itemName + ", from=" + from + ", to=" + to + ", jRule=" + jRule + ", method=" + method
                + "]";
    }

    public String getTrigger() {
        return trigger;
    }

    public String getTriggerFullString() {
        if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
            return trigger + " from " + from + " to " + to;
        }
        if (from != null && !from.isEmpty()) {
            return trigger + " from " + from;
        }
        if (to != null && !to.isEmpty()) {
            return trigger + " to " + to;
        }
        if (update != null && !update.isEmpty()) {
            return trigger + " " + update;
        }
        return trigger;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public JRule getjRule() {
        return jRule;
    }

    public void setjRule(JRule jRule) {
        this.jRule = jRule;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public void setMethod(Method method) {
        this.method = method;
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
}
