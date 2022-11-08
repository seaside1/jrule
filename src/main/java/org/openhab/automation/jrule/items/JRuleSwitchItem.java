package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

public interface JRuleSwitchItem extends JRuleItem<JRuleOnOffValue> {
    String ON = "ON";
    String OFF = "OFF";

    static JRuleSwitchItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleSwitchItem.class);
    }
}
