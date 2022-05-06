package org.openhab.binding.jrule.items.generated;

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleRollershutterItem;
import org.openhab.automation.jrule.rules.value.JRuleStopMoveValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

public class _MyTestRollershutter implements JRuleCommonTrigger {
    public static final String ITEM = "MyTestRollerShutter";

    public static int getState() {
        return JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).getState();
    }

    public static void sendCommand(JRuleUpDownValue command) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleStopMoveValue command) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(command);
    }

    public static void sendCommand(int value) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(value);
    }

    public static void postUpdate(JRuleUpDownValue command) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).postUpdate(command);
    }

    public static void postUpdate(int value) {
        JRuleItemRegistry.get(ITEM, JRuleRollershutterItem.class).sendCommand(value);
    }
}
