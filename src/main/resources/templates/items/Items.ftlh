<#include "../CommonLicense.ftlh">

package ${packageName};

import org.openhab.automation.jrule.items.JRuleItemRegistry;

<#list items as item>
import org.openhab.automation.jrule.internal.items.${item.internalClass};
import org.openhab.automation.jrule.items.${item.interfaceClass};
</#list>

/**
* Automatically Generated Class for Items - DO NOT EDIT!
*
* @author Arne Seime - Refactoring
* @author Robert Delbrück - Refactoring
*/
public class JRuleItems {



<#list items as item>
 public static ${item.interfaceClass} ${item.name};

</#list>

 public JRuleItems() {

   <#list items as item>
    ${item.name} = JRuleItemRegistry.get("${item.name}", ${item.internalClass}.class);

   </#list>

 }

}

