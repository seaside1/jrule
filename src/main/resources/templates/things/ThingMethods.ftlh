
    public static final String ID = "${thing.id}";

    public static final String LABEL = "<#if thing.label??>${thing.label}</#if>";

    <#list thing.triggerChannels as channel>
    public static final String ${channel.fieldName} = "${thing.id}:${channel.channelName}";
    </#list>

    public ${thing.class}(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return "'"+LABEL+"'/"+ID;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }



