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
public class ${item.itemClassName} extends JRuleSwitchItem {
<#include "Common.ftlh">
}
