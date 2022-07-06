<#include "CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleContactItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleContactItem  {

<#include "CommonMethods.ftlh">

}