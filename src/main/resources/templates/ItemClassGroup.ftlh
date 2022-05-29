<#include "License.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.${item.itemGroupClassName};

<#include "ClassJavadoc.ftlh">
public class ${item.itemClassName} extends ${item.itemGroupClassName}  {

<#include "Common.ftlh">

<#if item.quantityType??>
    public static final String QUANTITY_TYPE = "${item.quantityType}";

    public String getQuantityType() {
    return QUANTITY_TYPE;
    }
</#if>


}
