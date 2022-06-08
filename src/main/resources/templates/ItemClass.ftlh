<#include "CommonLicense.ftlh">
package ${item.package};

import org.openhab.automation.jrule.items.JRuleItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleItem {
<#include "CommonMethods.ftlh">
}
