/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.internal.engine.excutioncontext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleThingEvent;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.thing.events.ThingStatusInfoChangedEvent;

/**
 * The {@link JRuleThingExecutionContext} - execution context for thing triggers
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleThingExecutionContext extends JRuleExecutionContext {
    private final Optional<String> thing;
    private final Optional<String> from;
    private final Optional<String> to;

    public JRuleThingExecutionContext(JRule jRule, String logName, String[] loggingTags, Optional<String> thing,
                                      Optional<String> from,
                                      Optional<String> to, Method method,
                                      List<JRulePreconditionContext> preconditions) {
        super(jRule, logName, loggingTags, method, preconditions);
        this.thing = thing;
        this.from = from;
        this.to = to;
    }

    public Optional<String> getThing() {
        return thing;
    }

    @Override
    public String toString() {
        return "JRuleThingExecutionContext{" + "thing='" + thing + '\'' + ", from='"
                + from + '\'' + ", to='" + to + '\'' + '}';
    }

    @Override
    public boolean match(AbstractEvent event) {
        if (!(event instanceof ThingStatusInfoChangedEvent)) {
            return false;
        }
        ThingStatusInfoChangedEvent evt = (ThingStatusInfoChangedEvent) event;
        return thing.map(s -> evt.getThingUID().equals(s)).orElse(true)
                && from.map(s -> evt.getOldStatusInfo().getStatus().name().equals(s)).orElse(true)
                && to.map(s -> evt.getStatusInfo().getStatus().name().equals(s)).orElse(true);
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return new JRuleThingEvent(
                ((ThingStatusInfoChangedEvent) event).getThingUID().toString(),
                ((ThingStatusInfoChangedEvent) event).getStatusInfo().getStatus().name()
        );
    }
}