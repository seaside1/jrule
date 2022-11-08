package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;

public interface JRuleDateTimeItem extends JRuleItem<JRuleDateTimeValue> {
    static JRuleDateTimeItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleDateTimeItem.class);
    }
}
