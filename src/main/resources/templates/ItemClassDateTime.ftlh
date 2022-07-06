<#include "CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleDateTimeItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleDateTimeItem  {

<#include "CommonMethods.ftlh">
}
