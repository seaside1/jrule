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

public abstract class JRuleAbstractTest {
    @BeforeAll
    public static void initEngine() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("org.openhab.automation.jrule.engine.executors.enable", "false");
        JRuleConfig config = new JRuleConfig(properties);
        config.initConfig();

        JRuleEngine engine = JRuleEngine.get();
        engine.setConfig(config);
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
