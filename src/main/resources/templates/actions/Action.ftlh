<#include "../CommonLicense.ftlh">

package ${action.package};

import org.openhab.automation.jrule.actions.${action.parentClass};
import java.util.Objects;
<#list action.methods as method>
<#if method.import == true>
import ${method.returnType};
</#if>
</#list>

<#include "ActionJavadoc.ftlh">
// Wrapping class: ${action.type}
public class ${action.class} extends ${action.parentClass} {
<#include "ActionConstructor.ftlh">
<#list action.methods as method>
 <#include "ActionMethod.ftlh">


</#list>
}
