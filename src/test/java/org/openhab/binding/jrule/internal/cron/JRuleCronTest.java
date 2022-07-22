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
package org.openhab.binding.jrule.internal.cron;

import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.internal.cron.JRuleCronExpression;
import org.openhab.binding.jrule.internal.JRuleUtilTest;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * The {@link JRuleUtilTest}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleCronTest {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    @BeforeEach
    public void setUp() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ALL);
    }

    @Test
    public void testParseItemName() {
        // sec min hour day month DAY
        JRuleCronExpression expr = new JRuleCronExpression("4 10 21 6 8 *");
        ZonedDateTime nextTimeAfter = expr.nextTimeAfter(ZonedDateTime.now());
        Date futureTime = Date.from(nextTimeAfter.toInstant());
        long initialDelay = new Date(futureTime.getTime() - System.currentTimeMillis()).getTime();
        logger.debug("Schedule cron: {} initialDelay: {}", futureTime, initialDelay);
    }
}
