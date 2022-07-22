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
package org.openhab.automation.jrule.internal.test;

import java.util.List;

import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;

/**
 * The {@link JRuleMockedEventBus}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleMockedEventBus extends JRuleEventSubscriber {

    private final List<JRuleMockedItemStateChangedEvent> eventList;

    public JRuleMockedEventBus(String eventBusResourceName) {
        super();
        JRuleTestEventLogParser parser = new JRuleTestEventLogParser(eventBusResourceName);
        eventList = parser.parse();
        registerSubscribedItemsAndChannels();
    }

    public void start() {
        startSubscriber();
        eventList.forEach(super::receive);
    }
}
