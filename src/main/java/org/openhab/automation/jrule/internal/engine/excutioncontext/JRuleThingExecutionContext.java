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

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleThingEvent;
import org.openhab.automation.jrule.things.JRuleThingStatus;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.thing.events.ThingStatusInfoChangedEvent;

/**
 * The {@link JRuleThingExecutionContext} - execution context for thing triggers
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleThingExecutionContext extends JRuleExecutionContext {
    private final Optional<String> thing;
    private final Optional<JRuleThingStatus> from;
    private final Optional<JRuleThingStatus> to;

    public JRuleThingExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            Optional<String> thing, Optional<JRuleThingStatus> from, Optional<JRuleThingStatus> to,
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
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        if (!(event instanceof ThingStatusInfoChangedEvent)) {
            return false;
        }
        ThingStatusInfoChangedEvent evt = (ThingStatusInfoChangedEvent) event;
        return thing.map(s -> evt.getThingUID().toString().equals(s)).orElse(true)
                && from.map(s -> evt.getOldStatusInfo().getStatus().name().equals(s.name())).orElse(true)
                && to.map(s -> evt.getStatusInfo().getStatus().name().equals(s.name())).orElse(true);
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        ThingStatusInfoChangedEvent thingStatusChanged = (ThingStatusInfoChangedEvent) event;

        return new JRuleThingEvent((thingStatusChanged).getThingUID().toString(),
                thingStatusChanged.getStatusInfo().getStatus().name(),
                thingStatusChanged.getOldStatusInfo().getStatus().name());
    }

    @Override
    public String toString() {
        return "JRuleThingExecutionContext{" + "thing=" + thing + ", from=" + from + ", to=" + to + ", logName='"
                + logName + '\'' + ", jRule=" + rule + ", method=" + method + ", loggingTags="
                + Arrays.toString(loggingTags) + ", preconditionContextList=" + preconditionContextList + '}';
    }
}
