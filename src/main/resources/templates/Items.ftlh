<#include "Header.ftlh">

package ${packageName};

import org.openhab.automation.jrule.items.*;
import org.openhab.automation.jrule.items.JRuleItemRegistry;

/**
 * Automatically Generated Class for all items
 *
 * @author Arne Seime - Initial contribution
 */
public class Items {

<#list items as item>
 public static ${item.itemClassType} ${item.itemName} = JRuleItemRegistry.get("${item.itemName}", ${item.itemClassType}.class);
</#list>

}
