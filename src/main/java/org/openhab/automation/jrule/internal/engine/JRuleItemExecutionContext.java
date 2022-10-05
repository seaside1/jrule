package org.openhab.automation.jrule.internal.engine;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRulePrecondition;

import java.lang.reflect.Method;

public class JRuleItemExecutionContext extends JRuleExecutionContext {
    private static final String FROM_PREFIX = " from ";
    private static final String TO_PREFIX = " to ";
    private static final String SPACE = " ";

    private final String itemClass;
    private final String itemName;
    private final String trigger;
    private final String update;
    private final String from;
    private final String to;
    private final Double gt;
    private final Double gte;
    private final Double lt;
    private final Double lte;
    protected final String eq;
    protected final String neq;

    public JRuleItemExecutionContext(JRule jRule, String logName, String[] loggingTags, String trigger, String from, String to, String update, String ruleName, String itemClass, String itemName, Method method, boolean eventParameterPresent, Double lt, Double lte, Double gt, Double gte, String eq, String neq, JRulePrecondition[] preconditions) {
        super(jRule, logName, loggingTags, ruleName, method, eventParameterPresent, preconditions);
        this.itemClass = itemClass;
        this.itemName = itemName;
        this.trigger = trigger;
        this.update = update;
        this.from = from;
        this.to = to;
        this.gt = gt;
        this.gte = gte;
        this.lt = lt;
        this.lte = lte;
        this.eq = eq;
        this.neq = neq;
    }

    public String getItemName() {
        return itemName;
    }

    public Double getGt() {
        return gt;
    }

    public Double getGte() {
        return gte;
    }

    public Double getLt() {
        return lt;
    }

    public Double getLte() {
        return lte;
    }

    public String getEq() {
        return eq;
    }

    public String getNeq() {
        return neq;
    }

    public String getTrigger() {
        return trigger;
    }

    public boolean isComparatorOperation() {
        return lte != null || lt != null || gt != null || gte != null || eq != null || neq != null;
    }

    private String buildFromToString(String trigger, String from, String to) {
        final StringBuilder builder = new StringBuilder();
        builder.append(trigger);
        if (from != null) {
            builder.append(FROM_PREFIX);
            builder.append(from);
        }
        if (to != null) {
            builder.append(TO_PREFIX);
            builder.append(to);
        }
        return builder.toString();
    }


    private String buildUpdateString(String trigger, String update) {
        return trigger +
                SPACE +
                update;
    }

    public String getTriggerFullString() {
        if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
            return buildFromToString(trigger, from, to);
        }
        if (from != null && !from.isEmpty()) {
            return buildFromToString(trigger, from, null);
        }
        if (to != null && !to.isEmpty()) {
            return buildFromToString(trigger, null, to);
        }
        if (update != null && !update.isEmpty()) {
            return buildUpdateString(trigger, update);
        }
        return trigger;
    }

    @Override
    public String toString() {
        return "JRuleExecutionContext [trigger=" + trigger + ", ruleName=" + ruleName + ", itemClass=" + itemClass
                + ", itemName=" + itemName + ", from=" + from + ", to=" + to + ", gt=" + gt + ", gte=" + gte + ", lt="
                + lt + ", lte=" + lte + ", eq=" + eq + ", update=" + update + ", jRule=" + jRule + ", method=" + method
                + ", eventParameterPresent=" + eventParameterPresent + "]";
    }
}
