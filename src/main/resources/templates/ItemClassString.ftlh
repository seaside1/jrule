<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRuleCommonTrigger {

<#include "StaticVariables.ftlh">

    public static String getState() {
        return JRuleItemRegistry.get(ITEM, JRuleStringItem.class).getState();
    }

    public static void sendCommand(String command) {
        JRuleItemRegistry.get(ITEM, JRuleStringItem.class).sendCommand(command);
    }

    public static void postUpdate(String value) {
        JRuleItemRegistry.get(ITEM, JRuleStringItem.class).postUpdate(value);
    }
}
