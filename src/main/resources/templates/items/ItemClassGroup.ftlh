<#include "../CommonLicense.ftlh">

package ${item.package};

import org.openhab.automation.jrule.items.${item.parentClass};

<#include "CommonJavadoc.ftlh">
public class ${item.class} extends ${item.parentClass}  {

<#include "CommonMethods.ftlh">

<#if item.quantityType??>
    public static final String QUANTITY_TYPE = "${item.quantityType}";

    public String getQuantityType() {
    return QUANTITY_TYPE;
    }
</#if>


}
