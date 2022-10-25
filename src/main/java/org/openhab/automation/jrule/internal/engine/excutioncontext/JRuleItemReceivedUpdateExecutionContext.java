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
import org.openhab.automation.jrule.rules.JRuleEventState;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.items.events.ItemStateEvent;

/**
 * The {@link JRuleItemReceivedUpdateExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemReceivedUpdateExecutionContext extends JRuleItemExecutionContext {
    private final Optional<String> state;

    public JRuleItemReceivedUpdateExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            String itemName, Optional<Double> lt, Optional<Double> lte, Optional<Double> gt, Optional<Double> gte,
            Optional<String> eq, Optional<String> neq, List<JRulePreconditionContext> preconditionContextList,
            Optional<String> state) {
        super(jRule, logName, loggingTags, method, itemName, lt, lte, gt, gte, eq, neq, preconditionContextList);
        this.state = state;
    }

    @Override
    public boolean match(AbstractEvent event) {
        return event instanceof ItemStateEvent && ((ItemStateEvent) event).getItemName().equals(this.getItemName())
                && state.map(s -> ((ItemStateEvent) event).getItemState().toString().equals(s)).orElse(true)
                && super.matchCondition(((ItemStateEvent) event).getItemState().toString());
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return new JRuleItemEvent(this.getItemName(), null,
                new JRuleEventState(((ItemStateEvent) event).getItemState().toString()), null);
    }
}
