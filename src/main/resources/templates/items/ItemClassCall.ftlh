<#include "../CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleCallItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleCallItem {

<#include "CommonMethods.ftlh">
}