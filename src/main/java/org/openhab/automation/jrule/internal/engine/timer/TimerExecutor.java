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
package org.openhab.automation.jrule.internal.engine.timer;

import java.util.ArrayList;
import java.util.List;

import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleTimeTimerExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleTimedCronExecutionContext;
import org.openhab.core.scheduler.CronScheduler;
import org.openhab.core.scheduler.ScheduledCompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TimerExecutor}
 *
 * @author Robert Delbrück
 */
public class TimerExecutor {
    private final Logger logger = LoggerFactory.getLogger(TimerExecutor.class);
    private final List<ScheduledCompletableFuture<Void>> timers = new ArrayList<>();
    private final JRuleEngine jRuleEngine;
    private CronScheduler cronScheduler;

    public TimerExecutor(JRuleEngine jRuleEngine) {
        this.jRuleEngine = jRuleEngine;
    }

    public void add(JRuleTimedCronExecutionContext executionContext) {
        timers.add(cronScheduler.schedule(() -> {
            if (jRuleEngine.matchPrecondition(executionContext)) {
                jRuleEngine.invokeRule(executionContext, executionContext.createJRuleEvent(null));
            }
        }, executionContext.getCron()));
    }

    public void add(JRuleTimeTimerExecutionContext executionContext) {
        String cron = String.format("%s %s %s %s %s %s", executionContext.getSecond().map(String::valueOf).orElse("*"),
                executionContext.getMinute().map(String::valueOf).orElse("*"),
                executionContext.getHour().map(String::valueOf).orElse("*"), "*", "*", "*");
        JRuleLog.info(logger, TimerExecutor.class.getSimpleName(), "Generated cron for timer: {}", cron);
        timers.add(cronScheduler.schedule(() -> {
            if (jRuleEngine.matchPrecondition(executionContext)) {
                jRuleEngine.invokeRule(executionContext, executionContext.createJRuleEvent(null));
            }
        }, cron));
    }

    public void add(JRuleExecutionContext context) {
        if (context instanceof JRuleTimedCronExecutionContext) {
            this.add((JRuleTimedCronExecutionContext) context);
        } else if (context instanceof JRuleTimeTimerExecutionContext) {
            this.add((JRuleTimeTimerExecutionContext) context);
        }
    }

    public void setCronScheduler(CronScheduler cronScheduler) {
        this.cronScheduler = cronScheduler;
    }

    public void clear() {
        timers.forEach(timer -> timer.cancel(true));
        timers.clear();
    }
}
