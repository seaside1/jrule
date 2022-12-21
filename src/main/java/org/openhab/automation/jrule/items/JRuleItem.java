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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleRefreshValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleItem {
    static JRuleItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName);
    }

    String getName();

    String getLabel();

    String getType();

    String getId();

    @NonNullByDefault
    Map<String, String> getMetadata();

    @NonNullByDefault
    List<String> getTags();

    default String getStateAsString() {
        return getState().toString();
    }

    default JRuleValue getState() {
        return JRuleEventHandler.get().getValue(getName());
    }

    default <TD extends JRuleValue> TD getStateAs(Class<TD> type) {
        return JRuleEventHandler.get().getValue(getName(), type);
    }

    default void sendUncheckedCommand(JRuleValue command) {
        JRuleEventHandler.get().sendCommand(getName(), command);
    }

    default void postUncheckedUpdate(JRuleValue state) {
        JRuleEventHandler.get().postUpdate(getName(), state);
    }

    default void postUpdate(JRuleRefreshValue state) {
        postUncheckedUpdate(state);
    }

    default void postNullUpdate() {
        JRuleEventHandler.get().postUpdate(getName(), null);
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

    default Optional<JRuleValue> getHistoricState(ZonedDateTime timestamp) {
        return getHistoricState(timestamp, null);
    }

    Optional<JRuleValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId);

    default boolean isGroup() {
        return false;
    }
}
