<#include "../CommonLicense.ftlh">

package ${packageName};

import org.openhab.automation.jrule.items.JRuleItemRegistry;

<#list items as item>
import org.openhab.automation.jrule.generated.items.${item.class};
</#list>

/**
* Automatically Generated Class for Items - DO NOT EDIT!
*
* @author Arne Seime - Refactoring
*/
public class JRuleItems {



<#list items as item>
 public static ${item.class} ${item.name};

</#list>

 public JRuleItems() {

   <#list items as item>
    ${item.name} = JRuleItemRegistry.get("${item.name}", ${item.class}.class);

   </#list>

 }

}

