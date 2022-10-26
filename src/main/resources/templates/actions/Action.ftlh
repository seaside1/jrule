<#include "../CommonLicense.ftlh">

package ${action.package};

import org.openhab.automation.jrule.actions.${action.parentClass};
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

<#include "ActionJavadoc.ftlh">
// Wrapping class: ${action.type}
public class ${action.class} extends ${action.parentClass} {
<#include "ActionConstructor.ftlh">
<#list action.methods as method>
 <#include "ActionMethod.ftlh">


</#list>
}
