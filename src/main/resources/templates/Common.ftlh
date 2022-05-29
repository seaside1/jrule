    public static final String ITEM = "${item.itemName}";

    public static final String LABEL = "<#if item.itemLabel??>${item.itemLabel}</#if>";

    public static final String TYPE = "${item.itemType}";

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

    public ${item.itemClassName}(String itemName) {
        super(itemName);
    }
