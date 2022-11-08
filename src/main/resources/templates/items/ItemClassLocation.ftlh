<#include "../CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.internal.items.JRuleLocationItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleLocationItem {

<#include "CommonMethods.ftlh">

  // Empty for now, waiting for PR #40
}
