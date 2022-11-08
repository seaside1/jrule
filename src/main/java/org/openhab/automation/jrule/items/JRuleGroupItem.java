package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleValue;

import java.util.Set;

public interface JRuleGroupItem<T extends JRuleValue> extends JRuleItem<T> {
    default Set<String> members() {
        return JRuleEventHandler.get().getGroupMemberNames(getName());
    }

    default Set<JRuleItem<T>> memberItems() {
        return JRuleEventHandler.get().getGroupMemberItems(getName());
    }

    default void sendCommand(String value) {
        members().forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    default void postUpdate(String value) {
        members().forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }
}
