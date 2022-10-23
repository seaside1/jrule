<#include "../CommonLicense.ftlh">

package ${packageName};

<#list actions as action>
import org.openhab.automation.jrule.generated.actions.${action.class};
</#list>

/**
* Automatically Generated Class for Actions - DO NOT EDIT!
*
* @author Robert Delbrück - Initial contribution
*/
public class JRuleActions {
    <#list actions as action>
    public static final ${action.class} ${action.name};
    </#list>

    static {
        <#list actions as action>
        ${action.name} = new ${action.class}("${action.scope}", "${action.id}");
        </#list>
    }
}

