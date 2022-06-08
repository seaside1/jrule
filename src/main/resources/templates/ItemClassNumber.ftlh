<#include "License.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.JRuleNumberItem;

<#include "ClassJavadoc.ftlh">
public class ${item.class} extends JRuleNumberItem {

<#include "Common.ftlh">

<#if item.quantityType??>
    public static final String QUANTITY_TYPE = "${item.quantityType}";

    public String getQuantityType() {
    return QUANTITY_TYPE;
    }
</#if>

}
