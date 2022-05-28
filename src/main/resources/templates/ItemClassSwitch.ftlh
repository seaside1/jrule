<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleSwitchTrigger;

/**
 * Automatically Generated Class for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRuleSwitchTrigger {

<#include "Common.ftlh">

    public static JRuleOnOffValue getState() {
        return JRuleItemRegistry.get(ITEM, JRuleSwitchItem.class).getState();
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleSwitchItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleSwitchItem.class).postUpdate(value);
    }
}
