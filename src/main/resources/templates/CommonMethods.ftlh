    public static final String ITEM = "${item.name}";

    public static final String LABEL = "<#if item.label??>${item.label}</#if>";

    public static final String TYPE = "${item.type}";

    @Override
    public String getName() {
        return ITEM;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "'"+LABEL+"'/"+ITEM+"/"+TYPE;
    }

    public ${item.class}(String name) {
        super(name);
    }
