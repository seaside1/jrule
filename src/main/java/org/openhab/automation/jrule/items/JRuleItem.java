/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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
import java.util.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRuleRefreshValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.types.State;

/**
 * The {@link JRuleItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleItem {
    static JRuleItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName);
    }

    static Optional<JRuleItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    String getName();

    String getLabel();

    String getType();

    String getId();

    @NonNullByDefault
    Map<String, JRuleItemMetadata> getMetadata();

    void addMetadata(String namespace, JRuleItemMetadata metadata, boolean override);

    @NonNullByDefault
    List<String> getTags();

    /**
     * Returns all GroupItems, which this item belongs to -> this item is a member of the returning result
     * 
     * @return GroupItems which this items belongs to
     */
    default Set<JRuleGroupItem<? extends JRuleItem>> getGroupItems() {
        return getGroupItems(false);
    }

    /**
     * Returns all GroupItems, which this item belongs to -> this item is a member of the returning result
     *
     * @param recursive recursively up to the root or not
     * @return (recursively) all GroupItems which this items belongs to
     */
    default Set<JRuleGroupItem<? extends JRuleItem>> getGroupItems(boolean recursive) {
        return new HashSet<>(JRuleEventHandler.get().getGroupItems(getName(), recursive));
    }

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

    default Optional<JRuleValue> getStateAt(ZonedDateTime timestamp) {
        return getStateAt(timestamp, null);
    }

    Optional<JRuleValue> getStateAt(ZonedDateTime timestamp, String persistenceServiceId);

    default Optional<State> getPreviousState(boolean skipEquals) {
        return getPreviousState(skipEquals, null);
    }

    Optional<State> getPreviousState(boolean skipEquals, String persistenceServiceId);

    default void persist(JRuleValue state, ZonedDateTime time) {
        persist(state, time, null);
    }

    void persist(JRuleValue state, ZonedDateTime time, String persistenceServiceId);

    default boolean isGroup() {
        return false;
    }
}
