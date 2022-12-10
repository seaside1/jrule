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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleEventState;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleItemReceivedCommandExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemReceivedCommandExecutionContext extends JRuleItemExecutionContext {
    private static final Logger log = LoggerFactory.getLogger(JRuleItemReceivedCommandExecutionContext.class);
    protected final Optional<String> command;

    public JRuleItemReceivedCommandExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            String itemName, boolean memberOf, Optional<JRuleConditionContext> conditionContext,
            List<JRulePreconditionContext> preconditionContextList, Optional<String> command) {
        super(jRule, logName, loggingTags, method, itemName, memberOf, conditionContext, preconditionContextList);
        this.command = command;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        JRuleLog.debug(log, "JRuleItemReceivedCommandExecutionContext", "does it match?: {}, {}, {}", this, event,
                checkData);
        if (!(event instanceof ItemCommandEvent
                && matchCondition(((ItemCommandEvent) event).getItemCommand().toString(), null)
                && command.map(s -> ((ItemCommandEvent) event).getItemCommand().toString().equals(s)).orElse(true))) {
            return false;
        }

        if (!isMemberOf() && ((ItemCommandEvent) event).getItemName().equals(this.getItemName())) {
            return true;
        }
        if (isMemberOf() && checkData instanceof JRuleAdditionalItemCheckData
                && ((JRuleAdditionalItemCheckData) checkData).getBelongingGroups().contains(this.getItemName())) {
            return true;
        }
        return false;
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        final String memberName;
        if (isMemberOf()) {
            memberName = ((ItemEvent) event).getItemName();
        } else {
            memberName = null;
        }

        return new JRuleItemEvent(this.getItemName(), memberName,
                new JRuleEventState(((ItemCommandEvent) event).getItemCommand().toString()), null);
    }

    @Override
    public String toString() {
        return "JRuleItemReceivedCommandExecutionContext{" + "command=" + command + ", itemName='" + itemName + '\''
                + ", memberOf=" + memberOf + ", conditionContext=" + conditionContext + ", logName='" + logName + '\''
                + ", jRule=" + rule + ", method=" + method + ", loggingTags=" + Arrays.toString(loggingTags)
                + ", preconditionContextList=" + preconditionContextList + '}';
    }
}
