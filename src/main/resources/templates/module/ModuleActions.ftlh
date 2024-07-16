<#include "../CommonLicense.ftlh">

package ${packageName};

import org.openhab.automation.jrule.internal.handler.JRuleModuleActionHandler;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;


/**
* Automatically Generated Class for Rule Module Actions - DO NOT EDIT!
* <br/>
* Includes all available actions for all rule modules
*
* @author Arne Seime- Initial contribution
*/
public class JRuleModuleActions {
<#list methods as action>

    /**
     * ${action.label}
     * ${action.description}
     * @return Map<String, Object> - The result of the action
    */

    public static Map <String,Object> ${action.name} (<#list action.args as arg>${arg.type} ${arg.name}<#sep>, </#sep></#list>) {
        Map<String, Object> context = new HashMap<>();
        <#list action.args as arg>
        context.put("${arg.name}", ${arg.name});
        </#list>

        return JRuleModuleActionHandler.get().execute("${action.uid}", context);
    }
</#list>

}

