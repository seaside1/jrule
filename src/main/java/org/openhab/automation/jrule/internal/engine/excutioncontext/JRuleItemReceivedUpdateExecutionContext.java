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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.openhab.automation.jrule.internal.engine.JRuleInvocationCallback;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleItem;
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

    public JRuleItemReceivedUpdateExecutionContext(String uid, String logName, String[] loggingTags,
            JRuleInvocationCallback invocationCallback, String itemName, JRuleMemberOf memberOf,
            Optional<JRuleConditionContext> conditionContext, List<JRulePreconditionContext> preconditionContextList,
            Optional<String> state, Duration timedLock, Duration delayed) {
        super(uid, logName, loggingTags, invocationCallback, itemName, memberOf, conditionContext,
                preconditionContextList, timedLock, delayed);
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
            JRuleAdditionalItemCheckData itemCheckData = (JRuleAdditionalItemCheckData) checkData;
            switch (getMemberOf()) {
                case All:
                    return itemCheckData.getBelongingGroups().contains(this.getItemName());
                case Groups:
                    return itemCheckData.getBelongingGroups().contains(this.getItemName()) && itemCheckData.isGroup();
                case Items:
                    return itemCheckData.getBelongingGroups().contains(this.getItemName()) && !itemCheckData.isGroup();
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        final JRuleItem item;
        final JRuleItem memberItem;
        if (getMemberOf() != JRuleMemberOf.None) {
            memberItem = JRuleItem.forName(((ItemEvent) event).getItemName());
            item = JRuleItem.forName(((ItemEvent) event).getItemName());
        } else {
            memberItem = null;
            item = JRuleItem.forName(this.getItemName());
        }

        // updating the item state to be sure that it's update when the JRule method is fired
        // JRuleEventHandler.get().setValue(((ItemStateEvent) event).getItemName(),
        // ((ItemStateEvent) event).getItemState());

        return new JRuleItemEvent(item, memberItem,
                JRuleEventHandler.get().toValue(((ItemStateEvent) event).getItemState()), null, null, null);
    }

    @Override
    public String toString() {
        return "JRuleItemReceivedUpdateExecutionContext{" + "state=" + state + ", itemName='" + itemName + '\''
                + ", memberOf=" + memberOf + ", conditionContext=" + conditionContext + ", logName='" + logName + '\''
                + ", uid=" + uid + ", invocationCallback=" + invocationCallback + ", loggingTags="
                + Arrays.toString(loggingTags) + ", preconditionContextList=" + preconditionContextList + '}';
    }
}
