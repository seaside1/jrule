<#include "../CommonLicense.ftlh">

package ${thing.package};

import org.openhab.automation.jrule.things.${thing.parentClass};
import java.util.List;
<#include "ThingJavadoc.ftlh">
public class ${thing.class} extends ${thing.parentClass} {
<#include "ThingMethods.ftlh">

    public List<String> getSubThingUIDs() {
        return List.of(
        <#list thing.subThingUIDs as subThingUID>
            "${subThingUID}"<#sep>,</#sep>
        </#list>
        );

    }
}
