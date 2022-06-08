<#include "CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleNumberItem;

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends JRuleNumberItem {

<#include "CommonMethods.ftlh">

<#if item.quantityType??>
    public static final String QUANTITY_TYPE = "${item.quantityType}";

    public String getQuantityType() {
    return QUANTITY_TYPE;
    }
</#if>

}
