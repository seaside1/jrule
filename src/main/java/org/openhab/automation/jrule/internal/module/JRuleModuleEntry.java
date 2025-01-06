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
package org.openhab.automation.jrule.internal.module;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleStartupExecutionContext;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRuleWhenStartup;
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

    public JRuleModuleEntry(String uid, String ruleName) {
        this.uid = uid;
        tags.add("JRule");
        setTriggers(ruleTriggers);
        setConditions(List.of());
        setConfiguration(new Configuration());
        setConfigurationDescriptions(List.of());
        setName(ruleName);
        setActions(List.of());
        setTags(tags);
    }

    public static String createUid(JRule jRule, Method method) {
        return jRule.getClass().getCanonicalName().replace("org.openhab.automation.jrule.rules.user.", "") + "."
                + method.getName();
    }

    public void dispose() {
        executionContextList.clear();
    }

    /**
     * Called from framework when a rule has been enabled in the user interface
     */
    public void ruleEnabled() {
        if (!enabled) {
            JRuleEngine.get().getRuleLoadingStatistics().addEnabledRule();
            logger.debug("Enabling rule '{}' / {}", getName(), uid);
        }
        executionContextList.forEach(e -> e.setEnabled(true));
        enabled = true;
    }

    /**
     * Called from the framework when a rule has been disabled in the user interface
     */
    public void ruleDisabled() {
        if (enabled) {
            logger.debug("Disabling rule '{}' / {}", getName(), uid);
        }
        executionContextList.forEach(e -> e.setEnabled(false));
        enabled = false;
    }

    public void addJRuleWhenItemReceivedUpdate(JRuleExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedUpdate.class));
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenItemReceivedCommand(JRuleExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedCommand.class));
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenItemChange(JRuleExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemChange.class));
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenChannelTrigger(JRuleExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenChannelTrigger.class));
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenCronTrigger(JRuleExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenCronTrigger.class));

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenTimeTrigger(JRuleExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenTimeTrigger.class));
        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenStartupTrigger(JRuleExecutionContext context) {
        executionContextList.add(context);
        // TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
        // .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenTimeTrigger.class));
        // ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenThingTrigger(JRuleExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenThingTrigger.class));

        ruleTriggers.add(triggerBuilder.build());
    }

    public void addJRuleWhenStartupTrigger(JRuleStartupExecutionContext context) {
        executionContextList.add(context);
        TriggerBuilder triggerBuilder = TriggerBuilder.create().withId("" + (triggerCounter++))
                .withTypeUID(JRuleModuleUtil.toTriggerModuleUID(JRuleWhenStartup.class));

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
