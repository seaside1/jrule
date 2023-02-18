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
package org.openhab.binding.jrule.internal.rules;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.test.JRuleMockedEventBus;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.core.events.Event;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.types.State;

/**
 * The {@link JRuleAbstractTest} is a base class for simple rule trigger testing
 *
 *
 * @author Arne Seime - Initial contribution
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class JRuleAbstractTest {

    protected ItemRegistry itemRegistry;
    protected CollectingEventPublisher eventPublisher;
    private JRuleMockedEventBus eventBus = new JRuleMockedEventBus();

    @AfterAll
    protected void shutdown() {
        eventBus.stop();
    }

    @BeforeAll
    protected void initEngine() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("org.openhab.automation.jrule.engine.executors.enable", "false");
        JRuleConfig config = new JRuleConfig(properties);
        config.initConfig();

        JRuleEngine engine = JRuleEngine.get();
        engine.setConfig(config);

        itemRegistry = Mockito.mock(ItemRegistry.class);
        JRuleEventHandler.get().setItemRegistry(itemRegistry);
        JRuleEngine.get().setItemRegistry(itemRegistry);

        eventPublisher = new CollectingEventPublisher();
        JRuleEventHandler.get().setEventPublisher(eventPublisher);

        eventBus.start();
    }

    protected <T extends JRule> T initRule(Class<T> rule) {
        T spyRule = Mockito.spy(rule);
        JRuleEngine.get().reset();
        JRuleEngine.get().add(spyRule);
        return spyRule;
    }

    protected void fireEvents(boolean async, List<Event> events) {
        eventBus.fire(async, events);
    }

    protected void registerItem(GenericItem item, State state) throws ItemNotFoundException {
        item.setState(state);
        when(itemRegistry.getItem(item.getName())).thenReturn(item);
    }
}
