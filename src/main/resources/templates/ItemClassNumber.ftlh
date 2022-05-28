<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRuleCommonTrigger {

<#include "Common.ftlh">
    
    public static Double getState() {
        return JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).getState();
    }

    public static void sendCommand(double command) {
        JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).sendCommand(command);
    }

    public static void postUpdate(double value) {
        JRuleItemRegistry.get(ITEM, JRuleNumberItem.class).postUpdate(value);
    }
}
