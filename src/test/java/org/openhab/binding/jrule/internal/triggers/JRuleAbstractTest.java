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
package org.openhab.binding.jrule.internal.triggers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.test.JRuleMockedEventBus;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.core.events.Event;
import org.openhab.core.items.ItemRegistry;

/**
 * The {@link JRuleAbstractTest} is a base class for simple rule trigger testing
 *
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleAbstractTest {
    protected static ItemRegistry itemRegistry = Mockito.mock(ItemRegistry.class);

    @BeforeAll
    public static void initEngine() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("org.openhab.automation.jrule.engine.executors.enable", "false");
        JRuleConfig config = new JRuleConfig(properties);
        config.initConfig();

        JRuleEngine engine = JRuleEngine.get();
        engine.setConfig(config);
        engine.setItemRegistry(itemRegistry);
    }

    protected <T extends JRule> T initRule(T rule) {
        T spyRule = Mockito.spy(rule);
        JRuleEngine.get().add(spyRule);
        return spyRule;
    }

    protected void fireEvents(List<Event> events) {
        JRuleMockedEventBus eventBus = new JRuleMockedEventBus(events);
        eventBus.start();
    }
}
