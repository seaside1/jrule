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
import org.openhab.automation.jrule.rules.event.JRuleChannelEvent;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.thing.events.ChannelTriggeredEvent;

/**
 * The {@link JRuleChannelExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleChannelExecutionContext extends JRuleExecutionContext {
    private final String channel;
    private final Optional<String> event;

    public JRuleChannelExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method,
            List<JRulePreconditionContext> preconditionContextList, String channel, Optional<String> event) {
        super(jRule, logName, loggingTags, method, preconditionContextList);
        this.channel = channel;
        this.event = event;
    }

    public String getChannel() {
        return channel;
    }

    public Optional<String> getEvent() {
        return this.event;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        return event instanceof ChannelTriggeredEvent
                && ((ChannelTriggeredEvent) event).getChannel().getAsString().equals(this.channel)
                && this.event.map(e -> e.equals(((ChannelTriggeredEvent) event).getEvent())).orElse(true);
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return new JRuleChannelEvent(((ChannelTriggeredEvent) event).getChannel().getAsString(),
                ((ChannelTriggeredEvent) event).getEvent());
    }
}
