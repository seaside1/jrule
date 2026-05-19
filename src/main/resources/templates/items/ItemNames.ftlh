<#include "../CommonLicense.ftlh">

package ${packageName};

import org.openhab.automation.jrule.items.JRuleItemDefinition;

/**
* Automatically Generated Enum for ItemNames - DO NOT EDIT!
*
* @author Robert Delbrück - Initial
*/
public class JRuleItemNames {
<#list itemNames as itemName>
    /**
     * Name: ${itemName.name}
     * <br/>
     * Type: ${itemName.type}
     * <br/>
     * Label: ${(itemName.label)!}
     * <br/>
     * Tags: ${itemName.tags}
     * <br/>
     * Metadata: ${itemName.metadata}
     */
 @JRuleItemDefinition(name="${itemName.name}", type="${itemName.type}", groupNames={<#list itemName.groupNames as groupName>"${groupName}"<#sep>, </#sep></#list>})
 public static final String ${itemName.name} = "${itemName.name}";
</#list>
}

