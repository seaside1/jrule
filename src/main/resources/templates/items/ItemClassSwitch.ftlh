<#include "../CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.internal.items.JRuleSwitchItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleSwitchItem {
<#include "CommonMethods.ftlh">
}
