<#include "License.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleNumberItem;

<#include "ClassJavadoc.ftlh">
public class ${item.itemClassName} extends JRuleNumberItem {

<#include "Common.ftlh">
    
    public static final String QUANTITY_TYPE = "${item.quantityType}";
    
    public String getQuantityType() {
        return QUANTITY_TYPE;
    }
}
