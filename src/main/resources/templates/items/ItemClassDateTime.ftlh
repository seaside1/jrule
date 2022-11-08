<#include "../CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.internal.items.JRuleDateTimeItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleDateTimeItem  {

<#include "CommonMethods.ftlh">
}
