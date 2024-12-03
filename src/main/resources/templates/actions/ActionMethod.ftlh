    /**
     * ${action.id}: ${method.name}
     <#list method.args as arg>
     * @${arg.name}: ${arg.name} of ${arg.type}
     </#list>
     <#if method.hasReturnType == true>* @return ${method.returnType}</#if>
     */
    public ${method.returnType} ${method.name}(<#list method.args as arg>${arg.type} ${arg.name}<#sep>, </#sep></#list>) throws Exception {
        <#if method.hasReturnType == true>return (${method.returnType})</#if> super.invokeMethod("${action.actionClassFqn}", "${method.name}", new Class<?>[]{<#list method.args as arg>${arg.reflectionType}.class<#sep>, </#sep></#list>}<#if method.args?size != 0>, </#if><#list method.args as arg>${arg.name}<#sep>, </#sep></#list>);
    }