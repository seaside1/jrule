<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.trigger.JRulePlayerTrigger;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRulePlayerTrigger {

<#include "Common.ftlh">
    
    public static JRulePlayPauseValue getState() {
        return JRuleItemRegistry.get(ITEM, JRulePlayerItem.class).getState();
    }

    public static void sendCommand(JRulePlayPauseValue command) {
        JRuleItemRegistry.get(ITEM, JRulePlayerItem.class).sendCommand(command);
    }

    public static void postUpdate(JRulePlayPauseValue value) {
        JRuleItemRegistry.get(ITEM, JRulePlayerItem.class).postUpdate(value);
    }
}