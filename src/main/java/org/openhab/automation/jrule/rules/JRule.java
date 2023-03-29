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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.handler.JRuleActionHandler;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.internal.handler.JRuleTransformationHandler;
import org.openhab.automation.jrule.internal.handler.JRuleVoiceHandler;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRule} .
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRule {

    private static final Logger logger = LoggerFactory.getLogger(JRule.class);

    public static final ThreadLocal<JRuleExecutionContext> JRULE_EXECUTION_CONTEXT = new ThreadLocal<>();

    public JRule() {
        // Add rules default disabled, let openHAB enable rules if not disabled in GUI
        JRuleEngine.get().add(this, false);
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

    protected String transform(String stateDescPattern, String state) throws JRuleRuntimeException {
        return JRuleTransformationHandler.get().transform(stateDescPattern, state);
    }

    protected void executeCommandLine(String... commandLine) {
        JRuleActionHandler.get().executeCommandLine(commandLine);
    }

    @Deprecated
    protected String executeCommandLineAndAwaitResponse(long timeout, String... commandLine) {
        return JRuleActionHandler.get().executeCommandAndAwaitResponse(Duration.of(timeout, ChronoUnit.SECONDS),
                commandLine);
    }

    protected String executeCommandLineAndAwaitResponse(Duration timeout, String... commandLine) {
        return JRuleActionHandler.get().executeCommandAndAwaitResponse(timeout, commandLine);
    }

    /**
     * Sends a GET-HTTP request and returns the result as a String
     *
     * @param url Target URL
     * @return Result as String
     */
    protected String sendHttpGetRequest(String url, @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpGetRequest(url, null, timeout);
    }

    /**
     * Sends a GET-HTTP request with the given request headers, and timeout in ms, and returns the result as a String
     *
     * @param url Target URL
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled, or null then it will never be canceled
     * @return Result as String
     */
    protected String sendHttpGetRequest(String url, @Nullable Map<String, String> headers, @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpGetRequest(url, headers, timeout);
    }

    /**
     * Sends a PUT-HTTP request and returns the result as a String
     *
     * @param url Target URL
     * @param timeout Time after the request will be canceled, or null then it will never be canceled
     * @return Result as String
     */
    protected String sendHttpPutRequest(String url, @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpPutRequest(url, null, null, null, timeout);
    }

    /**
     * Sends a PUT-HTTP request with the given content, request headers, and timeout in ms, and returns the result as a
     * String
     *
     * @param url Target URL
     * @param contentType @see javax.ws.rs.core.MediaType
     * @param content Request content
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled, or null then it will never be canceled
     * @return Result as String
     */
    protected String sendHttpPutRequest(String url, String contentType, String content, Map<String, String> headers,
            @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpPutRequest(url, contentType, content, headers, timeout);
    }

    /**
     * Sends a POST-HTTP request and returns the result as a String
     *
     * @param url Target URL
     * @param timeout Time after the request will be canceled, or null then it will never be canceled
     * @return Result as String
     */
    protected String sendHttpPostRequest(String url, @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpPostRequest(url, null, null, null, timeout);
    }

    /**
     * Sends a POST-HTTP request with the given content, request headers, and timeout in ms, and returns the result as a
     * String
     * <br/>
     *
     * @param url Target URL
     * @param contentType @see javax.ws.rs.core.MediaType
     * @param content Request content
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled, or null then it will never be canceled
     * @return Result as String
     */
    protected String sendHttpPostRequest(String url, String contentType, String content, Map<String, String> headers,
            @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpPostRequest(url, contentType, content, headers, timeout);
    }

    /**
     * Sends a DELETE-HTTP request and returns the result as a String
     *
     * @param url Target URL
     * @param timeout Time after the request will be canceled, or null then it will never be canceled
     * @return Result as String
     */
    protected String sendHttpDeleteRequest(String url, @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpDeleteRequest(url, null, timeout);
    }

    /**
     * Sends a DELETE-HTTP request with the given request headers, and timeout in ms, and returns the result as a String
     *
     * @param url Target URL
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled, or null then it will never be canceled
     * @return Result as String
     */
    protected String sendHttpDeleteRequest(String url, Map<String, String> headers, @Nullable Duration timeout) {
        return JRuleActionHandler.get().sendHttpDeleteRequest(url, headers, timeout);
    }

    /**
     * Creates or replaces a timer.
     *
     * @param timerName Name of the timer or null.
     * @param delay Initial delay and delay between the timers.
     * @param function Code to execute.
     * @return A handle for the timer.
     */
    protected JRuleTimerHandler.JRuleTimer createOrReplaceTimer(@Nullable String timerName, Duration delay,
            Runnable function) {
        return JRuleTimerHandler.get().createOrReplaceTimer(timerName, delay, function, null);
    }

    /**
     * Creates a timer.
     *
     * @param timerName Name of the timer or null.
     * @param delay Initial delay and delay between the timers.
     * @param function Code to execute.
     * @return A handle for the timer.
     */
    protected JRuleTimerHandler.JRuleTimer createTimer(@Nullable String timerName, Duration delay, Runnable function) {
        return JRuleTimerHandler.get().createTimer(timerName, delay, function, null);
    }

    /**
     * Creates a timer.
     *
     * @param delay Initial delay and delay between the timers.
     * @param function Code to execute.
     * @return A handle for the timer.
     */
    protected JRuleTimerHandler.JRuleTimer createTimer(Duration delay, Runnable function) {
        return JRuleTimerHandler.get().createTimer(null, delay, function, null);
    }

    /**
     * Creates or replace a repeating timer. All timers will have a delay to the previous one.
     *
     * @param timerName Name of the timer or null.
     * @param delay Initial delay and delay between the timers.
     * @param numberOfRepeats Number of repetitions.
     * @param function Code to execute.
     * @return A handle for the timer.
     */
    protected JRuleTimerHandler.JRuleTimer createOrReplaceRepeatingTimer(@Nullable String timerName, Duration delay,
            int numberOfRepeats, Runnable function) {
        return JRuleTimerHandler.get().createOrReplaceRepeatingTimer(timerName, delay, numberOfRepeats, function, null);
    }

    /**
     * Creates a repeating timer. All timers will have a delay to the previous one.
     *
     * @param timerName Name of the timer or null.
     * @param delay Initial delay and delay between the timers.
     * @param numberOfRepeats Number of repetitions.
     * @param function Code to execute.
     * @return A handle for the timer.
     */
    protected JRuleTimerHandler.JRuleTimer createRepeatingTimer(@Nullable String timerName, Duration delay,
            int numberOfRepeats, Runnable function) {
        return JRuleTimerHandler.get().createRepeatingTimer(timerName, delay, numberOfRepeats, function, null);
    }

    /**
     * Creates a repeating timer. All timers will have a delay to the previous one.
     *
     * @param delay Initial delay and delay between the timers.
     * @param numberOfRepeats Number of repetitions.
     * @param function Code to execute.
     * @return A handle for the timer.
     */
    protected JRuleTimerHandler.JRuleTimer createRepeatingTimer(Duration delay, int numberOfRepeats,
            Runnable function) {
        return JRuleTimerHandler.get().createRepeatingTimer(null, delay, numberOfRepeats, function, null);
    }

    /**
     * Cancels the timer with the given name.
     *
     * @param timerName Name of the timer or null.
     * @return true if canceled, false if not.
     */
    protected boolean cancelTimer(String timerName) {
        return JRuleTimerHandler.get().cancelTimer(timerName);
    }

    /**
     * Checks if the timer with the given name is still running.
     *
     * @param timerName Name of the timer or null.
     * @return true if running, false if not.
     */
    protected boolean isTimerRunning(String timerName) {
        return JRuleTimerHandler.get().isTimerRunning(timerName);
    }

    /**
     * Creating a timed look.
     *
     * @param lockName Name of the lock. Must be unique over all rules.
     * @param duration Duration until the lock is held.
     * @return Returns true if the lock for this name is not held. false if the lock is held.
     */
    protected boolean getTimedLock(@Nullable String lockName, Duration duration) {
        return JRuleTimerHandler.get().getTimedLock(lockName, duration);
    }

    protected void say(String text, String voiceId, String sinkId) {
        JRuleVoiceHandler.get().say(text, voiceId, sinkId);
    }

    protected void sendCommand(String itemName, JRuleOnOffValue command) {
        JRuleEventHandler.get().sendCommand(itemName, command);
    }

    protected void sendCommand(String itemName, JRulePercentValue percentTypeCommand) {
        JRuleEventHandler.get().sendCommand(itemName, percentTypeCommand);
    }

    protected void sendCommand(String itemName, String command) {
        JRuleEventHandler.get().sendCommand(itemName, new JRuleStringValue(command));
    }

    protected void sendCommand(String itemName, double value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRuleDecimalValue(value));
    }

    protected void sendCommand(String itemName, double value, String unit) {
        JRuleEventHandler.get().sendCommand(itemName, value, unit);
    }

    protected void sendCommand(String itemName, int value) {
        JRuleEventHandler.get().sendCommand(itemName, new JRuleDecimalValue(value));
    }

    protected void sendCommand(String itemName, Date date) {
        JRuleEventHandler.get().sendCommand(itemName, new JRuleDateTimeValue(date));
    }

    protected void postUpdate(String itemName, Date date) {
        JRuleEventHandler.get().postUpdate(itemName, new JRuleDateTimeValue(date));
    }

    protected void postUpdate(String itemName, JRuleOnOffValue state) {
        JRuleEventHandler.get().postUpdate(itemName, state);
    }

    protected void postUpdate(String itemName, String value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRuleStringValue(value));
    }

    protected void postUpdate(String itemName, double value) {
        JRuleEventHandler.get().postUpdate(itemName, new JRuleDecimalValue(value));
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
