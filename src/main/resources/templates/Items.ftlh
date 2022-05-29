<#include "Header.ftlh">

package ${packageName};

import org.openhab.automation.jrule.items.JRuleItemRegistry;

<#list items as item>
import org.openhab.automation.jrule.items.generated.${item.itemClassName};
</#list>

/**
 * Automatically Generated Class for all items
 *
 * @author Arne Seime - Initial contribution
 */
public class Items {

<#list items as item>
 public static ${item.itemClassName} ${item.itemName} = JRuleItemRegistry.get("${item.itemName}", ${item.itemClassName}.class);
</#list>

}
