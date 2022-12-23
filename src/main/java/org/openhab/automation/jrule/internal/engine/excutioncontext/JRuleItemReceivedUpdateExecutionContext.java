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
            String itemName, boolean memberOf, Optional<Double> lt, Optional<Double> lte, Optional<Double> gt,
            Optional<Double> gte, Optional<String> eq, Optional<String> neq,
            List<JRulePreconditionContext> preconditionContextList, Optional<String> state, Duration timedLock) {
        super(jRule, logName, loggingTags, method, itemName, memberOf, lt, lte, gt, gte, eq, neq,
                preconditionContextList, timedLock);
        this.state = state;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        if (!(event instanceof ItemStateEvent
                && super.matchCondition(((ItemStateEvent) event).getItemState().toString())
                && state.map(s -> ((ItemStateEvent) event).getItemState().toString().equals(s)).orElse(true))) {
            return false;
        }
        if (!isMemberOf() && ((ItemStateEvent) event).getItemName().equals(this.getItemName())) {
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
                JRuleEventHandler.get().toValue(((ItemStateEvent) event).getItemState()), null);
    }

    @Override
    public String toString() {
        return "JRuleItemReceivedUpdateExecutionContext{" + "state=" + state + ", itemName='" + itemName + '\''
                + ", memberOf=" + memberOf + ", gt=" + gt + ", gte=" + gte + ", lt=" + lt + ", lte=" + lte + ", eq="
                + eq + ", neq=" + neq + ", logName='" + logName + '\'' + ", jRule=" + rule + ", method=" + method
                + ", loggingTags=" + Arrays.toString(loggingTags) + ", preconditionContextList="
                + preconditionContextList + '}';
    }
}
