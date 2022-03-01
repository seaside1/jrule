/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.automation.jrule.rules;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.handler.JRuleActionHandler;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.handler.JRuleTransformationHandler;
import org.openhab.automation.jrule.internal.handler.JRuleVoiceHandler;
import org.openhab.automation.jrule.items.JRulePercentType;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.core.transform.TransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRule} .
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRule {

    private static final Logger logger = LoggerFactory.getLogger(JRule.class);

    private final static Map<String, CompletableFuture<Void>> ruleNameToCompletableFuture = new HashMap<>();
    private final static Map<String, List<CompletableFuture<Void>>> ruleNameToCompletableFutureList = new HashMap<>();

    private volatile String logName = "";

    public JRule() {
        JRuleEngine.get().add(this);
    }

    protected synchronized boolean isTimerRunning(String ruleName) {
        final CompletableFuture<Void> completableFuture = ruleNameToCompletableFuture.get(ruleName);
        return completableFuture != null && completableFuture.isDone();
    }

    protected synchronized CompletableFuture<Void> createOrReplaceTimer(String ruleName, long timeInSeconds,
            Consumer<Void> fn) {
        CompletableFuture<Void> future = ruleNameToCompletableFuture.get(ruleName);
        if (future != null) {
            JRuleLog.debug(logger, ruleName, "Future already running");
            boolean cancelled = future.cancel(false);
            ruleNameToCompletableFuture.remove(ruleName);
            JRuleLog.info(logger, ruleName, "Replacing existing timer by removing old timer cancelled: {}", cancelled);
        }
        return createTimer(ruleName, timeInSeconds, fn);
    }

    protected boolean timerIsRunning(String ruleName) {
        return ruleNameToCompletableFuture.containsKey(ruleName)
                || ruleNameToCompletableFutureList.containsKey(ruleName);
    }

    protected synchronized boolean cancelTimer(String ruleName) {
        boolean cancelled = false;
        final CompletableFuture<Void> completableFuture = ruleNameToCompletableFuture.get(ruleName);
        if (completableFuture != null) {
            JRuleLog.debug(logger, ruleName, "Future already running");
            try {
                cancelled = completableFuture.cancel(false);
            } catch (Exception x) {
                JRuleLog.debug(logger, ruleName, "Failed to cancel timer", x);
            } finally {
                ruleNameToCompletableFuture.remove(ruleName);
            }
        }
        return cancelled;
    }

    protected synchronized CompletableFuture<Void> createTimer(String ruleName, int timeInSeconds, Consumer<Void> fn) {
        return createTimer(ruleName, (long) timeInSeconds, fn);
    }

    protected synchronized CompletableFuture<Void> createTimer(String ruleName, long timeInSeconds, Consumer<Void> fn) {
        if (ruleNameToCompletableFuture.get(ruleName) != null) {
            JRuleLog.debug(logger, ruleName, "Future already running");
            return ruleNameToCompletableFuture.get(ruleName);
        }
        CompletableFuture<Void> future = JRuleUtil.delayedExecution(timeInSeconds, TimeUnit.SECONDS);
        ruleNameToCompletableFuture.put(ruleName, future);
        JRuleLog.info(logger, ruleName, "Start timer timeSeconds: {}", timeInSeconds);
        return future.thenAccept(fn).thenAccept(s -> {
            JRuleLog.info(logger, ruleName, "Timer has finsihed");
            ruleNameToCompletableFuture.remove(ruleName);
        });
    }

    protected synchronized List<CompletableFuture<Void>> createOrReplaceRepeatingTimer(String ruleName,
            int dealyInSeconds, int numberOfReapts, Consumer<Void> fn) {
        return createOrReplaceRepeatingTimer(ruleName, (long) dealyInSeconds, numberOfReapts, fn);
    }

    protected synchronized List<CompletableFuture<Void>> createOrReplaceRepeatingTimer(String ruleName,
            long delayInSeconds, int numberOfRepeats, Consumer<Void> fn) {
        List<CompletableFuture<Void>> completableFutures = ruleNameToCompletableFutureList.get(ruleName);
        if (completableFutures != null) {
            JRuleLog.debug(logger, ruleName, "Repeating Future already running");
            completableFutures.forEach(cF -> cF.cancel(true));
            completableFutures.clear();
            ruleNameToCompletableFutureList.remove(ruleName);
            JRuleLog.info(logger, ruleName, "Replacing existing repeating timer by removing old timer");
        }
        return createRepeatingTimer(ruleName, delayInSeconds, numberOfRepeats, fn);
    }

    protected synchronized List<CompletableFuture<Void>> createRepeatingTimer(String ruleName, long delayInSeconds,
            int numberOfRepeats, Consumer<Void> fn) {
        List<CompletableFuture<Void>> futures = ruleNameToCompletableFutureList.get(ruleName);
        if (futures != null) {
            JRuleLog.debug(logger, ruleName, "Repeating timer already running");
            return ruleNameToCompletableFutureList.get(ruleName);
        }
        JRuleLog.info(logger, ruleName, "Start Repeating timer, delay: {}s repeats: {}", delayInSeconds,
                numberOfRepeats);

        futures = new ArrayList<>();
        ruleNameToCompletableFutureList.put(ruleName, futures);
        CompletableFuture<Void> lastFuture = null;
        for (int i = 0; i < numberOfRepeats; i++) {
            lastFuture = JRuleUtil.delayedExecution(delayInSeconds * i, TimeUnit.SECONDS);
            futures.add(lastFuture);
        }
        futures.forEach(f -> f.thenAccept(fn));
        if (lastFuture != null) {
            futures.add(lastFuture.thenAccept(s -> {
                JRuleLog.info(logger, ruleName, "Repeating Timer has finsihed");
                List<CompletableFuture<Void>> finishedList = ruleNameToCompletableFutureList.remove(ruleName);
                if (finishedList != null) {
                    finishedList.clear();
                }
            }));
        }
        return futures;
    }

    protected synchronized List<CompletableFuture<Void>> createRepeatingTimer(String ruleName, int delayInSeconds,
            int numberOfRepeats, Consumer<Void> fn) {
        return createRepeatingTimer(ruleName, (long) delayInSeconds, numberOfRepeats, fn);
    }

    protected void say(String text) {
        JRuleVoiceHandler.get().say(text);
    }

    protected String transform(String stateDescPattern, String state) throws TransformationException {
        return JRuleTransformationHandler.get().transform(stateDescPattern, state);
    }

    protected void executeCommandLine(String... commandLine) {
        JRuleActionHandler.get().executeCommandLine(commandLine);
    }

    protected String executeCommandLineAndAwaitResponse(long delayInSeconds, String... commandLine) {
        return JRuleActionHandler.get().executeCommandAndAwaitResponse(delayInSeconds, commandLine);
    }

    protected void say(String text, String voiceId, String sinkId) {
        JRuleVoiceHandler.get().say(text, voiceId, sinkId);
    }

    protected void sendCommand(String itemName, JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    protected void sendCommand(String itemName, JRulePercentType percentTypeCommand) {
        JRuleEventHandler.get().sendCommand(itemName, percentTypeCommand);
    }

    protected void sendCommand(String itemName, String command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    protected void sendCommand(String itemName, double value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    protected void sendCommand(String itemName, int value) {
        JRuleEventHandler.get().sendCommand(itemName, value);
    }

    protected void sendCommand(String itemName, Date date) {
        JRuleEventHandler.get().sendCommand(itemName, date);
    }

    protected void postUpdate(String itemName, Date date) {
        JRuleEventHandler.get().postUpdate(itemName, date);
    }

    protected void postUpdate(String itemName, JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    protected void postUpdate(String itemName, String value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    protected void postUpdate(String itemName, double value) {
        JRuleEventHandler.get().postUpdate(itemName, value);
    }

    protected boolean getTimedLock(String ruleName, int seconds) {
        CompletableFuture<Void> future = ruleNameToCompletableFuture.get(ruleName);
        if (future != null) {
            return false;
        }
        Supplier<CompletableFuture<Void>> asyncTask = () -> CompletableFuture.completedFuture(null);
        future = JRuleUtil.scheduleAsync(asyncTask, seconds, TimeUnit.SECONDS);
        ruleNameToCompletableFuture.put(ruleName, future);
        future.thenAccept(itemName -> {
            JRuleLog.info(logger, ruleName, "Timer completed! Releasing lock");
            ruleNameToCompletableFuture.remove(ruleName);
        });
        return true;
    }

    protected int nowHour() {
        return Instant.now().atZone(ZoneId.systemDefault()).getHour();
    }

    protected int nowMinute() {
        return Instant.now().atZone(ZoneId.systemDefault()).getMinute();
    }

    protected int getIntValueOrDefault(Double doubleValue, int defaultValue) {
        return doubleValue == null ? defaultValue : doubleValue.intValue();
    }

    public void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, getRuleLogName(), message, parameters);
    }

    public void logInfo(String message, Object... parameters) {
        JRuleLog.info(logger, getRuleLogName(), message, parameters);
    }

    public void logError(String message, Object... parameters) {
        JRuleLog.error(logger, getRuleLogName(), message, parameters);
    }

    public void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, getRuleLogName(), message, parameters);
    }

    protected String getRuleLogName() {
        return logName;
    }

    public void setRuleLogName(String logName) {
        this.logName = logName;
    }
}
