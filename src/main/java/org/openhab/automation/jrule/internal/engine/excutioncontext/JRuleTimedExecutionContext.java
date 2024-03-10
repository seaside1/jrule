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

import java.util.List;

import org.openhab.automation.jrule.internal.engine.JRuleInvocationCallback;

/**
 * The {@link JRuleTimedExecutionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleTimedExecutionContext extends JRuleExecutionContext {
    public JRuleTimedExecutionContext(String uid, String logName, String[] loggingTags,
            JRuleInvocationCallback invocationCallback, List<JRulePreconditionContext> preconditionContextList) {
        super(uid, logName, loggingTags, invocationCallback, preconditionContextList, null, null);
    }
}
