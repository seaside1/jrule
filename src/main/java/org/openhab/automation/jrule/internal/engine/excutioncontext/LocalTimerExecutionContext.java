package org.openhab.automation.jrule.internal.engine.excutioncontext;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.core.events.AbstractEvent;

public class LocalTimerExecutionContext extends JRuleExecutionContext {

    private String timerName;

    public LocalTimerExecutionContext(JRuleExecutionContext parentContext, String timerName) {
        super(parentContext.getRule(), parentContext.getLogName(), null, null, null);
        this.timerName = timerName;
    }

    @Override
    public JRuleEvent createJRuleEvent(AbstractEvent event) {
        return null;
    }

    @Override
    public String getLogName() {
        return super.getLogName() + "/" + timerName;
    }

    @Override
    public boolean match(AbstractEvent event, JRuleAdditionalCheckData checkData) {
        return false;
    }
}
