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

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openhab.automation.jrule.internal.engine.JRuleInvocationCallback;
import org.openhab.automation.jrule.rules.JRuleCondition;
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
    protected final Optional<JRuleConditionContext> conditionContext;

    public JRuleItemExecutionContext(String uid, String logName, String[] loggingTags,
            JRuleInvocationCallback invocationCallback, String itemName, JRuleMemberOf memberOf,
            Optional<JRuleConditionContext> conditionContext, List<JRulePreconditionContext> preconditionContextList,
            Duration timedLock, Duration delayed) {
        super(uid, logName, loggingTags, invocationCallback, preconditionContextList, timedLock, delayed);
        this.itemName = itemName;
        this.memberOf = memberOf;
        this.conditionContext = conditionContext;
    }

    public String getItemName() {
        return itemName;
    }

    public boolean matchCondition(String state, String previousState) {
        return conditionContext.map(c -> c.matchCondition(state)).orElse(true);
    }

    public JRuleMemberOf getMemberOf() {
        return memberOf;
    }

    public static class JRuleAdditionalItemCheckData extends JRuleAdditionalCheckData {
        private final boolean group;
        private final List<String> belongingGroups;

        public JRuleAdditionalItemCheckData(boolean group, List<String> belongingGroups) {
            this.group = group;
            this.belongingGroups = belongingGroups;
        }

        public List<String> getBelongingGroups() {
            return belongingGroups;
        }

        public boolean isGroup() {
            return group;
        }

        @Override
        public String toString() {
            return "JRuleAdditionalItemCheckData{" + "group=" + group + ", belongingGroups=" + belongingGroups + '}';
        }
    }

    public static class JRuleConditionContext {
        protected final Optional<Double> gt;
        protected final Optional<Double> gte;
        protected final Optional<Double> lt;
        protected final Optional<Double> lte;
        protected final Optional<String> eq;
        protected final Optional<String> neq;

        public JRuleConditionContext(Optional<Double> gt, Optional<Double> gte, Optional<Double> lt,
                Optional<Double> lte, Optional<String> eq, Optional<String> neq) {
            this.gt = gt;
            this.gte = gte;
            this.lt = lt;
            this.lte = lte;
            this.eq = eq;
            this.neq = neq;
        }

        public JRuleConditionContext(JRuleCondition jRuleCondition) {
            this.lt = Optional.of(jRuleCondition.lt()).filter(aDouble -> aDouble != Double.MIN_VALUE);
            this.lte = Optional.of(jRuleCondition.lte()).filter(aDouble -> aDouble != Double.MIN_VALUE);
            this.gt = Optional.of(jRuleCondition.gt()).filter(aDouble -> aDouble != Double.MIN_VALUE);
            this.gte = Optional.of(jRuleCondition.gte()).filter(aDouble -> aDouble != Double.MIN_VALUE);
            this.eq = Optional.of(jRuleCondition.eq()).filter(StringUtils::isNotEmpty);
            this.neq = Optional.of(jRuleCondition.neq()).filter(StringUtils::isNotEmpty);
        }

        public boolean matchCondition(String state) {
            if (eq.isPresent() && eq.filter(state::equals).isEmpty()) {
                return false;
            }
            if (neq.isPresent() && neq.filter(ref -> !state.equals(ref)).isEmpty()) {
                return false;
            }
            if (lt.isPresent() && lt.filter(ref -> getNumber(state).filter(a -> a < ref).isPresent()).isEmpty()) {
                return false;
            }
            if (lte.isPresent() && lte.filter(ref -> getNumber(state).filter(a -> a <= ref).isPresent()).isEmpty()) {
                return false;
            }
            if (gt.isPresent() && gt.filter(ref -> getNumber(state).filter(a -> a > ref).isPresent()).isEmpty()) {
                return false;
            }
            if (gte.isPresent() && gte.filter(ref -> getNumber(state).filter(a -> a >= ref).isPresent()).isEmpty()) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JRuleConditionContext{" + "gt=" + gt + ", gte=" + gte + ", lt=" + lt + ", lte=" + lte + ", eq=" + eq
                    + ", neq=" + neq + '}';
        }
    }

    private static Optional<Double> getNumber(String state) {
        try {
            return Optional.of(QuantityType.valueOf(state).doubleValue());
        } catch (IllegalArgumentException e) {
            if (NumberUtils.isParsable(state)) {
                return Optional.of(Double.parseDouble(state));
            } else {
                return Optional.empty();
            }
        }
    }
}
