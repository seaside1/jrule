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
package org.openhab.automation.jrule.internal.module;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRuleWhenThingTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenTimeTrigger;
import org.openhab.core.automation.Action;
import org.openhab.core.automation.Trigger;
import org.openhab.core.automation.module.script.rulesupport.shared.simple.SimpleRule;
import org.openhab.core.automation.util.TriggerBuilder;
import org.openhab.core.config.core.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleModuleEntry} represents a rule in the user interface
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleModuleEntry extends SimpleRule {

    private static final Logger logger = LoggerFactory.getLogger(JRuleModuleEntry.class);

    List<Trigger> ruleTriggers = new ArrayList<>();

    List<JRuleExecutionContext> executionContextList = new ArrayList<>();

    Set<String> tags = new LinkedHashSet<>();

    int triggerCounter = 1;

    boolean enabled = false;

    public JRuleModuleEntry(JRule jRule, Method method, String ruleName) {
        this.uid = jRule.getClass().getCanonicalName().replace("org.openhab.automation.jrule.rules.user.", "") + "."
                + method.getName();
        tags.add("JRule");
        setTriggers(ruleTriggers);
        setConditions(List.of());
        setConfiguration(new Configuration());
        setConfigurationDescriptions(List.of());
        setName(ruleName);
        setActions(List.of());
        setTags(tags);
    }

    public void dispose() {
        executionContextList.clear();
    }

    /**
     * Called from framework when a rule has been enabled in the user interface
     */
    public void ruleEnabled() {
        if (!enabled) {
            logger.info("Enabling rule {} / {}", getName(), uid);
        }
        executionContextList.forEach(e -> e.setEnabled(true));
        enabled = true;
    }

    /**
     * Called from the framework when a rule has been disabled in the user interface
     */
    public void ruleDisabled() {
        if (enabled) {
            logger.info("Disabling rule {} / {}", getName(), uid);
        }
        executionContextList.forEach(e -> e.setEnabled(false));
        enabled = false;
    }

    public void addJRuleWhenAnnotation(JRuleWhenItemReceivedUpdate jRuleWhen, JRuleExecutionContext context) {
        Configuration triggerConfig = new Configuration();
        executionContextList.add(context);
        triggerConfig.put("itemName", jRuleWhen.item());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedUpdate.class));
        // .withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenItemReceivedCommand jRuleWhen, JRuleExecutionContext context) {
        Configuration triggerConfig = new Configuration();
        executionContextList.add(context);
        triggerConfig.put("itemName", jRuleWhen.item());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedCommand.class))
                .withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenItemChange jRuleWhen, JRuleExecutionContext context) {
        Configuration triggerConfig = new Configuration();
        executionContextList.add(context);
        triggerConfig.put("itemName", jRuleWhen.item());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemChange.class))
                .withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenChannelTrigger jRuleWhen, JRuleExecutionContext context) {
        Configuration triggerConfig = new Configuration();
        executionContextList.add(context);
        triggerConfig.put("channelUID", jRuleWhen.channel());
        if (jRuleWhen.event() != null)
            triggerConfig.put("event", jRuleWhen.event());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenChannelTrigger.class))
                .withConfiguration(triggerConfig);
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenCronTrigger jRuleWhen, JRuleExecutionContext context) {
        Configuration triggerConfig = new Configuration();
        executionContextList.add(context);
        triggerConfig.put("cronExpression", jRuleWhen.cron());
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenCronTrigger.class))
                .withConfiguration(triggerConfig);

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenTimeTrigger jRuleWhen, JRuleExecutionContext context) {
        Configuration triggerConfig = new Configuration();
        executionContextList.add(context);
        List<String> triggers = new ArrayList<>();

        if (jRuleWhen.hours() != -1)
            triggers.add("hours=" + jRuleWhen.hours());
        if (jRuleWhen.minutes() != -1)
            triggers.add("minutes=" + jRuleWhen.minutes());
        if (jRuleWhen.seconds() != -1)
            triggers.add("seconds=" + jRuleWhen.seconds());
        triggerConfig.put("cronExpression", String.join(",", triggers));
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenTimeTrigger.class))
                .withConfiguration(triggerConfig);

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenAnnotation(JRuleWhenThingTrigger jRuleWhen, JRuleExecutionContext context) {
        Configuration triggerConfig = new Configuration();
        executionContextList.add(context);
        triggerConfig.put("thingUID", jRuleWhen.thing());
        if (jRuleWhen.to() != null)
            triggerConfig.put("status", jRuleWhen.to());
        if (jRuleWhen.from() != null)
            triggerConfig.put("previousStatus", jRuleWhen.from());

        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenThingTrigger.class))
                .withConfiguration(triggerConfig);

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addTags(String[] loggingTags) {
        tags.addAll(Arrays.stream(loggingTags).collect(Collectors.toSet()));
    }

    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        return "not_executed";
    }
}
