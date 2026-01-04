<#include "../../CommonLicense.ftlh">

package ${action.package};

import org.openhab.automation.jrule.actions.${action.parentClass};
import java.util.Objects;
<#list action.imports as import>
    import ${import};
</#list>

<#include "ActionJavadoc.ftlh">
public class ${action.class} extends ${action.parentClass} {
<#include "ActionConstructor.ftlh">
<#list action.methods as method>
    <#include "ActionMethod.ftlh">


</#list>
}
