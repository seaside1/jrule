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
package org.openhab.automation.jrule.internal.engine.context;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.openhab.automation.jrule.rules.JRule;

/**
 * The {@link JRuleItemExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleItemExecutionContext extends JRuleExecutionContext {
    private final String itemName;
    private final Optional<Double> gt;
    private final Optional<Double> gte;
    private final Optional<Double> lt;
    private final Optional<Double> lte;
    protected final Optional<String> eq;
    protected final Optional<String> neq;

    public JRuleItemExecutionContext(JRule jRule, String logName, String[] loggingTags, String itemName, Method method,
            Optional<Double> lt, Optional<Double> lte, Optional<Double> gt, Optional<Double> gte, Optional<String> eq,
            Optional<String> neq, List<JRulePreconditionContext> preconditionContextList) {
        super(jRule, logName, loggingTags, method, preconditionContextList);
        this.itemName = itemName;
        this.gt = gt;
        this.gte = gte;
        this.lt = lt;
        this.lte = lte;
        this.eq = eq;
        this.neq = neq;
    }

    public String getItemName() {
        return itemName;
    }

    public boolean matchCondition(String state) {
        if (eq.isPresent() && eq.filter(state::equals).isEmpty()) {
            return false;
        }
        if (neq.isPresent() && neq.filter(ref -> !state.equals(ref)).isEmpty()) {
            return false;
        }
        if (lt.isPresent() && lt.filter(ref -> ref < Double.parseDouble(state)).isEmpty()) {
            return false;
        }
        if (lte.isPresent() && lte.filter(ref -> ref <= Double.parseDouble(state)).isEmpty()) {
            return false;
        }
        if (gt.isPresent() && gt.filter(ref -> ref > Double.parseDouble(state)).isEmpty()) {
            return false;
        }
        if (gte.isPresent() && gte.filter(ref -> ref >= Double.parseDouble(state)).isEmpty()) {
            return false;
        }
        return true;
    }
}
