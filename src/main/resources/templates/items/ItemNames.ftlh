<#include "../CommonLicense.ftlh">

package ${packageName};

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
 public static final String ${itemName.name} = "${itemName.name}";
</#list>
}

