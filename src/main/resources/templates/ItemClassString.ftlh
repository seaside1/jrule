<#include "CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleStringItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleStringItem {

<#include "CommonMethods.ftlh">
}
