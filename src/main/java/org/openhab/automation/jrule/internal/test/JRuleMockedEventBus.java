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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.core.events.Event;

/**
 * The {@link JRuleMockedEventBus}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleMockedEventBus extends JRuleEventSubscriber {
    private final Executor executor = Executors.newFixedThreadPool(10);

    public JRuleMockedEventBus() {
    }

    public void start() {
        startSubscriber();
    }

    public void fire(boolean async, List<Event> events) {
        if (async) {
            events.forEach(event -> executor.execute(() -> super.receive(event)));
        } else {
            events.forEach(super::receive);
        }
    }

    public void fire(String eventBusResourceName) {
        JRuleTestEventLogParser parser = new JRuleTestEventLogParser(eventBusResourceName);
        fire(false, parser.parse());
    }

    public void stop() {
        stopSubscriber();
    }
}
