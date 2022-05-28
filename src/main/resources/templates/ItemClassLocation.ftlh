<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleSwitchTrigger;

/**
 * Automatically Generated Class for Item ${item.itemName}
 *
 * @author Arne Seime - Initial contribution
 */
public class ${item.itemClassName} implements JRuleSwitchTrigger {

<#include "StaticVariables.ftlh">

  // Empty for now, waiting for PR #40
}
