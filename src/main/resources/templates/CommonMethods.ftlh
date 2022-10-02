    public static final String ITEM = "${item.name}";

    public static final String LABEL = "<#if item.label??>${item.label}</#if>";

    public static final String TYPE = "${item.type}";

    public static final String ID = "${item.id}";

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String toString() {
        return "'"+LABEL+"'/"+ITEM+"/"+TYPE;
    }

    public ${item.class}(String name) {
        super(name);
    }
