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

import org.openhab.automation.jrule.exception.JRuleExecutionException;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleLocalTimerExecutionContext;
import org.openhab.automation.jrule.internal.handler.JRuleActionHandler;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.handler.JRuleTransformationHandler;
import org.openhab.automation.jrule.internal.handler.JRuleVoiceHandler;
import org.openhab.automation.jrule.items.JRulePercentType;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRule} .
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRule {

    private static final Logger logger = LoggerFactory.getLogger(JRule.class);

    private final static Map<String, CompletableFuture<Void>> timerNameToLockFuture = new HashMap<>();
    private final static Map<String, CompletableFuture<Void>> timerNameToTimerFuture = new HashMap<>();
    private final static Map<String, List<CompletableFuture<Void>>> timerNameToTimerFutureList = new HashMap<>();

    public static final ThreadLocal<JRuleExecutionContext> JRULE_EXECUTION_CONTEXT = new ThreadLocal<>();

    public JRule() {
        JRuleEngine.get().add(this);
    }

    protected synchronized boolean isTimerRunning(String timerName) {
        final CompletableFuture<Void> completableFuture = timerNameToTimerFuture.get(timerName);
        return completableFuture != null && (completableFuture.isDone() || completableFuture.isCancelled());
    }

    protected synchronized CompletableFuture<Void> createOrReplaceTimer(String timerName, long timeInSeconds,
            Consumer<Void> fn) {
        CompletableFuture<Void> future = timerNameToTimerFuture.get(timerName);
        if (future != null) {
            JRuleLog.debug(logger, timerName, "Future already running hashCode: {}", future.hashCode());
            boolean cancelled = future.cancel(false);
            timerNameToTimerFuture.remove(timerName);

            JRuleLog.info(logger, timerName, "Replacing existing timer by removing old timer cancelled: {}", cancelled);
        }
        return createTimer(timerName, timeInSeconds, fn);
    }

    protected boolean timerIsRunning(String timerName) {
        return timerNameToTimerFuture.containsKey(timerName) || timerNameToTimerFutureList.containsKey(timerName);
    }

    protected synchronized boolean cancelTimer(String timeName) {
        boolean cancelled = false;
        final CompletableFuture<Void> completableFuture = timerNameToTimerFuture.get(timeName);
        if (completableFuture != null) {
            JRuleLog.debug(logger, timeName, "Future already running");
            try {
                cancelled = completableFuture.cancel(false);
            } catch (Exception x) {
                JRuleLog.debug(logger, timeName, "Failed to cancel timer", x);
            } finally {
                timerNameToTimerFuture.remove(timeName);
            }
        }

        List<CompletableFuture<Void>> completableFutures = timerNameToTimerFutureList.get(timeName);
        if (completableFutures != null) {
            cancelled |= cancelListOfTimersFutures(timeName, completableFutures);
        }

        return cancelled;
    }

    protected synchronized CompletableFuture<Void> createTimer(String timerName, int timeInSeconds, Consumer<Void> fn) {
        return createTimer(timerName, (long) timeInSeconds, fn);
    }

    protected synchronized CompletableFuture<Void> createTimer(String timerName, long timeInSeconds,
            Consumer<Void> fn) {
        if (timerNameToTimerFuture.containsKey(timerName)) {
            JRuleLog.debug(logger, timerName, "Future already running hashCode: " + timerName.hashCode());
            return timerNameToTimerFuture.get(timerName);
        }
        JRuleLocalTimerExecutionContext context = new JRuleLocalTimerExecutionContext(JRULE_EXECUTION_CONTEXT.get(),
                timerName);
        CompletableFuture<Void> future = JRuleUtil.delayedExecution(timeInSeconds, TimeUnit.SECONDS);
        timerNameToTimerFuture.put(timerName, future);

        JRuleLog.info(logger, context.getLogName(), "Start timer timeSeconds: {} hashCode: {}", timeInSeconds,
                future.hashCode());
        return future.thenAccept(s -> {
            try {
                JRule.JRULE_EXECUTION_CONTEXT.set(context);
                JRuleLog.info(logger, context.getLogName(), "Timer has finsihed");
                JRuleLog.debug(logger, context.getLogName(), "Timer has finsihed hashCode: {}", future.hashCode());
                timerNameToTimerFuture.remove(timerName);
            } finally {
                JRule.JRULE_EXECUTION_CONTEXT.remove();
            }

        }).thenAccept(s -> {
            try {
                JRule.JRULE_EXECUTION_CONTEXT.set(context);
                fn.accept(null);
            } finally {
                JRule.JRULE_EXECUTION_CONTEXT.remove();
            }
        });
    }

    protected synchronized List<CompletableFuture<Void>> createOrReplaceRepeatingTimer(String timerName,
            int dealyInSeconds, int numberOfReapts, Consumer<Void> fn) {
        return createOrReplaceRepeatingTimer(timerName, (long) dealyInSeconds, numberOfReapts, fn);
    }

    protected synchronized List<CompletableFuture<Void>> createOrReplaceRepeatingTimer(String timerName,
            long delayInSeconds, int numberOfRepeats, Consumer<Void> fn) {
        List<CompletableFuture<Void>> completableFutures = timerNameToTimerFutureList.get(timerName);
        if (completableFutures != null) {
            cancelListOfTimersFutures(timerName, completableFutures);
            JRuleLog.info(logger, timerName, "Replacing existing repeating timer by removing old timer");
        }
        return createRepeatingTimer(timerName, delayInSeconds, numberOfRepeats, fn);
    }

    private boolean cancelListOfTimersFutures(String timerName, List<CompletableFuture<Void>> completableFutures) {
        boolean cancelled = false;
        JRuleLog.debug(logger, timerName, "Cancel Futures");
        for (CompletableFuture<?> future : completableFutures) {
            cancelled |= future.cancel(true);
        }
        completableFutures.clear();
        timerNameToTimerFutureList.remove(timerName);
        return cancelled;
    }

    protected synchronized List<CompletableFuture<Void>> createRepeatingTimer(String timerName, long delayInSeconds,
            int numberOfRepeats, Consumer<Void> fn) {
        List<CompletableFuture<Void>> futures = timerNameToTimerFutureList.get(timerName);

        if (futures != null) {
            JRuleLog.debug(logger, timerName, "Repeating timer already running");
            return timerNameToTimerFutureList.get(timerName);
        }
        JRuleLog.info(logger, timerName, "Start Repeating timer, delay: {}s repeats: {}", delayInSeconds,
                numberOfRepeats);

        futures = new ArrayList<>();
        timerNameToTimerFutureList.put(timerName, futures);
        CompletableFuture<Void> lastFuture = null;
        for (int i = 0; i < numberOfRepeats; i++) {
            lastFuture = JRuleUtil.delayedExecution(delayInSeconds * (i + 1), TimeUnit.SECONDS);
            futures.add(lastFuture);
        }

        JRuleLocalTimerExecutionContext context = new JRuleLocalTimerExecutionContext(JRULE_EXECUTION_CONTEXT.get(),
                timerName);

        futures.forEach(f -> {
            try {
                JRule.JRULE_EXECUTION_CONTEXT.set(context);
                f.thenAccept(fn);
            } finally {
                JRULE_EXECUTION_CONTEXT.remove();
            }
        });
        if (lastFuture != null) {
            futures.add(lastFuture.thenAccept(s -> {
                JRuleLog.info(logger, context.getLogName(), "Repeating Timer has finsihed");
                List<CompletableFuture<Void>> finishedList = timerNameToTimerFutureList.remove(timerName);
                if (finishedList != null) {
                    finishedList.clear();
                }
            }));
        }
        return futures;
    }

    protected synchronized List<CompletableFuture<Void>> createRepeatingTimer(String timerName, int delayInSeconds,
            int numberOfRepeats, Consumer<Void> fn) {
        return createRepeatingTimer(timerName, (long) delayInSeconds, numberOfRepeats, fn);
    }

    protected void say(String text) {
        JRuleVoiceHandler.get().say(text);
    }

    protected void say(String text, String voiceId, String sinkId, int volumePercent) {
        JRuleVoiceHandler.get().say(text, voiceId, sinkId, volumePercent);
    }

    protected void say(String text, int volume) {
        JRuleVoiceHandler.get().say(text, volume);
    }

    protected String transform(String stateDescPattern, String state) throws JRuleExecutionException {
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

    protected void sendCommand(String itemName, double value, String unit) {
        JRuleEventHandler.get().sendCommand(itemName, value, unit);
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

    protected boolean getTimedLock(String lockName, int seconds) {
        CompletableFuture<Void> future = timerNameToLockFuture.get(lockName);
        if (future != null) {
            return false;
        }
        Supplier<CompletableFuture<Void>> asyncTask = () -> CompletableFuture.completedFuture(null);
        JRuleLocalTimerExecutionContext context = new JRuleLocalTimerExecutionContext(JRULE_EXECUTION_CONTEXT.get(),
                lockName);
        future = JRuleUtil.scheduleAsync(asyncTask, seconds, TimeUnit.SECONDS, context);

        timerNameToLockFuture.put(lockName, future);
        future.thenAccept(itemName -> {
            JRuleLog.info(logger, context.getLogName(), "Timer completed! Releasing lock");
            timerNameToLockFuture.remove(lockName);
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
        JRuleExecutionContext context = JRULE_EXECUTION_CONTEXT.get();
        if (context != null) {
            return context.getLogName();
        } else {
            // Default value if context not set
            return this.getClass().getName();
        }
    }
}
