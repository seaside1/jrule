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
package org.openhab.automation.jrule.rules.event;

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

    public JRuleItemEvent(JRuleItem item, JRuleItem memberItem, JRuleValue state, JRuleValue oldState) {
        this.item = item;
        this.memberItem = memberItem;
        this.state = state;
        this.oldState = oldState;
    }

    public JRuleItem getItem() {
        return item;
    }

    public <I extends JRuleItem> I getItem(Class<I> asType) {
        if (!asType.isAssignableFrom(item.getClass())) {
            throw new JRuleRuntimeException(String.format("'%s' cannot be cast to '%s'", item.getClass(), asType));
        }
        return (I) item;
    }

    public JRuleItem getMemberItem() {
        return memberItem;
    }

    public <I extends JRuleItem> I getMemberItem(Class<I> asType) {
        if (!asType.isAssignableFrom(memberItem.getClass())) {
            throw new JRuleRuntimeException(
                    String.format("'%s' cannot be cast to '%s'", memberItem.getClass(), asType));
        }
        return (I) memberItem;
    }

    public JRuleValue getState() {
        return state;
    }

    public JRuleValue getOldState() {
        return oldState;
    }

    @Override
    public String toString() {
        return String.format("JRuleEvent [item=%s, memberItem=%s, oldState=%s]", item, memberItem, oldState);
    }
}
