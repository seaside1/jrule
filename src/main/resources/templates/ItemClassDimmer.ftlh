<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleDimmerTrigger;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRuleDimmerTrigger {

<#include "StaticVariables.ftlh">
    
    public static int getState() {
        return JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).getState();
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).sendCommand(command);
    }

    public static void sendCommand(int command) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).postUpdate(value);
    }

    public static void postUpdate(int value) {
        JRuleItemRegistry.get(ITEM, JRuleDimmerItem.class).postUpdate(value);
    }
}