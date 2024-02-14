package org.openhab.automation.jrule.rules.event;

public class JRuleStartupEvent extends JRuleEvent {
    private final int startupLevel;

    public JRuleStartupEvent(int startupLevel) {
        this.startupLevel = startupLevel;
    }

    public int getStartupLevel() {
        return startupLevel;
    }

    @Override
    public String toString() {
        return "JRuleStartupEvent{" + "startupLevel=" + startupLevel + '}';
    }
}
