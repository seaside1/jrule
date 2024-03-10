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

    public JRuleItemChangeExecutionContext(String uid, String logName, String[] loggingTags,
            JRuleInvocationCallback invocationCallback, String itemName, JRuleMemberOf memberOf,
            Optional<JRuleConditionContext> conditionContext, Optional<JRuleConditionContext> previousConditionContext,
            List<JRulePreconditionContext> preconditionContextList, Optional<String> from, Optional<String> to,
            Duration timedLock, Duration delayed) {
        super(uid, logName, loggingTags, invocationCallback, itemName, memberOf, conditionContext,
                preconditionContextList, timedLock, delayed);
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
            item = JRuleItem.forName(this.getItemName());
            memberItem = JRuleItem.forName(((ItemEvent) event).getItemName());
        } else {
            item = JRuleItem.forName(this.getItemName());
            memberItem = event instanceof GroupItemStateChangedEvent
                    ? JRuleItem.forName(((GroupItemStateChangedEvent) event).getMemberName())
                    : null;
        }

        // updating the item state to be sure that it's update when the JRule method is fired
        // JRuleEventHandler.get().setValue(((ItemStateChangedEvent) event).getItemName(),
        // ((ItemStateChangedEvent) event).getItemState());

        return new JRuleItemEvent(item, memberItem,
                JRuleEventHandler.get().toValue(((ItemStateChangedEvent) event).getItemState()),
                JRuleEventHandler.get().toValue(((ItemStateChangedEvent) event).getOldItemState()));
    }

    @Override
    public String toString() {
        return "JRuleItemChangeExecutionContext{" + "from=" + from + ", to=" + to + ", itemName='" + itemName + '\''
                + ", memberOf=" + memberOf + ", conditionContext=" + conditionContext + ", logName='" + logName + '\''
                + ", uid=" + uid + ", invocationCallback=" + invocationCallback + ", loggingTags="
                + Arrays.toString(loggingTags) + ", preconditionContextList=" + preconditionContextList + '}';
    }
}
