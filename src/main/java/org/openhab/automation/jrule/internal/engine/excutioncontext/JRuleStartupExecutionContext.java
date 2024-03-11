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
import java.util.List;

import org.openhab.automation.jrule.internal.engine.JRuleInvocationCallback;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.event.JRuleStartupEvent;
import org.openhab.core.events.AbstractEvent;
import org.openhab.core.events.system.StartlevelEvent;

/**
 * The {@link JRuleStartupExecutionContext} - execution context for startup triggers
 *
 * @author Robert Delbrück
 */
public class JRuleStartupExecutionContext extends JRuleExecutionContext {

    private final int startupLevel;

    public JRuleStartupExecutionContext(String uid, String logName, String[] loggingTags,
            JRuleInvocationCallback invocationCallback, List<JRulePreconditionContext> preconditionContextList,
            Duration timedLock, Duration delayed, int startupLevel) {
        super(uid, logName, loggingTags, invocationCallback, preconditionContextList, timedLock, delayed);
        this.startupLevel = startupLevel;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        if (!(event instanceof StartlevelEvent evt)) {
            return false;
        }
        return startupLevel == evt.getStartlevel();
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        StartlevelEvent startlevelEvent = (StartlevelEvent) event;

        return new JRuleStartupEvent((startlevelEvent).getStartlevel());
    }

    public int getStartupLevel() {
        return startupLevel;
    }
}
