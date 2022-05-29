<#include "License.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleSwitchItem;

<#include "ClassJavadoc.ftlh">
public class ${item.itemClassName} extends JRuleSwitchItem {
<#include "Common.ftlh">
}
