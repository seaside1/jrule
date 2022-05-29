    public static final String ITEM = "${item.itemName}";

    public static final String LABEL = "<#if item.itemLabel??>${item.itemLabel}</#if>";

    public static final String TYPE = "${item.itemType}";

    public String getName() {
        return ITEM;
    }

    public String getLabel() {
        return LABEL;
    }

    public String getType() {
        return TYPE;
    }

    public String toString() {
        return "'"+LABEL+"'/"+ITEM+"/"+TYPE;
    }

    public ${item.itemClassName}(String itemName) {
        super(itemName);
    }
