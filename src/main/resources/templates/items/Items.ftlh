<#include "../CommonLicense.ftlh">

package ${packageName};

import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;

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
    /**
     * Name: ${item.name}
     * <br/>
     * Type: ${item.type}
     * <br/>
     * Label: ${(item.label)!}
     * <br/>
     * Tags: ${item.tags}
     * <br/>
     * Metadata: ${item.metadata}
     */
 public static ${item.interfaceClass} ${item.name};

</#list>

 static {
   loadItems();
 }

  /**
  * Need this method for testing
  */
 private static void loadItems() throws JRuleItemNotFoundException {
    <#list items as item>
     ${item.name} = JRuleItemRegistry.get("${item.name}", ${item.internalClass}.class);

    </#list>
 }

}

