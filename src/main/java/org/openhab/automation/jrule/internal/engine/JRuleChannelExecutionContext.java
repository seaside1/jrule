package org.openhab.automation.jrule.internal.engine;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRulePrecondition;

import java.lang.reflect.Method;

public class JRuleChannelExecutionContext extends JRuleExecutionContext {
    private final String channel;
    private final String event;

    public JRuleChannelExecutionContext(JRule jRule, String logName, String[] loggingTags, String ruleName, Method method, boolean eventParameterPresent, JRulePrecondition[] preconditions, String channel, String event) {
        super(jRule, logName, loggingTags, ruleName, method, eventParameterPresent, preconditions);
        this.channel = channel;
        this.event = event;
    }

    public String getChannel() {
        return channel;
    }

    public String getEvent() {
        return this.event;
    }
}
