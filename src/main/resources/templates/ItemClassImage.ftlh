<#include "CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleImageItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleImageItem {

<#include "CommonMethods.ftlh">
}