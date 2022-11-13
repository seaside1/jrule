<#include "../CommonLicense.ftlh">

package ${packageName};

/**
* Automatically Generated Enum for ItemNames - DO NOT EDIT!
*
* @author Robert Delbrück - Initial
*/
public class JRuleItemName {
<#list itemNames as itemName>
 public static final String ${itemName.name} = "${itemName.name}";
</#list>
}

