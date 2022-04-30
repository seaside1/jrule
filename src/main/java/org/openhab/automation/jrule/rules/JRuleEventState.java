package org.openhab.automation.jrule.rules;

import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.core.library.types.QuantityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEventState}
 *
 * @author Gerhard Riegler - Initial contribution
 */
public class JRuleEventState {
    private static final Logger logger = LoggerFactory.getLogger(JRuleEventState.class);
    private final String value;

    public JRuleEventState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public JRuleOnOffValue getValueAsOnOffValue() {
        return JRuleOnOffValue.getValueFromString(value);
    }

    public JRuleOpenClosedValue getValueAsOpenClosedValue() {
        return JRuleOpenClosedValue.getValueFromString(value);
    }

    public JRuleUpDownValue getValueAsUpDownValue() {
        return JRuleUpDownValue.getValueFromString(value);
    }

    public Double getValueAsDouble() {
        try {
            return QuantityType.valueOf(value).doubleValue();
        } catch (Exception e) {
            logger.warn("Error converting {} to double: {}", value, e.getMessage());
            return null;
        }
    }

    public Integer getValueAsInteger() {
        try {
            return QuantityType.valueOf(value).intValue();
        } catch (Exception e) {
            logger.warn("Error converting {} to int: {}", value, e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("JRuleEventState{value='%s'}", value);
    }
}
