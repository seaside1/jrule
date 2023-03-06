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
package org.openhab.automation.jrule.internal.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleCondition;
import org.openhab.automation.jrule.rules.JRulePrecondition;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRuleWhenThingTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenTimeTrigger;
import org.openhab.core.automation.Action;
import org.openhab.core.automation.Condition;
import org.openhab.core.automation.Trigger;
import org.openhab.core.automation.internal.module.handler.ItemStateConditionHandler;
import org.openhab.core.automation.module.script.rulesupport.shared.simple.SimpleRule;
import org.openhab.core.automation.util.ModuleBuilder;
import org.openhab.core.automation.util.TriggerBuilder;
import org.openhab.core.config.core.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEntry} represents a rule in the user interface
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleEntry extends SimpleRule {

    private final Logger logger = LoggerFactory.getLogger(JRuleEntry.class);

    List<Trigger> ruleTriggers = new ArrayList<>();

    List<Condition> conditions = new ArrayList<>();

    int triggerCounter = 0;

    int conditionCounter = 0;

    public JRuleEntry(JRule jRule, Method method, String ruleName) {
        this.uid = jRule.getClass().getCanonicalName().replace("org.openhab.automation.jrule.rules.user.", "") + "."
                + method.getName();
        setTriggers(ruleTriggers);
        setConditions(conditions);
        setConfiguration(new Configuration(new HashMap<>()));
        setConfigurationDescriptions(new ArrayList<>());
        setName(ruleName);
        setActions(List.of());
        setTags(Set.of());
    }

    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        System.out.println(String.format("Action from GUI for module %s and input %s", module, inputs));
        return "executed";
    }

    public void addJRuleWhenAnnotation(JRuleWhenItemReceivedUpdate jRuleWhen) {
        Configuration triggerConfig = new Configuration();
        triggerConfig.put("itemName", jRuleWhen.item());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID("core.ItemStateUpdateTrigger").withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenItemReceivedCommand jRuleWhen) {
        Configuration triggerConfig = new Configuration();
        triggerConfig.put("itemName", jRuleWhen.item());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID("core.ItemCommandTrigger").withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenItemChange jRuleWhen) {
        Configuration triggerConfig = new Configuration();
        triggerConfig.put("itemName", jRuleWhen.item());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID("core.ItemStateChangeTrigger").withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenChannelTrigger jRuleWhen) {
        Configuration triggerConfig = new Configuration();
        triggerConfig.put("channelUID", jRuleWhen.channel());
        if (jRuleWhen.event() != null)
            triggerConfig.put("event", jRuleWhen.event());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID("core.ChannelEventTrigger").withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenCronTrigger jRuleWhen) {
        Configuration triggerConfig = new Configuration();
        triggerConfig.put("cronExpression", jRuleWhen.cron());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID("timer.GenericCronTrigger").withConfiguration(triggerConfig);

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenTimeTrigger jRuleWhen) {
        Configuration triggerConfig = new Configuration();
        List<String> triggers = new ArrayList<>();

        if (jRuleWhen.hours() != -1)
            triggers.add("hours=" + jRuleWhen.hours());
        if (jRuleWhen.minutes() != -1)
            triggers.add("minutes=" + jRuleWhen.minutes());
        if (jRuleWhen.seconds() != -1)
            triggers.add("seconds=" + jRuleWhen.seconds());
        triggerConfig.put("cronExpression", String.join(",", triggers));
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID("timer.GenericCronTrigger").withConfiguration(triggerConfig);

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenThingTrigger jRuleWhen) {
        Configuration triggerConfig = new Configuration();
        triggerConfig.put("thingUID", jRuleWhen.thing());
        if (jRuleWhen.to() != null)
            triggerConfig.put("status", jRuleWhen.to());
        if (jRuleWhen.from() != null)
            triggerConfig.put("previousStatus", jRuleWhen.from());

        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID("core.ThingStatusChangeTrigger").withConfiguration(triggerConfig);

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addPrecondition(JRulePrecondition jRulePrecondition) {
        Configuration conditionConfiguration = new Configuration();
        conditionConfiguration.put(ItemStateConditionHandler.ITEM_NAME, jRulePrecondition.item());

        JRuleCondition condition = jRulePrecondition.condition();
        if (!"".equals(condition.eq())) {
            conditionConfiguration.put(ItemStateConditionHandler.OPERATOR, "=");
            conditionConfiguration.put(ItemStateConditionHandler.STATE, condition.eq());
        } else if (!"".equals(condition.neq())) {
            conditionConfiguration.put(ItemStateConditionHandler.OPERATOR, "!=");
            conditionConfiguration.put(ItemStateConditionHandler.STATE, condition.neq());
        } else if (condition.gt() != Double.MIN_VALUE) {
            conditionConfiguration.put(ItemStateConditionHandler.OPERATOR, ">");
            conditionConfiguration.put(ItemStateConditionHandler.STATE, condition.gt());
        } else if (condition.gte() != Double.MIN_VALUE) {
            conditionConfiguration.put(ItemStateConditionHandler.OPERATOR, ">=");
            conditionConfiguration.put(ItemStateConditionHandler.STATE, condition.gte());
        } else if (condition.lt() != Double.MIN_VALUE) {
            conditionConfiguration.put(ItemStateConditionHandler.OPERATOR, "<");
            conditionConfiguration.put(ItemStateConditionHandler.STATE, condition.lt());
        } else if (condition.lte() != Double.MIN_VALUE) {
            conditionConfiguration.put(ItemStateConditionHandler.OPERATOR, "<=");
            conditionConfiguration.put(ItemStateConditionHandler.STATE, condition.lte());
        } else {
            logger.error(
                    "Unsupported comparison operator found on @JRuleCondition in rule {}. Skipping GUI registration of @JRulePrecondition",
                    uid);
            return;
        }

        Condition ruleCondition = ModuleBuilder.createCondition().withId("" + (conditionCounter++))
                .withTypeUID("core.ItemStateCondition").withConfiguration(conditionConfiguration).build();
        conditions.add(ruleCondition);
    }
}
