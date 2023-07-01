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
package org.openhab.automation.jrule.internal.handler;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleLocalTimerExecutionContext;
import org.openhab.automation.jrule.rules.JRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * The {@link JRuleTimerHandler} handles all things with internal rule timers
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleTimerHandler {
    private static final Logger logger = LoggerFactory.getLogger(JRuleTimerHandler.class);
    public static final String LOCK_PREFIX = "$LOCK$-";
    private static volatile JRuleTimerHandler instance = null;

    public static JRuleTimerHandler get() {
        if (instance == null) {
            synchronized (JRuleThingHandler.class) {
                if (instance == null) {
                    instance = new JRuleTimerHandler();
                }
            }
        }
        return instance;
    }

    private final CopyOnWriteArrayList<JRuleTimer> timers = new CopyOnWriteArrayList<>();

    private static final ExecutorService executorService = Executors
            .newCachedThreadPool(target -> new Thread(target, "jrule-timer"));

    private JRuleTimerHandler() {
    }

    public synchronized JRuleTimer createOrReplaceTimer(@Nullable final String timerName, Duration delay,
            Consumer<JRuleTimer> function, @Nullable JRuleExecutionContext context) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        cancelTimer(newTimerName);
        return createTimer(newTimerName, delay, function, context);
    }

    public synchronized boolean cancelTimer(@Nullable String timerName) {
        getTimers(timerName).forEach(JRuleTimer::cancel);
        try {
            return getTimers(timerName).size() > 0;
        } finally {
            removeTimer(timerName);
        }
    }

    public synchronized boolean isTimerRunning(String timerName) {
        boolean noTimerWithName = timers.stream().noneMatch(timer -> timer.name.equals(timerName));
        if (noTimerWithName) {
            return false;
        }
        return timers.stream().filter(timer -> timer.name.equals(timerName))
                .noneMatch(timer -> timer.futures.stream().anyMatch(CompletableFuture::isDone));
    }

    public JRuleTimer createTimer(@Nullable final String timerName, Duration delay, Consumer<JRuleTimer> function,
            @Nullable JRuleExecutionContext context) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        Optional<JRuleTimer> any = timers.stream().filter(timer -> Objects.equals(timer.name, newTimerName)).findAny();
        if (any.isPresent()) {
            JRuleLog.debug(logger, any.get().getLogName(), "Timer '{}' already running", newTimerName);
            return any.get();
        }

        CompletableFuture<?> future = delayedExecution(delay);
        JRuleTimer timer = new JRuleTimer(newTimerName, function, future,
                context != null ? context : getCurrentContext(), delay);
        timers.add(timer);

        JRuleLog.info(logger, timer.getLogName(), "Start timer '{}' with delay: {}", newTimerName, delay);
        future.thenAccept(s -> executorService.submit(() -> invokeTimerInternal(timer, function)));
        return timer;
    }

    public synchronized JRuleTimer createOrReplaceRepeatingTimer(@Nullable final String timerName, Duration delay,
            int numberOfRepeats, Consumer<JRuleTimer> function, @Nullable JRuleExecutionContext context) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        cancelTimer(newTimerName);
        return createRepeatingTimer(newTimerName, delay, numberOfRepeats, function, context);
    }

    public synchronized JRuleTimer createRepeatingTimer(@Nullable String timerName, Duration delay, int numberOfRepeats,
            Consumer<JRuleTimer> function, @Nullable JRuleExecutionContext context) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        Optional<JRuleTimer> any = timers.stream().filter(timer -> timer.name.equals(newTimerName)).findAny();
        if (any.isPresent()) {
            return any.get();
        }

        List<CompletableFuture<?>> newTimers = Stream.iterate(0, i -> i + 1).limit(numberOfRepeats)
                .map(i -> delayedExecution(delay.multipliedBy(i).plus(delay))).collect(Collectors.toList());
        JRuleTimer timer = new JRuleTimer(newTimerName, function, newTimers,
                context != null ? context : getCurrentContext(), delay);
        timers.add(timer);
        logger.trace("added repeating timers '{}': {}", newTimerName, newTimers.size());
        getTimers(newTimerName);

        newTimers.forEach(
                future -> future.thenAccept(s -> executorService.submit(() -> invokeTimerInternal(timer, function))));
        return timer;
    }

    public boolean getTimedLock(String lockName, Duration duration) {
        if (getTimers(LOCK_PREFIX + lockName).size() > 0) {
            return false;
        }
        CompletableFuture<?> future = delayedExecution(duration);
        future.thenRun(() -> removeTimer(LOCK_PREFIX + lockName)).thenRun(() -> JRuleLog.info(logger,
                getCurrentContext().getLogName(), String.format("Timer '%s' completed! Releasing lock", lockName)));
        timers.add(new JRuleTimer(LOCK_PREFIX + lockName, null, future, getCurrentContext(), duration));
        return true;
    }

    private static JRuleExecutionContext getCurrentContext() {
        return JRule.JRULE_EXECUTION_CONTEXT.get();
    }

    private synchronized List<JRuleTimer> getTimers(String timerName) {
        List<JRuleTimer> list = timers.stream().filter(timer -> timer.name.equals(timerName))
                .collect(Collectors.toList());
        logger.trace("timers for name '{}': {}", timerName, list.size());
        return list;
    }

    private void invokeTimerInternal(JRuleTimer timer, Consumer<JRuleTimer> runnable) {
        try {
            JRule.JRULE_EXECUTION_CONTEXT.set(new JRuleLocalTimerExecutionContext(timer.context, timer.name));
            JRuleLog.debug(logger, timer.context.getMethod().getName(), "Invoking timer from context: {}",
                    timer.context);

            JRuleLog.debug(logger, timer.context.getMethod().getName(), "setting mdc tags: {}",
                    timer.context.getLoggingTags());
            MDC.put(JRuleEngine.MDC_KEY_RULE, timer.context.getMethod().getName());
            MDC.put(JRuleEngine.MDC_KEY_TIMER, timer.name);
            Arrays.stream(timer.context.getLoggingTags()).forEach(s -> MDC.put(s, s));
            runnable.accept(timer);
        } catch (IllegalArgumentException | SecurityException e) {
            JRuleLog.error(logger, timer.context.getMethod().getName(), "Error {}", ExceptionUtils.getStackTrace(e));
        } finally {
            Arrays.stream(timer.context.getLoggingTags()).forEach(MDC::remove);
            MDC.remove(JRuleEngine.MDC_KEY_RULE);
            MDC.remove(JRuleEngine.MDC_KEY_TIMER);
            logger.debug("Removing thread local after rule completion");
            if (timer.isDone()) {
                removeTimer(timer.name);
            }
            JRule.JRULE_EXECUTION_CONTEXT.remove();
        }
    }

    private synchronized void removeTimer(String timerName) {
        logger.trace("remove timer: '{}'", timerName);
        timers.removeIf(timer -> timer.name.equals(timerName));
    }

    private CompletableFuture<?> delayedExecution(Duration delay) {
        Executor delayedExecutor = CompletableFuture.delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS,
                executorService);
        return CompletableFuture.supplyAsync(() -> null, delayedExecutor);
    }

    public void cancelAll() {
        this.timers.forEach(jRuleTimer -> cancelTimer(jRuleTimer.name));
    }

    public final class JRuleTimer {
        private final Duration delay;
        private final String name;
        private final List<CompletableFuture<?>> futures;

        private final JRuleExecutionContext context;
        private Consumer<JRuleTimer> function;

        public JRuleTimer(String name, Consumer<JRuleTimer> function, CompletableFuture<?> future,
                JRuleExecutionContext context, Duration delay) {
            this.name = name;
            this.function = function;
            this.futures = List.of(future);
            this.context = context;
            this.delay = delay;
        }

        public JRuleTimer(String name, Consumer<JRuleTimer> function, List<CompletableFuture<?>> futures,
                JRuleExecutionContext context, Duration delay) {
            this.name = name;
            this.function = function;
            this.futures = futures;
            this.context = context;
            this.delay = delay;
        }

        public void cancel() {
            logger.debug("before: {}", futures);
            this.futures.forEach(future -> future.cancel(false));
            logger.debug("after: {}", futures);
        }

        public String getLogName() {
            return String.format("%s / %s", this.context.getLogName(), this.name);
        }

        public boolean isRunning() {
            return futures.stream().anyMatch(future -> !future.isDone());
        }

        public JRuleTimerHandler.JRuleTimer createTimerAfter(@Nullable String timerName, Duration delay,
                Consumer<JRuleTimer> function) {
            return JRuleTimerHandler.this.createTimer(timerName, delay.plus(this.delay), function, context);
        }

        public JRuleTimerHandler.JRuleTimer createTimerAfter(Duration delay, Consumer<JRuleTimer> function) {
            return JRuleTimerHandler.this.createTimer(null, delay.plus(this.delay), function, context);
        }

        public JRuleTimerHandler.JRuleTimer createOrReplaceTimerAfter(@Nullable String timerName, Duration delay,
                Consumer<JRuleTimer> function) {
            return JRuleTimerHandler.this.createOrReplaceTimer(timerName, delay.plus(this.delay), function, context);
        }

        public JRuleTimerHandler.JRuleTimer createOrReplaceRepeatingTimerAfter(@Nullable String timerName,
                Duration delay, int numberOfRepeats, Consumer<JRuleTimer> function) {
            return JRuleTimerHandler.this.createOrReplaceRepeatingTimer(timerName, delay.plus(this.delay),
                    numberOfRepeats, function, context);
        }

        public JRuleTimerHandler.JRuleTimer createRepeatingTimerAfter(@Nullable String timerName, Duration delay,
                int numberOfRepeats, Consumer<JRuleTimer> function) {
            return JRuleTimerHandler.this.createRepeatingTimer(timerName, delay.plus(this.delay), numberOfRepeats,
                    function, context);
        }

        public JRuleTimerHandler.JRuleTimer createRepeatingTimerAfter(Duration delay, int numberOfRepeats,
                Consumer<JRuleTimer> function) {
            return JRuleTimerHandler.this.createRepeatingTimer(null, delay.plus(this.delay), numberOfRepeats, function,
                    context);
        }

        public JRuleTimerHandler.JRuleTimer rescheduleTimer(Duration delay) {
            return JRuleTimerHandler.this.createOrReplaceTimer(this.name, delay, this.function, context);
        }

        public boolean isDone() {
            return futures.stream().allMatch(CompletableFuture::isDone);
        }
    }
}
