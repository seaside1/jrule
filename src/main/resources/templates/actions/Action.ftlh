<#include "../CommonLicense.ftlh">

package ${action.package};

import java.util.Objects;
import java.lang.Exception;
import org.openhab.automation.jrule.actions.JRuleAbstractAction;
<#list action.imports as import>
    import ${import};
</#list>

<#include "ActionJavadoc.ftlh">
public class ${action.class} extends JRuleAbstractAction {
    private static ${action.class} instance;

    public static ${action.class} getInstance() {
        if (instance == null) {
            instance = new ${action.class}();
        }
        return instance;
    }

<#list action.methods as method>
    <#include "ActionMethod.ftlh">


</#list>
}
