<#include "../CommonLicense.ftlh">

package ${thing.package};

import org.openhab.automation.jrule.things.${thing.parentClass};

<#include "ThingJavadoc.ftlh">
public class ${thing.class} extends ${thing.parentClass} {
<#include "ThingMethods.ftlh">
}
