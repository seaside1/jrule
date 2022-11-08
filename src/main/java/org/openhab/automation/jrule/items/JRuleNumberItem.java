package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

public interface JRuleNumberItem extends JRuleItem<JRuleStringValue> {
    static JRuleNumberItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleNumberItem.class);
    }
}
