package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

public interface JRuleRollershutterItem extends JRuleItem<JRuleStringValue> {
    static JRuleRollershutterItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleRollershutterItem.class);
    }
}
