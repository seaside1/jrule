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
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleMemberOf;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.items.events.GroupItemStateChangedEvent;
import org.openhab.core.items.events.ItemEvent;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleItemChangeExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemChangeExecutionContext extends JRuleItemExecutionContext {
    private final Logger log = LoggerFactory.getLogger(JRuleItemChangeExecutionContext.class);
    private final Optional<String> from;
    private final Optional<String> to;
    private final Optional<JRuleConditionContext> previousConditionContext;

    public JRuleItemChangeExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            String itemName, JRuleMemberOf memberOf, Optional<JRuleConditionContext> conditionContext,
            Optional<JRuleConditionContext> previousConditionContext,
            List<JRulePreconditionContext> preconditionContextList, Optional<String> from, Optional<String> to,
            Duration timedLock) {
        super(jRule, logName, loggingTags, method, itemName, memberOf, conditionContext, preconditionContextList,
                timedLock);
        this.from = from;
        this.to = to;
        this.previousConditionContext = previousConditionContext;
    }

    public boolean matchCondition(String state, String previousState) {
        return super.matchCondition(state, previousState)
                && previousConditionContext.map(c -> c.matchCondition(previousState)).orElse(true);
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        if (!(event instanceof ItemStateChangedEvent
                && matchCondition(((ItemStateChangedEvent) event).getItemState().toString(),
                        ((ItemStateChangedEvent) event).getOldItemState().toString())
                && from.map(s -> ((ItemStateChangedEvent) event).getOldItemState().toString().equals(s)).orElse(true)
                && to.map(s -> ((ItemStateChangedEvent) event).getItemState().toString().equals(s)).orElse(true))) {
            return false;
        }
        if (getMemberOf() == JRuleMemberOf.None
                && ((ItemStateChangedEvent) event).getItemName().equals(this.getItemName())) {
            return true;
        }
        if (getMemberOf() != JRuleMemberOf.None && checkData instanceof JRuleAdditionalItemCheckData) {
            switch (getMemberOf()) {
                case All:
                    return true;
                case Groups:
                    return ((JRuleAdditionalItemCheckData) checkData).getBelongingGroups().contains(this.getItemName());
                case Items:
                    return !((JRuleAdditionalItemCheckData) checkData).getBelongingGroups()
                            .contains(this.getItemName());
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        final String itemName;
        final String memberName;
        if (getMemberOf() != JRuleMemberOf.None) {
            itemName = this.getItemName();
            memberName = ((ItemEvent) event).getItemName();
        } else {
            itemName = this.getItemName();
            memberName = event instanceof GroupItemStateChangedEvent
                    ? ((GroupItemStateChangedEvent) event).getMemberName()
                    : null;
        }

        return new JRuleItemEvent(itemName, memberName,
                JRuleEventHandler.get().toValue(((ItemStateChangedEvent) event).getItemState()),
                JRuleEventHandler.get().toValue(((ItemStateChangedEvent) event).getOldItemState()));
    }

    @Override
    public String toString() {
        return "JRuleItemChangeExecutionContext{" + "from=" + from + ", to=" + to + ", itemName='" + itemName + '\''
                + ", memberOf=" + memberOf + ", conditionContext=" + conditionContext + ", logName='" + logName + '\''
                + ", jRule=" + rule + ", method=" + method + ", loggingTags=" + Arrays.toString(loggingTags)
                + ", preconditionContextList=" + preconditionContextList + '}';
    }
}
