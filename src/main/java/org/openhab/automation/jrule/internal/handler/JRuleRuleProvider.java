package org.openhab.automation.jrule.internal.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.core.automation.Action;
import org.openhab.core.automation.Rule;
import org.openhab.core.automation.RuleProvider;
import org.openhab.core.automation.module.script.rulesupport.shared.simple.SimpleRule;
import org.openhab.core.automation.util.TriggerBuilder;
import org.openhab.core.common.registry.ProviderChangeListener;
import org.openhab.core.config.core.Configuration;
import org.osgi.service.component.annotations.Component;

@NonNullByDefault
@Component(immediate = true, service = { JRuleRuleProvider.class, RuleProvider.class })
public class JRuleRuleProvider implements RuleProvider {

    private final Collection<ProviderChangeListener<Rule>> listeners = new ArrayList<>();

    private final Map<String, Rule> rules = new ConcurrentHashMap<>();

    @Override
    public Collection<Rule> getAll() {
        return rules.values();
    }

    @Override
    public void addProviderChangeListener(ProviderChangeListener<Rule> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeProviderChangeListener(ProviderChangeListener<Rule> listener) {
        listeners.remove(listener);
    }

    public void reset() {
        for (Rule rule : rules.values()) {
            listeners.stream().forEach(e -> e.removed(this, rule));
        }
        rules.clear();
    }

    public void add(JRuleExecutionContext context) {

        RuleEntry simpleRule = new RuleEntry("RuleUID");
        simpleRule.setConfiguration(new Configuration(new HashMap<>()));
        simpleRule.setConfigurationDescriptions(new ArrayList<>());
        simpleRule.setName(context.getLogName());
        simpleRule.setDescription(context.getLogName());

        Configuration triggerConfig = new Configuration();
        triggerConfig.put("itemName", "TODO item name");
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("1").withConfiguration(triggerConfig);

        simpleRule.setTriggers(List.of(triggerBuilder.build()));
        simpleRule.setConditions(List.of());
        simpleRule.setActions(List.of());
        simpleRule.setTags(Set.of());

        rules.put(simpleRule.getUID(), simpleRule);

        listeners.stream().forEach(e -> e.added(this, simpleRule));
    }

    private class RuleEntry extends SimpleRule {

        public RuleEntry(String uid) {
            this.uid = uid;
        }

        @Override
        public Object execute(Action module, Map<String, ?> inputs) {
            System.out.println(String.format("Action from GUI for module %s and input %s", module, inputs));
            return "executed";
        }
    }
}
