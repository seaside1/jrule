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
package org.openhab.automation.jrule.internal.test;

import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.openhab.core.types.State;

/**
 * The {@link JRuleMockedItemStateChangedEvent}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleMockedItemStateChangedEvent extends ItemStateChangedEvent {

    protected JRuleMockedItemStateChangedEvent(String topic, String payload, String itemName, State newItemState,
            State oldItemState) {
        super(topic, payload, itemName, newItemState, oldItemState, null, null, JRuleConfig.BASE_PACKAGE);
    }

    @Override
    public String getType() {
        return ItemStateChangedEvent.class.getSimpleName();
    }
}
