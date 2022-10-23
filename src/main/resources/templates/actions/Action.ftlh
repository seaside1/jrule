<#include "../CommonLicense.ftlh">

package ${action.package};

import org.openhab.automation.jrule.actions.${action.parentClass};
//import org.openhab.core.thing.binding.ThingActions;
//import org.openhab.core.model.script.actions.Things;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

<#include "ActionJavadoc.ftlh">
public class ${action.class} extends ${action.parentClass} {
<#include "ActionConstructor.ftlh">
<#list action.methods as method>
 <#include "ActionMethod.ftlh">


</#list>
}
