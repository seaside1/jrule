<#include "License.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleStringItem;

<#include "ClassJavadoc.ftlh">
public class ${item.itemClassName} extends JRuleStringItem {

<#include "Common.ftlh">
}
