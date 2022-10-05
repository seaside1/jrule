package org.openhab.automation.jrule.internal.engine;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRulePrecondition;

import java.lang.reflect.Method;

public class JRuleTimedExecutionContext extends JRuleExecutionContext {
    public JRuleTimedExecutionContext(JRule jRule, String logName, String[] loggingTags, Method method, String ruleName, boolean jRuleEventPresent, JRulePrecondition[] preconditions) {
        super(jRule, logName, loggingTags, ruleName, method, jRuleEventPresent, preconditions);
    }
}
