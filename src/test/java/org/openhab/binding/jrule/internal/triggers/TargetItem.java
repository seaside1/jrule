package org.openhab.binding.jrule.internal.triggers;

import org.openhab.automation.jrule.items.JRuleStringItem;

public class TargetItem extends JRuleStringItem {

    protected TargetItem(String itemName) {
        super(itemName);
    }

    @Override
    public String getLabel() {
        return "label";
    }

    @Override
    public String getType() {
        return "STRING";
    }

    @Override
    public String getId() {
        return getId();
    }
}
