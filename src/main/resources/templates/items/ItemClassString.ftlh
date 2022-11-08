<#include "../CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.internal.items.JRuleStringItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleStringItem {

<#include "CommonMethods.ftlh">
}
