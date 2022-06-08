<#include "License.ftlh">

package ${packageName};

import org.openhab.automation.jrule.items.JRuleItemRegistry;

<#list items as item>
import org.openhab.automation.jrule.items.generated.${item.itemClassName};
</#list>

/**
* Automatically Generated Class for Items - DO NOT EDIT!
*
* @author Arne Seime - Refactoring
*/
public class Items {



<#list items as item>
 public static ${item.itemClassName} ${item.itemName};

</#list>

 public Items() {

   <#list items as item>
    ${item.itemName} = JRuleItemRegistry.get("${item.itemName}", ${item.itemClassName}.class);

   </#list>

 }

}

