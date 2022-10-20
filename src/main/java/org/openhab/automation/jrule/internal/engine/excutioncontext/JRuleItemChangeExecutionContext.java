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
import org.openhab.core.items.events.ItemStateChangedEvent;

/**
 * The {@link JRuleItemChangeExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemChangeExecutionContext extends JRuleItemExecutionContext {
    private final Optional<String> from;
    private final Optional<String> to;

    public JRuleItemChangeExecutionContext(JRule jRule, String logName, String[] loggingTags, String itemName,
            Method method, Optional<Double> lt, Optional<Double> lte, Optional<Double> gt, Optional<Double> gte,
            Optional<String> eq, Optional<String> neq, List<JRulePreconditionContext> preconditionContextList,
            Optional<String> from, Optional<String> to) {
        super(jRule, logName, loggingTags, itemName, method, lt, lte, gt, gte, eq, neq, preconditionContextList);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean match(AbstractEvent event) {
        return event instanceof ItemStateChangedEvent
                && ((ItemStateChangedEvent) event).getItemName().equals(this.getItemName())
                && from.map(s -> ((ItemStateChangedEvent) event).getOldItemState().toString().equals(s)).orElse(true)
                && to.map(s -> ((ItemStateChangedEvent) event).getItemState().toString().equals(s)).orElse(true)
                && super.matchCondition(((ItemStateChangedEvent) event).getItemState().toString());
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return new JRuleItemEvent(this.getItemName(),
                new JRuleEventState(((ItemStateChangedEvent) event).getItemState().toString()),
                new JRuleEventState(((ItemStateChangedEvent) event).getOldItemState().toString()));
    }
}
