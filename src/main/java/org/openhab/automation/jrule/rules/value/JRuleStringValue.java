package org.openhab.automation.jrule.rules.value;

public class JRuleStringValue implements JRuleValue {
    private final String value;

    public JRuleStringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static JRuleStringValue getValueFromString(String value) {
        return new JRuleStringValue(value);
    }
}
