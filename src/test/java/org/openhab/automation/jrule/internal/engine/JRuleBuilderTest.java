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

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.engine.JRuleBuilder.Condition;
import org.openhab.automation.jrule.internal.engine.excutioncontext.*;
import org.openhab.automation.jrule.internal.module.JRuleRuleProvider;
import org.openhab.automation.jrule.internal.rules.JRuleAbstractTest;
import org.openhab.automation.jrule.rules.JRuleMemberOf;
import org.openhab.automation.jrule.things.JRuleThingStatus;
import org.openhab.core.scheduler.CronScheduler;

/**
 * The {@link JRuleBuilderTest}
 *
 *
 * @author Rüdiger Sopp - Initial contribution
 */
public class JRuleBuilderTest extends JRuleAbstractTest {

    JRuleEngine jRuleEngine;
    JRuleRuleProvider ruleProvider;
    CronScheduler cronScheduler;
    JRuleInvocationCallback invocationCallback = event -> {
    };

    JRuleBuilder jRuleBuilder;

    Pattern UUID_REGEX = Pattern
            .compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    @BeforeEach
    public void setup() {
        jRuleEngine = Mockito.spy(JRuleEngine.class);
        ruleProvider = Mockito.mock(JRuleRuleProvider.class);
        cronScheduler = Mockito.mock(CronScheduler.class);
        jRuleEngine.setRuleProvider(ruleProvider);
        jRuleEngine.setCronScheduler(cronScheduler);

        jRuleBuilder = new JRuleBuilder(jRuleEngine, "ruleName", invocationCallback);
    }

    @Test
    void testEmptyRule() {

        boolean result = jRuleBuilder.build();

        Assertions.assertFalse(result);
        verify(jRuleEngine, never()).addToContext(null, false);
        verify(ruleProvider, times(1)).add(argThat(entry -> UUID_REGEX.matcher(entry.getUID()).matches()
                && "ruleName".equals(entry.getName()) && entry.getTriggers().isEmpty()));
    }

    @Test
    void testUid() {

        boolean result = jRuleBuilder.uid("uid").build();

        Assertions.assertFalse(result);
        verify(jRuleEngine, never()).addToContext(null, false);
        verify(ruleProvider, times(1)).add(argThat(entry -> "uid".equals(entry.getUID())
                && "ruleName".equals(entry.getName()) && entry.getTriggers().isEmpty()));
    }

