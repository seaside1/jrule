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

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.core.events.AbstractEvent;

/**
 * The {@link JRuleLocalTimerExecutionContext} is used to provide context information when executing local timers
 * created inside rules
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleLocalTimerExecutionContext extends JRuleExecutionContext {

    private JRuleExecutionContext parentContext;
    private String timerName;

    public JRuleLocalTimerExecutionContext(JRuleExecutionContext parentContext, String timerName) {
        super(parentContext.getRule(), parentContext.getLogName(), parentContext.getLoggingTags(),
                parentContext.getMethod(), parentContext.getPreconditionContextList(), null, null);
        this.parentContext = parentContext;
        this.timerName = timerName;
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return null;
    }

    @Override
    public String getLogName() {
        return parentContext.getLogName() + " / " + timerName;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        return false;
    }
}
