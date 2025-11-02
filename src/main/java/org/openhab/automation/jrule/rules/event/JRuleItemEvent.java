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
package org.openhab.automation.jrule.rules.event;

import java.time.ZonedDateTime;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleItemEvent}
 * <br>
 * ATTENTION: For Command events the state isn't updated in the item yet, so use the state property.
 *
 * @author Robert Delbrück
 */
public class JRuleItemEvent extends JRuleEvent {
    private final JRuleItem item;
    private final JRuleItem memberItem;
    private final JRuleValue state;
    private final JRuleValue oldState;

    private final @Nullable ZonedDateTime lastStateUpdate;
    private final @Nullable ZonedDateTime lastStateChange;

    public JRuleItemEvent(JRuleItem item, JRuleItem memberItem, JRuleValue state, JRuleValue oldState,
            @Nullable ZonedDateTime lastStateUpdate, @Nullable ZonedDateTime lastStateChange) {
        this.item = item;
        this.memberItem = memberItem;
        this.state = state;
        this.oldState = oldState;
        this.lastStateUpdate = lastStateUpdate;
        this.lastStateChange = lastStateChange;
    }

    public JRuleItem getItem() {
        return item;
    }

    /**
     * Casts the item to the given type.
     * 
     * @param asType Cast to this type
     * @return the casted item
     * @param <I> new item type
     */
    public <I extends JRuleItem> I getItem(Class<I> asType) {
        if (!asType.isAssignableFrom(item.getClass())) {
            throw new JRuleRuntimeException(String.format("'%s' cannot be cast to '%s'", item.getClass(), asType));
        }
        return (I) item;
    }

    public JRuleItem getMemberItem() {
        return memberItem;
    }

    /**
     * Casts the member-item to the given type.
     * 
     * @param asType Cast to this type
     * @return the casted member-item
     * @param <I> new item type
     */
    public <I extends JRuleItem> I getMemberItem(Class<I> asType) {
        if (!asType.isAssignableFrom(memberItem.getClass())) {
            throw new JRuleRuntimeException(
                    String.format("'%s' cannot be cast to '%s'", memberItem.getClass(), asType));
        }
        return (I) memberItem;
    }

    /**
     * Gets the new state of the item. Must be used while receiving commands.
     * 
     * @return the new item state
     */
    public JRuleValue getState() {
        return state;
    }

    /**
     * Gets the old state of the item. Just for Item-Change-Events
     * 
     * @return the old state of the item
     */
    public JRuleValue getOldState() {
        return oldState;
    }

    /**
     * Gets the timestamp of the previous state update that occurred prior to this event.
     *
     * @return the last state update
     */
    public @Nullable ZonedDateTime getLastStateUpdate() {
        return lastStateUpdate;
    }

    /**
     * Gets the timestamp of the previous state change that occurred prior to this event.
     *
     * @return the last state change
     */
    public @Nullable ZonedDateTime getLastStateChange() {
        return lastStateChange;
    }

    @Override
    public String toString() {
        return String.format("JRuleEvent [item=%s, memberItem=%s, oldState=%s]", item, memberItem, oldState);
    }
}
