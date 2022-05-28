<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleContactItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Timo Litzius - Initial contribution
 */
public class ${item.itemClassName} implements JRuleCommonTrigger {

<#include "StaticVariables.ftlh">
    
    public static JRuleOpenClosedValue getState() {
        return JRuleItemRegistry.get(ITEM, JRuleContactItem.class).getState();
    }
}