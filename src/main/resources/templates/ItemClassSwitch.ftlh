<#include "License.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleSwitchItem;

<#include "ClassJavadoc.ftlh">
public class ${item.class} extends JRuleSwitchItem {
<#include "Common.ftlh">
}
