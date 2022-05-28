    public static final String ITEM = "${item.itemName}";

    public static final String LABEL = "<#if item.itemLabel??>${item.itemLabel}</#if>";

    public String getName() {
        return ITEM;
    }

    public String getLabel() {
        return LABEL;
    }
