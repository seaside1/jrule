    public static final String ID = "${thing.id}";

    public static final String LABEL = "<#if thing.label??>${thing.label}</#if>";

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