    @Test
    public void testDefaultProperties() {

        boolean result = jRuleBuilder.uid("uid").whenItemReceivedCommand("item", null, null, null).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(argThat(context -> context != null && !context.isEnabled()
                && invocationCallback.equals(context.getInvocationCallback()) && "ruleName".equals(context.getLogName())
                && context.getLoggingTags() == JRuleEngine.EMPTY_LOG_TAGS && context.getTimedLock() == null
                && context.getPreconditionContextList().isEmpty() && context.getDelayed() == null), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testPropertyEnabled() {

        boolean result = jRuleBuilder.uid("uid").whenItemReceivedCommand("item", null, null, null).enableRule(true)
                .build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(argThat(context -> context != null && context.isEnabled()
                && "ruleName".equals(context.getLogName()) && context.getLoggingTags() == JRuleEngine.EMPTY_LOG_TAGS
                && context.getTimedLock() == null && context.getDelayed() == null), eq(true));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testPropertyLogName() {

        boolean result = jRuleBuilder.uid("uid").whenItemReceivedCommand("item", null, null, null).logName("logName")
                .build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(argThat(context -> context != null && !context.isEnabled()
                && "logName".equals(context.getLogName()) && context.getLoggingTags() == JRuleEngine.EMPTY_LOG_TAGS
                && context.getTimedLock() == null && context.getDelayed() == null), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testPropertyLoggingTags() {

        String[] loggingTags = new String[] { "A", "B", "C" };

        boolean result = jRuleBuilder.uid("uid").whenItemReceivedCommand("item", null, null, null)
                .loggingTags(loggingTags).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(argThat(context -> context != null && !context.isEnabled()
                && "ruleName".equals(context.getLogName()) && context.getLoggingTags() == loggingTags
                && context.getTimedLock() == null && context.getDelayed() == null), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testPropertyTimedLock() {

        boolean result = jRuleBuilder.uid("uid").whenItemReceivedCommand("item", null, null, null)
                .timedLock(Duration.ofSeconds(2)).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(
                argThat(context -> context != null && !context.isEnabled() && "ruleName".equals(context.getLogName())
                        && context.getLoggingTags() == JRuleEngine.EMPTY_LOG_TAGS
                        && Duration.ofSeconds(2).equals(context.getTimedLock()) && context.getDelayed() == null),
                eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testPropertyDelayed() {

        boolean result = jRuleBuilder.uid("uid").whenItemReceivedCommand("item", null, null, null)
                .delayed(Duration.ofSeconds(3)).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(argThat(context -> context != null && !context.isEnabled()
                && "ruleName".equals(context.getLogName()) && context.getLoggingTags() == JRuleEngine.EMPTY_LOG_TAGS
                && context.getTimedLock() == null && Duration.ofSeconds(3).equals(context.getDelayed())), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testPreconditions() {

        boolean result = jRuleBuilder.uid("uid")
                .preCondition("empty", new Condition(null, null, null, null, null, null))
                .preCondition("abc", new Condition(0.1, 0.2, 0.3, 0.4, "e", "n"))
                .preCondition("def", new Condition(0.5, 0.6, 0.7, 0.8, "1", "2"))
                .whenItemReceivedCommand("item", null, null, null).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(
                argThat(context -> context != null && !context.isEnabled() && "ruleName".equals(context.getLogName())
                        && context.getLoggingTags() == JRuleEngine.EMPTY_LOG_TAGS && context.getTimedLock() == null
                        && context.getDelayed() == null && context.getPreconditionContextList().size() == 3
                        && context.getPreconditionContextList().stream()
                                .anyMatch(c -> c.getItem().equals("empty") && c.getLt().isEmpty()
                                        && c.getLte().isEmpty() && c.getGt().isEmpty() && c.getGte().isEmpty()
                                        && c.getEq().isEmpty() && c.getNeq().isEmpty())
                        && context.getPreconditionContextList().stream()
                                .anyMatch(c -> c.getItem().equals("abc") && c.getLt().orElse(0.0).equals(0.1)
                                        && c.getLte().orElse(0.0).equals(0.2) && c.getGt().orElse(0.0).equals(0.3)
                                        && c.getGte().orElse(0.0).equals(0.4) && c.getEq().orElse("").equals("e")
                                        && c.getNeq().orElse("").equals("n"))
                        && context.getPreconditionContextList().stream()
                                .anyMatch(c -> c.getItem().equals("def") && c.getLt().orElse(0.0).equals(0.5)
                                        && c.getLte().orElse(0.0).equals(0.6) && c.getGt().orElse(0.0).equals(0.7)
                                        && c.getGte().orElse(0.0).equals(0.8) && c.getEq().orElse("").equals("1")
                                        && c.getNeq().orElse("").equals("2"))),
                eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testWhenStartupTrigger() {

        boolean result = jRuleBuilder.whenStartupTrigger(10).whenStartupTrigger(30).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(2)).addToContext(isA(JRuleStartupExecutionContext.class), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 2));
    }

    @Test
    public void testWhenThingTrigger() {

        boolean result = jRuleBuilder.whenThingTrigger("thing", null, null)
                .whenThingTrigger("thing", JRuleThingStatus.INITIALIZING, JRuleThingStatus.ONLINE).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(2)).addToContext(isA(JRuleThingExecutionContext.class), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 2));
    }

    @Test
    public void testWhenChannelTrigger() {

        boolean result = jRuleBuilder.whenChannelTrigger("channel1", null).whenChannelTrigger("channel2", "event")
                .build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1))
                .addToContext(argThat(context -> context instanceof JRuleChannelExecutionContext channelExecutionContext
                        && "channel1".equals(channelExecutionContext.getChannel())
                        && channelExecutionContext.getEvent().isEmpty()), eq(false));
        verify(jRuleEngine, times(1))
                .addToContext(argThat(context -> context instanceof JRuleChannelExecutionContext channelExecutionContext
                        && "channel2".equals(channelExecutionContext.getChannel())
                        && "event".equals(channelExecutionContext.getEvent().orElse(null))), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 2));
    }

    @Test
    public void testWhenItemReceivedCommand() {

        boolean result = jRuleBuilder.whenItemReceivedCommand("item", JRuleMemberOf.None, null, null)
                .whenItemReceivedCommand("item", JRuleMemberOf.All, "command", null).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(2)).addToContext(isA(JRuleItemReceivedCommandExecutionContext.class), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 2));
    }

    @Test
    public void testWhenItemChanged() {

        boolean result = jRuleBuilder.whenItemChange("item", JRuleMemberOf.None, null, "to", null, null)
                .whenItemChange("item", JRuleMemberOf.All, "from", null, null, null).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(2)).addToContext(isA(JRuleItemChangeExecutionContext.class), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 2));
    }

    @Test
    public void testWhenItemReceivedUpdate() {

        boolean result = jRuleBuilder.whenItemReceivedUpdate("item", JRuleMemberOf.None, "state", null)
                .whenItemReceivedUpdate("item", JRuleMemberOf.All, "command", null).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(2)).addToContext(isA(JRuleItemReceivedUpdateExecutionContext.class), eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 2));
    }

    @Test
    public void testWhenCronTrigger() {

        boolean result = jRuleBuilder.whenCronTrigger("abcde").build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(
                argThat(context -> context instanceof JRuleTimedCronExecutionContext timedCronExecutionContext
                        && timedCronExecutionContext.getCron().equals("abcde")),
                eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 1));
    }

    @Test
    public void testWhenTimeTrigger() {

        boolean result = jRuleBuilder.whenTimeTrigger(null, null, null).whenTimeTrigger(12, 34, 56).build();

        Assertions.assertTrue(result);
        verify(jRuleEngine, times(1)).addToContext(
                argThat(context -> context instanceof JRuleTimeTimerExecutionContext timeTimerExecutionContext
                        && timeTimerExecutionContext.getHour().isEmpty()
                        && timeTimerExecutionContext.getMinute().isEmpty()
                        && timeTimerExecutionContext.getSecond().isEmpty()),
                eq(false));
        verify(jRuleEngine, times(1)).addToContext(
                argThat(context -> context instanceof JRuleTimeTimerExecutionContext timeTimerExecutionContext
                        && timeTimerExecutionContext.getHour().orElse(0) == 12
                        && timeTimerExecutionContext.getMinute().orElse(0) == 34
                        && timeTimerExecutionContext.getSecond().orElse(0) == 56),
                eq(false));
        verify(ruleProvider, times(1)).add(argThat(entry -> entry.getTriggers().size() == 2));
    }

    @Test
    public void testCondition() {
        Condition condition = new Condition(0.1, 0.2, 0.3, 0.4, "eq", "neq");

        Assertions.assertEquals(0.1, condition.lt());
        Assertions.assertEquals(0.2, condition.lte());
        Assertions.assertEquals(0.3, condition.gt());
        Assertions.assertEquals(0.4, condition.gte());
        Assertions.assertEquals("eq", condition.eq());
        Assertions.assertEquals("neq", condition.neq());
    }
}
