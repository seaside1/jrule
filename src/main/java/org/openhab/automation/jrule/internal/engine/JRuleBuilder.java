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
package org.openhab.automation.jrule.internal.engine;

import static org.openhab.automation.jrule.internal.engine.JRuleEngine.EMPTY_LOG_TAGS;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleChannelExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemChangeExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemReceivedCommandExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemReceivedUpdateExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRulePreconditionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleStartupExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleThingExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleTimeTimerExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleTimedCronExecutionContext;
import org.openhab.automation.jrule.internal.module.JRuleModuleEntry;
import org.openhab.automation.jrule.rules.JRuleMemberOf;
import org.openhab.automation.jrule.things.JRuleThingStatus;

/**
 * The {@link JRuleBuilder}
 *
 * @author Rüdiger Sopp - Initial contribution
 */
public class JRuleBuilder {

    private final JRuleEngine jRuleEngine;

    private final JRuleInvocationCallback invocationCallback;
    private final String ruleName;
    private boolean enableRule = false;
    private String uid = null;
    private String logName = null;
    private String[] loggingTags = EMPTY_LOG_TAGS;
    private Duration timedLock = null;
    private Duration delayed = null;

    private final List<PreCondition> preConditions = new ArrayList<>();

    private final List<WhenStartupTrigger> whenStartupTriggers = new ArrayList<>();
    private final List<WhenThingTrigger> whenThingTriggers = new ArrayList<>();
    private final List<WhenChannelTrigger> whenChannelTriggers = new ArrayList<>();
    private final List<WhenItemReceivedCommand> whenItemReceivedCommandTriggers = new ArrayList<>();
    private final List<WhenItemChanged> whenItemChangedTriggers = new ArrayList<>();
    private final List<WhenItemReceivedUpdate> whenItemReceivedUpdateTriggers = new ArrayList<>();
    private final List<WhenCronTrigger> whenCronTriggers = new ArrayList<>();
    private final List<WhenTimeTrigger> whenTimeTriggers = new ArrayList<>();

    JRuleBuilder(JRuleEngine jRuleEngine, String ruleName, JRuleInvocationCallback invocationCallback) {
        this.jRuleEngine = jRuleEngine;
        this.ruleName = ruleName;
        this.invocationCallback = invocationCallback;
    }

    public JRuleBuilder enableRule(boolean enableRule) {
        this.enableRule = enableRule;
        return this;
    }

    public JRuleBuilder uid(String uid) {
        this.uid = uid;
        return this;
    }

    public JRuleBuilder logName(String logName) {
        this.logName = logName;
        return this;
    }

    public JRuleBuilder loggingTags(String[] loggingTags) {
        this.loggingTags = loggingTags;
        return this;
    }

    public JRuleBuilder timedLock(Duration timedLock) {
        this.timedLock = timedLock;
        return this;
    }

    public JRuleBuilder delayed(Duration delayed) {
        this.delayed = delayed;
        return this;
    }

    public JRuleBuilder preCondition(String itemName, Condition condition) {
        preConditions.add(new PreCondition(itemName, condition));
        return this;
    }

    public JRuleBuilder whenStartupTrigger(int level) {
        whenStartupTriggers.add(new WhenStartupTrigger(level));
        return this;
    }

    public JRuleBuilder whenThingTrigger(String thingName, @Nullable JRuleThingStatus from,
            @Nullable JRuleThingStatus to) {
        whenThingTriggers.add(new WhenThingTrigger(thingName, from, to));
        return this;
    }

    public JRuleBuilder whenChannelTrigger(String channelName, @Nullable String event) {
        whenChannelTriggers.add(new WhenChannelTrigger(channelName, event));
        return this;
    }

    public JRuleBuilder whenItemReceivedCommand(String itemName, JRuleMemberOf memberOf, @Nullable String command,
            @Nullable Condition condition) {
        whenItemReceivedCommandTriggers.add(new WhenItemReceivedCommand(itemName, memberOf, command, condition));
        return this;
    }

    public JRuleBuilder whenItemChange(String itemName, JRuleMemberOf memberOf, @Nullable String from,
            @Nullable String to, @Nullable Condition previousCondition, @Nullable Condition condition) {
        whenItemChangedTriggers.add(new WhenItemChanged(itemName, memberOf, from, to, previousCondition, condition));
        return this;
    }

    public JRuleBuilder whenItemReceivedUpdate(String itemName, JRuleMemberOf memberOf, @Nullable String state,
            @Nullable Condition condition) {
        whenItemReceivedUpdateTriggers.add(new WhenItemReceivedUpdate(itemName, memberOf, state, condition));
        return this;
    }

    public JRuleBuilder whenCronTrigger(String cron) {
        whenCronTriggers.add(new WhenCronTrigger(cron));
        return this;
    }

    public JRuleBuilder whenTimeTrigger(@Nullable Integer hour, @Nullable Integer minute, @Nullable Integer second) {
        whenTimeTriggers.add(new WhenTimeTrigger(hour, minute, second));
        return this;
    }

