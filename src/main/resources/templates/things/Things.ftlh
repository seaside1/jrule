<#include "../CommonLicense.ftlh">

package ${packageName};

import org.openhab.automation.jrule.things.JRuleThingRegistry;
import org.openhab.automation.jrule.things.JRuleBridgeThing;
import org.openhab.automation.jrule.things.JRuleStandaloneThing;
import org.openhab.automation.jrule.things.JRuleSubThing;

<#list things as thing>
import org.openhab.automation.jrule.generated.things.${thing.class};
</#list>

/**
* Automatically Generated Class for Things - DO NOT EDIT!
*
* @author Arne Seime - Initial contribution
*/
public class JRuleThings {



<#list things as thing>
 public static ${thing.parentClass} ${thing.name};

</#list>

 public JRuleThings() {

   <#list things as thing>
    ${thing.name} = JRuleThingRegistry.get("${thing.id}", ${thing.class}.class);

   </#list>

 }

}

