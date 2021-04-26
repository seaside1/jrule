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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.openhab.binding.jrule.internal.JRuleUtil;
import org.openhab.binding.jrule.internal.handler.JRuleEngine;
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

    private static final Map<String, CompletableFuture<String>> itemToFuture = new HashMap<>();
    protected final ScheduledExecutorService scheduler = ThreadPoolManager
            .getScheduledPool(ThreadPoolManager.THREAD_POOL_NAME_COMMON);

    public JRule() {
        JRuleEngine.get().add(this);
    }

    // private CompletableFuture<String> getFuture(String item) {
    // CompletableFuture<String> future = itemToFuture.get(item);
    // if (reentrantLock == null) {
    // reentrantLock = new ReentrantLock();
    // itemToLock.put(item, reentrantLock);
    // }
    // return reentrantLock;
    // }

    protected boolean getTimedLock(String item, int seconds) {
        CompletableFuture<String> future = itemToFuture.get(item);
        if (future != null) {
            return false;
        }
        Supplier<CompletableFuture<String>> asyncTask = () -> CompletableFuture.completedFuture(item);
        future = JRuleUtil.scheduleAsync(scheduler, asyncTask, seconds, TimeUnit.SECONDS);
        itemToFuture.put(item, future);
        future.thenAccept(itemName -> {
            logger.debug("Timer completed! Releasing lock");
            itemToFuture.remove(itemName);
        });
        return true;
    }
}
