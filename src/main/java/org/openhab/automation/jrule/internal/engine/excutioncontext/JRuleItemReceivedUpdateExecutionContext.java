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
import org.openhab.core.items.events.ItemEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleItemReceivedUpdateExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemReceivedUpdateExecutionContext extends JRuleItemExecutionContext {
    private final Logger log = LoggerFactory.getLogger(JRuleItemReceivedUpdateExecutionContext.class);
    private final Optional<String> state;

    public JRuleItemReceivedUpdateExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            String itemName, JRuleMemberOf memberOf, Optional<JRuleConditionContext> conditionContext,
            List<JRulePreconditionContext> preconditionContextList, Optional<String> state, Duration timedLock) {
        super(jRule, logName, loggingTags, method, itemName, memberOf, conditionContext, preconditionContextList,
                timedLock);
        this.state = state;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        if (!(event instanceof ItemStateEvent
                && matchCondition(((ItemStateEvent) event).getItemState().toString(), null)
                && state.map(s -> ((ItemStateEvent) event).getItemState().toString().equals(s)).orElse(true))) {
            return false;
        }
        if (getMemberOf() == JRuleMemberOf.None && ((ItemStateEvent) event).getItemName().equals(this.getItemName())) {
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
            memberName = ((ItemEvent) event).getItemName();
            itemName = ((ItemEvent) event).getItemName();
        } else {
            memberName = null;
            itemName = this.getItemName();
        }

        return new JRuleItemEvent(itemName, memberName,
                JRuleEventHandler.get().toValue(((ItemStateEvent) event).getItemState()), null);
    }

    @Override
    public String toString() {
        return "JRuleItemReceivedUpdateExecutionContext{" + "state=" + state + ", itemName='" + itemName + '\''
                + ", memberOf=" + memberOf + ", conditionContext=" + conditionContext + ", logName='" + logName + '\''
                + ", jRule=" + rule + ", method=" + method + ", loggingTags=" + Arrays.toString(loggingTags)
                + ", preconditionContextList=" + preconditionContextList + '}';
    }
}