    public boolean build() {
        if (uid == null) {
            uid = UUID.randomUUID().toString();
        }

        if (StringUtils.isEmpty(logName)) {
            logName = ruleName;
        }

        final JRuleModuleEntry ruleModuleEntry = new JRuleModuleEntry(uid, ruleName);
        ruleModuleEntry.addTags(loggingTags);

        AtomicBoolean addedToContext = new AtomicBoolean(false);

        List<JRulePreconditionContext> preconditionContexts = preConditions.stream()
                .map(data -> new JRulePreconditionContext(data.itemName, Optional.ofNullable(data.condition.lt),
                        Optional.ofNullable(data.condition.lte), Optional.ofNullable(data.condition.gt),
                        Optional.ofNullable(data.condition.gte), Optional.ofNullable(data.condition.eq),
                        Optional.ofNullable(data.condition.neq)))
                .toList();

        whenStartupTriggers.forEach(data -> {
            JRuleStartupExecutionContext context = new JRuleStartupExecutionContext(uid, logName, loggingTags,
                    invocationCallback, preconditionContexts, timedLock, delayed, data.level);
            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addStartupTrigger();
            ruleModuleEntry.addJRuleWhenStartupTrigger(context);
            addedToContext.set(true);
        });

        whenThingTriggers.forEach(data -> {
            JRuleThingExecutionContext context = new JRuleThingExecutionContext(uid, logName, loggingTags,
                    invocationCallback, Optional.ofNullable(data.thingName), Optional.ofNullable(data.from),
                    Optional.ofNullable(data.to), preconditionContexts, timedLock, delayed);
            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addThingTrigger();
            ruleModuleEntry.addJRuleWhenThingTrigger(context);
            addedToContext.set(true);
        });

        whenChannelTriggers.forEach(data -> {
            JRuleChannelExecutionContext context = new JRuleChannelExecutionContext(uid, logName, loggingTags,
                    invocationCallback, preconditionContexts, data.channelName, Optional.ofNullable(data.event),
                    timedLock, delayed);
            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addChannelTrigger();
            ruleModuleEntry.addJRuleWhenChannelTrigger(context);
            addedToContext.set(true);
        });

        whenItemReceivedCommandTriggers.forEach(data -> {
            JRuleItemReceivedCommandExecutionContext context = new JRuleItemReceivedCommandExecutionContext(uid,
                    logName, loggingTags, invocationCallback, data.itemName, data.memberOf,
                    Optional.ofNullable(data.condition).map(Condition::toJRuleConditionContext), preconditionContexts,
                    Optional.ofNullable(data.command), timedLock, delayed);

            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addItemStateTrigger();
            ruleModuleEntry.addJRuleWhenItemReceivedCommand(context);
            addedToContext.set(true);
        });

        whenItemChangedTriggers.forEach(data -> {
            JRuleItemChangeExecutionContext context = new JRuleItemChangeExecutionContext(uid, logName, loggingTags,
                    invocationCallback, data.itemName, data.memberOf,
                    Optional.ofNullable(data.condition).map(Condition::toJRuleConditionContext),
                    Optional.ofNullable(data.previousCondition).map(Condition::toJRuleConditionContext),
                    preconditionContexts, Optional.ofNullable(data.from), Optional.ofNullable(data.to), timedLock,
                    delayed);

            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addItemStateTrigger();
            ruleModuleEntry.addJRuleWhenItemChange(context);
            addedToContext.set(true);
        });

        whenItemReceivedUpdateTriggers.forEach(data -> {
            JRuleItemReceivedUpdateExecutionContext context = new JRuleItemReceivedUpdateExecutionContext(uid, logName,
                    loggingTags, invocationCallback, data.itemName, data.memberOf,
                    Optional.ofNullable(data.condition).map(Condition::toJRuleConditionContext), preconditionContexts,
                    Optional.ofNullable(data.state), timedLock, delayed);

            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addItemStateTrigger();
            ruleModuleEntry.addJRuleWhenItemReceivedUpdate(context);
            addedToContext.set(true);
        });

        whenCronTriggers.forEach(data -> {
            JRuleTimedCronExecutionContext context = new JRuleTimedCronExecutionContext(uid, logName, loggingTags,
                    invocationCallback, preconditionContexts, data.cron);

            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addTimedTrigger();
            ruleModuleEntry.addJRuleWhenCronTrigger(context);
            addedToContext.set(true);
        });

        whenTimeTriggers.forEach(data -> {
            JRuleTimeTimerExecutionContext context = new JRuleTimeTimerExecutionContext(uid, logName, loggingTags,
                    invocationCallback, preconditionContexts, Optional.ofNullable(data.hour),
                    Optional.ofNullable(data.minute), Optional.ofNullable(data.second));
            jRuleEngine.addToContext(context, enableRule);
            jRuleEngine.ruleLoadingStatistics.addTimedTrigger();
            ruleModuleEntry.addJRuleWhenTimeTrigger(context);
            addedToContext.set(true);
        });

        jRuleEngine.ruleProvider.add(ruleModuleEntry);

        return addedToContext.get();
    }

    public record Condition(@Nullable Double lt, @Nullable Double lte, @Nullable Double gt, @Nullable Double gte,
            @Nullable String eq, @Nullable String neq) {

        private JRuleItemExecutionContext.JRuleConditionContext toJRuleConditionContext() {
            return new JRuleItemExecutionContext.JRuleConditionContext(Optional.ofNullable(gt),
                    Optional.ofNullable(gte), Optional.ofNullable(lt), Optional.ofNullable(lte),
                    Optional.ofNullable(eq), Optional.ofNullable(neq));
        }
    }

    private record PreCondition(String itemName, Condition condition) {
    }

    private record WhenStartupTrigger(int level) {
    }

    private record WhenThingTrigger(String thingName, JRuleThingStatus from, JRuleThingStatus to) {
    }

    private record WhenChannelTrigger(String channelName, String event) {
    }

    private record WhenItemReceivedCommand(String itemName, JRuleMemberOf memberOf, String command,
            Condition condition) {
    }

    private record WhenItemChanged(String itemName, JRuleMemberOf memberOf, String from, String to,
            Condition previousCondition, Condition condition) {
    }

    private record WhenItemReceivedUpdate(String itemName, JRuleMemberOf memberOf, String state, Condition condition) {
    }

    private record WhenCronTrigger(String cron) {
    }

    private record WhenTimeTrigger(Integer hour, Integer minute, Integer second) {
    }
}
