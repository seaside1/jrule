package org.openhab.binding.jrule.internal.triggers;

import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleLogName;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;

public class JRuleItemRules extends JRule {

    public static final String TRIGGER_ITEM = "triggerItem";
    public static final String TARGET_ITEM = "targetItem";

    @JRuleName("Rule name")
    @JRuleLogName("Rule log name")
    @JRuleWhenItemChange(item = TRIGGER_ITEM)
    public void testSendCommand() {
        JRuleStringItem.forName(TARGET_ITEM).sendCommand("command");
        createTimer("TimerName", 1, unused -> {
            JRuleStringItem.forName(TARGET_ITEM).sendCommand("timedCommand");
            createTimer("NestedTimer", 1, unused2 -> {
                JRuleStringItem.forName(TARGET_ITEM).sendCommand("nestedTimedCommand");
            });
        });
    }
}
