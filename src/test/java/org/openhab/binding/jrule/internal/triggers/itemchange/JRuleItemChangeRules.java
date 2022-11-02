package org.openhab.binding.jrule.internal.triggers.itemchange;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleEvent;

public class JRuleItemChangeRules extends JRule {

    public static final String ITEM = "item";
    public static final String ITEM_FROM = "item_from";
    public static final String ITEM_TO = "item_to";
    public static final String ITEM_FROM_TO = "item_from_to";

    @JRuleName("Test JRuleWhenItemChange")
    @JRuleWhenItemChange(item = ITEM)
    public void itemChange(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/from")
    @JRuleWhenItemChange(item = ITEM_FROM, from = "1")
    public void itemChangeFrom(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/to")
    @JRuleWhenItemChange(item = ITEM_TO, to = "1")
    public void itemChangeTo(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/from/to")
    @JRuleWhenItemChange(item = ITEM_FROM_TO, from = "1", to = "2")
    public void itemChangeFromTo(JRuleEvent event) {
    }
}
