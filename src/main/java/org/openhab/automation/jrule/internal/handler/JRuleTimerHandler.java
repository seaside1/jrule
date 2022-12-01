package org.openhab.automation.jrule.internal.handler;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.rules.JRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.netty.util.concurrent.DefaultThreadFactory;

public class JRuleTimerHandler {
    private static final Logger logger = LoggerFactory.getLogger(JRuleTimerHandler.class);
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

    private final ExecutorService executorService = Executors
            .newCachedThreadPool(new DefaultThreadFactory("jrule-timer", true));

    private JRuleTimerHandler() {
    }

    public synchronized boolean isTimerRunning(String timerName) {
        return timers.stream().filter(timer -> timer.name.equals(timerName)).noneMatch(timer -> timer.future.isDone());
    }

    public synchronized JRuleTimer createOrReplaceTimer(@Nullable final String timerName, Duration delay,
            Runnable function) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        cancelTimer(newTimerName);
        return createTimer(newTimerName, delay, function);
    }

    public synchronized boolean cancelTimer(String timerName) {
        getTimers(timerName).forEach(JRuleTimer::cancel);
        try {
            return getTimers(timerName).size() > 0;
        } finally {
            removeTimer(timerName);
        }
    }

    public synchronized JRuleTimer createTimer(@Nullable final String timerName, Duration delay, Runnable function) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        Optional<JRuleTimer> any = timers.stream().filter(timer -> timer.name.equals(newTimerName)).findAny();
        if (any.isPresent()) {
            JRuleLog.debug(logger, any.get().getLogName(), "Timer '{}' already running", newTimerName);
            return any.get();
        }

        CompletableFuture<Void> future = delayedExecution(delay);
        JRuleTimer timer = new JRuleTimer(newTimerName, future, getCurrentContext());
        timers.add(timer);

        JRuleLog.info(logger, timer.getLogName(), "Start timer '{}' with delay: {}", newTimerName, delay);
        future.thenAccept(s -> executorService.submit(() -> invokeTimerInternal(timer, function)));
        return timer;
    }

    public synchronized List<JRuleTimer> createOrReplaceRepeatingTimer(@Nullable final String timerName, Duration delay,
            int numberOfRepeats, Runnable function) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        cancelTimer(newTimerName);
        return createRepeatingTimer(newTimerName, delay, numberOfRepeats, function);
    }

    public synchronized List<JRuleTimer> createRepeatingTimer(String timerName, Duration delay, int numberOfRepeats,
            Runnable function) {
        final String newTimerName = Optional.ofNullable(timerName).orElse(UUID.randomUUID().toString());
        List<JRuleTimer> timerList = getTimers(newTimerName);
        if (!timerList.isEmpty()) {
            return timerList;
        }

        List<JRuleTimer> newTimers = Stream
                .iterate(0, i -> i + 1).limit(numberOfRepeats).map(i -> new JRuleTimer(newTimerName,
                        delayedExecution(delay.multipliedBy(i).plus(delay)), getCurrentContext()))
                .collect(Collectors.toList());
        timers.addAll(newTimers);

        newTimers.forEach(timer -> executorService.submit(() -> invokeTimerInternal(timer, function)));
        return newTimers;
    }

    private static JRuleExecutionContext getCurrentContext() {
        return JRule.JRULE_EXECUTION_CONTEXT.get();
    }

    private synchronized List<JRuleTimer> getTimers(String timerName) {
        return timers.stream().filter(timer -> timer.name.equals(timerName)).collect(Collectors.toList());
    }

    private void invokeTimerInternal(JRuleTimer timer, Runnable runnable) {
        JRule.JRULE_EXECUTION_CONTEXT.set(timer.context);
        JRuleLog.debug(logger, timer.context.getMethod().getName(), "Invoking timer from context: {}", timer.context);

        try {
            JRuleLog.debug(logger, timer.context.getMethod().getName(), "setting mdc tags: {}",
                    timer.context.getLoggingTags());
            MDC.put(JRuleEngine.MDC_KEY_RULE, timer.context.getMethod().getName());
            Arrays.stream(timer.context.getLoggingTags()).forEach(s -> MDC.put(s, s));
            runnable.run();
        } catch (IllegalArgumentException | SecurityException e) {
            JRuleLog.error(logger, timer.context.getMethod().getName(), "Error {}", ExceptionUtils.getStackTrace(e));
        } finally {
            Arrays.stream(timer.context.getLoggingTags()).forEach(MDC::remove);
            MDC.remove(JRuleEngine.MDC_KEY_RULE);
            logger.debug("Removing thread local after rule completion");
            timers.remove(timer);
            JRule.JRULE_EXECUTION_CONTEXT.remove();
        }
    }

    private synchronized void removeTimer(String timerName) {
        timers.removeIf(timer -> timer.name.equals(timerName));
    }

    private <T> CompletableFuture<T> delayedExecution(Duration delay) {
        Executor delayedExecutor = CompletableFuture.delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS,
                executorService);
        return CompletableFuture.supplyAsync(() -> null, delayedExecutor);
    }

    public static final class JRuleTimer {
        private String name;
        private CompletableFuture<Void> future;
        private JRuleExecutionContext context;

        public JRuleTimer(String name, CompletableFuture<Void> future, JRuleExecutionContext context) {
            this.name = name;
            this.future = future;
            this.context = context;
        }

        public void cancel() {
            this.future.cancel(false);
        }

        public String getLogName() {
            return String.format("%s/%s", this.context.getLogName(), this.name);
        }
    }
}
