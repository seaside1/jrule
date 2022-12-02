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
import java.util.Date;

import org.openhab.automation.jrule.exception.JRuleExecutionException;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.handler.JRuleActionHandler;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
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

    public static final ThreadLocal<JRuleExecutionContext> JRULE_EXECUTION_CONTEXT = new ThreadLocal<>();

    public JRule() {
        JRuleEngine.get().add(this);
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

    protected boolean isTimerRunning(String timerName) {
        return JRuleTimerHandler.get().isTimerRunning(timerName);
    }

    protected JRuleTimerHandler.JRuleTimer createOrReplaceTimer(String timerName, Duration delay, Runnable function) {
        return JRuleTimerHandler.get().createOrReplaceTimer(timerName, delay, function, null);
    }

    protected boolean cancelTimer(String timerName) {
        return JRuleTimerHandler.get().cancelTimer(timerName);
    }

    protected JRuleTimerHandler.JRuleTimer createTimer(String timerName, Duration delay, Runnable function) {
        return JRuleTimerHandler.get().createTimer(timerName, delay, function, null);
    }

    protected JRuleTimerHandler.JRuleTimer createOrReplaceRepeatingTimer(String timerName, Duration delay,
            int numberOfRepeats, Runnable function) {
        return JRuleTimerHandler.get().createOrReplaceRepeatingTimer(timerName, delay, numberOfRepeats, function, null);
    }

    protected JRuleTimerHandler.JRuleTimer createRepeatingTimer(String timerName, Duration delay, int numberOfRepeats,
            Runnable function) {
        return JRuleTimerHandler.get().createRepeatingTimer(timerName, delay, numberOfRepeats, function, null);
    }

    protected boolean getTimedLock(String lockName, Duration duration) {
        return JRuleTimerHandler.get().getTimedLock(lockName, duration);
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
