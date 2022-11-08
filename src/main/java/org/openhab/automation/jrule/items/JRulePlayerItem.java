package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

public interface JRulePlayerItem extends JRuleItem<JRuleStringValue> {
    static JRulePlayerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRulePlayerItem.class);
    }
}
