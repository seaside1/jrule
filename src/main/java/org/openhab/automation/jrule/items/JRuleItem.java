/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.items;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleItem<T extends JRuleValue> {
    static <T extends JRuleValue> JRuleItem<T> forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName);
    }

    String getName();

    String getLabel();

    String getType();

    String getId();

    Class<? extends JRuleValue> getDefaultValueClass();

    default String getStateAsString() {
        return getState().toString();
    }

    default T getState() {
        return (T) JRuleEventHandler.get().getValue(getName(), getDefaultValueClass());
    }

    default <TD extends JRuleValue> TD getStateAs(Class<TD> type) {
        return (TD) JRuleEventHandler.get().getValue(getName(), type);
    }

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

    default boolean isGroup() {
        return false;
    }
}
