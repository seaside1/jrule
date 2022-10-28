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
import java.util.List;
import java.util.Optional;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.core.library.types.QuantityType;

/**
 * The {@link JRuleItemExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleItemExecutionContext extends JRuleExecutionContext {
    private final String itemName;
    private final boolean memberOf;
    private final Optional<Double> gt;
    private final Optional<Double> gte;
    private final Optional<Double> lt;
    private final Optional<Double> lte;
    protected final Optional<String> eq;
    protected final Optional<String> neq;

    public JRuleItemExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method, String itemName,
                                     boolean memberOf, Optional<Double> lt, Optional<Double> lte, Optional<Double> gt, Optional<Double> gte, Optional<String> eq,
                                     Optional<String> neq, List<JRulePreconditionContext> preconditionContextList) {
        super(jRule, logName, loggingTags, method, preconditionContextList);
        this.itemName = itemName;
        this.memberOf = memberOf;
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
        if (getEq().isPresent() && getEq().filter(state::equals).isEmpty()) {
            return false;
        }
        if (getNeq().isPresent() && getNeq().filter(ref -> !state.equals(ref)).isEmpty()) {
            return false;
        }
        if (getLt().isPresent() && getLt().filter(ref -> QuantityType.valueOf(state).doubleValue() < ref).isEmpty()) {
            return false;
        }
        if (getLte().isPresent()
                && getLte().filter(ref -> QuantityType.valueOf(state).doubleValue() <= ref).isEmpty()) {
            return false;
        }
        if (getGt().isPresent() && getGt().filter(ref -> QuantityType.valueOf(state).doubleValue() > ref).isEmpty()) {
            return false;
        }
        if (getGte().isPresent()
                && getGte().filter(ref -> QuantityType.valueOf(state).doubleValue() >= ref).isEmpty()) {
            return false;
        }
        return true;
    }

    public Optional<Double> getGt() {
        return gt;
    }

    public Optional<Double> getGte() {
        return gte;
    }

    public Optional<Double> getLt() {
        return lt;
    }

    public Optional<Double> getLte() {
        return lte;
    }

    public Optional<String> getEq() {
        return eq;
    }

    public Optional<String> getNeq() {
        return neq;
    }

    public boolean isMemberOf() {
        return memberOf;
    }

    public static class JRuleAdditionalItemCheckData extends JRuleAdditionalCheckData {
        private final List<String> belongingGroups;

        public JRuleAdditionalItemCheckData(List<String> belongingGroups) {
            this.belongingGroups = belongingGroups;
        }

        public List<String> getBelongingGroups() {
            return belongingGroups;
        }
    }
}
