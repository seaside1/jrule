package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

public interface JRuleStringItem extends JRuleItem<JRuleStringValue> {
    static JRuleStringItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleStringItem.class);
    }
}
