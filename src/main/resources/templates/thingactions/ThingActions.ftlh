<#include "../CommonLicense.ftlh">

package ${packageName};

<#list actions as action>
import org.openhab.automation.jrule.generated.actions.${action.class};
</#list>

/**
* Automatically Generated Class for Actions - DO NOT EDIT!
* <br/>
* Includes all available actions for all things
*
* @author Robert Delbrück - Initial contribution
*/
public class JRuleThingActions {
    <#list actions as action>
    /**
     * thingUid: ${action.id}
     */
    public static final ${action.class} ${action.name};
    </#list>

    static {
        <#list actions as action>
        ${action.name} = new ${action.class}("${action.scope}", "${action.id}");
        </#list>
    }
}

