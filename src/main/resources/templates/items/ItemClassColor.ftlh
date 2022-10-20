<#include "../CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleColorItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleColorItem {

<#include "CommonMethods.ftlh">
}