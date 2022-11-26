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
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleMemberOf;
import org.openhab.core.library.types.QuantityType;

/**
 * The {@link JRuleItemExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleItemExecutionContext extends JRuleExecutionContext {
    protected final String itemName;
    protected final JRuleMemberOf memberOf;
    protected final Optional<Double> gt;
    protected final Optional<Double> gte;
    protected final Optional<Double> lt;
    protected final Optional<Double> lte;
    protected final Optional<String> eq;
    protected final Optional<String> neq;

    public JRuleItemExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method, String itemName,
            JRuleMemberOf memberOf, Optional<Double> lt, Optional<Double> lte, Optional<Double> gt,
            Optional<Double> gte, Optional<String> eq, Optional<String> neq,
            List<JRulePreconditionContext> preconditionContextList) {
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
        if (getLt().isPresent() && (!NumberUtils.isCreatable(state)
                || getLt().filter(ref -> QuantityType.valueOf(state).doubleValue() < ref).isEmpty())) {
            return false;
        }
        if (getLte().isPresent() && (!NumberUtils.isCreatable(state)
                || getLte().filter(ref -> QuantityType.valueOf(state).doubleValue() <= ref).isEmpty())) {
            return false;
        }
        if (getGt().isPresent() && (!NumberUtils.isCreatable(state)
                || getGt().filter(ref -> QuantityType.valueOf(state).doubleValue() > ref).isEmpty())) {
            return false;
        }
        if (getGte().isPresent() && (!NumberUtils.isCreatable(state)
                || getGte().filter(ref -> QuantityType.valueOf(state).doubleValue() >= ref).isEmpty())) {
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

    public JRuleMemberOf getMemberOf() {
        return memberOf;
    }

    public static class JRuleAdditionalItemCheckData extends JRuleAdditionalCheckData {
        /**
         * Itemname, IsGroup
         */
        private final Map<String, Boolean> belongingGroups;

        public JRuleAdditionalItemCheckData(Map<String, Boolean> belongingGroups) {
            this.belongingGroups = belongingGroups;
        }

        public Map<String, Boolean> getBelongingGroups() {
            return belongingGroups;
        }

        @Override
        public String toString() {
            return "JRuleAdditionalItemCheckData{" + "belongingGroups=" + belongingGroups + '}';
        }
    }
}
