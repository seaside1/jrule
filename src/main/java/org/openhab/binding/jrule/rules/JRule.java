/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.jrule.rules;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.openhab.binding.jrule.internal.JRuleUtil;
import org.openhab.binding.jrule.internal.handler.JRuleEngine;
import org.openhab.binding.jrule.internal.handler.JRuleVoiceHandler;
import org.openhab.core.common.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRule} .
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRule {

    private final Logger logger = LoggerFactory.getLogger(JRule.class);

    // TODO: Static or not...
    private final Map<String, CompletableFuture<Void>> ruleNameToCompletableFuture = new HashMap<>();

    protected final ScheduledExecutorService scheduler = ThreadPoolManager
            .getScheduledPool(ThreadPoolManager.THREAD_POOL_NAME_COMMON);

    public JRule() {
        JRuleEngine.get().add(this);
    }

    protected synchronized boolean isTimerRunning(String ruleName) {
        final CompletableFuture<Void> completableFuture = ruleNameToCompletableFuture.get(ruleName);
        return completableFuture == null ? false : completableFuture.isDone();
    }

    protected synchronized CompletableFuture<Void> createOrRescheduleTimer(String ruleName, int timeInSeconds,
            Consumer<Void> fn) {
        if (ruleNameToCompletableFuture.get(ruleName) != null) {
            logger.debug("Future already running for ruleName: {}", ruleName);
            CompletableFuture<Void> completableFuture = ruleNameToCompletableFuture.get(ruleName);
            boolean cancelled = completableFuture.cancel(false);
            ruleNameToCompletableFuture.remove(completableFuture);
            logger.debug("Rescheduling existing timer by removing old timer: {}", cancelled);
        }
        return createTimer(ruleName, timeInSeconds, fn);
    }

    protected synchronized CompletableFuture<Void> createTimer(String ruleName, int timeInSeconds, Consumer<Void> fn) {
        if (ruleNameToCompletableFuture.get(ruleName) != null) {
            logger.debug("Future already running for ruleName: {}", ruleName);
            return ruleNameToCompletableFuture.get(ruleName);
        }
        Executor delayedExecutor = CompletableFuture.delayedExecutor(timeInSeconds, TimeUnit.SECONDS, scheduler);
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> null, delayedExecutor);
        ruleNameToCompletableFuture.put(ruleName, future);
        logger.debug("Start timer for rule: {}, timeSeconds: {}", ruleName, timeInSeconds);
        return future.thenAccept(fn).thenAccept(s -> {
            logger.debug("Removing future for rule: {}", ruleName);
            ruleNameToCompletableFuture.remove(ruleName);
        });
    }

    protected void say(String text) {
        JRuleVoiceHandler.get().say(text);
    }

    protected void say(String text, String voiceId, String sinkId) {
        JRuleVoiceHandler.get().say(text, voiceId, sinkId);
    }

    protected boolean getTimedLock(String ruleName, int seconds) {
        CompletableFuture<Void> future = ruleNameToCompletableFuture.get(ruleName);
        if (future != null) {
            return false;
        }
        Supplier<CompletableFuture<Void>> asyncTask = () -> CompletableFuture.completedFuture(null);
        future = JRuleUtil.scheduleAsync(scheduler, asyncTask, seconds, TimeUnit.SECONDS);
        ruleNameToCompletableFuture.put(ruleName, future);
        future.thenAccept(itemName -> {
            logger.debug("Timer completed! Releasing lock");
            ruleNameToCompletableFuture.remove(ruleName);
        });
        return true;
    }

    protected int nowHour() {
        return Instant.now().atZone(ZoneOffset.UTC).getHour();
    }

    protected int nowMinute() {
        return Instant.now().atZone(ZoneOffset.UTC).getMinute();
    }

    protected int getIntValueOrDefault(Double doubleValue, int defaultValue) {
        return doubleValue == null ? defaultValue : doubleValue.intValue();
    }
}
