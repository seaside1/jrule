    public ${method.returnType} ${method.name}(<#list method.args as arg>${arg.type} ${arg.name}<#sep>, </#sep></#list>) {
        super.invokeMethod("${method.name}", <#list method.args as arg>${arg.name}<#sep>, </#sep></#list>);
        //<#if method.hasReturnType == true>return (${method.returnType})</#if> ((${method.type}) thingActions).${method.name}(<#list method.args as arg>${arg.name}<#sep>, </#sep></#list>);
        /*try {
            Method method = thingActions.getClass().getDeclaredMethod("${method.name}", <#list method.args as arg>${arg.type}.class<#sep>, </#sep></#list>);
            <#if method.hasReturnType == true>return (${method.returnType})</#if>method.invoke(thingActions, <#list method.args as arg>${arg.name}<#sep>, </#sep></#list>);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("method not found", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("error invoking method", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("cannot access method", e);
        }*/
    }