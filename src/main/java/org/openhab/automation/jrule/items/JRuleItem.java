package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface JRuleItem<T extends JRuleValue> {
    static <T extends JRuleValue> JRuleItem<T> forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName);
    }
    String getName();

    String getLabel();

    String getType();

    String getId();

    default String getStateAsString() {
        return getState().toString();
    }

    T getState();

    default void sendCommand(T command) {
        JRuleEventHandler.get().sendCommand(getName(), command.toString());
    }

    default void postUpdate(T state) {
        JRuleEventHandler.get().postUpdate(getName(), state.toString());
    }

    default void sendCommand(String state) {
        JRuleEventHandler.get().sendCommand(getName(), state);
    }

    default void postUpdate(String state) {
        JRuleEventHandler.get().postUpdate(getName(), state);
    }

    default Optional<ZonedDateTime> lastUpdated() {
        return lastUpdated(null);
    }

    Optional<ZonedDateTime> lastUpdated(String persistenceServiceId);

    default boolean changedSince(ZonedDateTime timestamp) {
        return changedSince(timestamp, null);
    }

    boolean changedSince(ZonedDateTime timestamp, String persistenceServiceId);

    default boolean updatedSince(ZonedDateTime timestamp) {
        return updatedSince(timestamp, null);
    }

    boolean updatedSince(ZonedDateTime timestamp, String persistenceServiceId);

    default Optional<T> getHistoricState(ZonedDateTime timestamp) {
        return getHistoricState(timestamp, null);
    }

    Optional<T> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId);
}
