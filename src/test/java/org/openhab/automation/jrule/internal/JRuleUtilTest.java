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
package org.openhab.automation.jrule.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * The {@link JRuleUtilTest}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleUtilTest {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    @BeforeEach
    public void setUp() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ALL);
    }

    @Test
    public void testParseItemName() {
        String itemTopicGroup = JRuleUtil
                .getItemNameFromTopic("openhab/items/gSensorPowerSum/ZwaveULRDimmerSensorPower/statechanged");
        logger.info("Topic Group: {}", itemTopicGroup);
        String topic = "openhab/items/ZwaveEye3Motion/statechanged";
        String itemTopic = JRuleUtil.getItemNameFromTopic(topic);
        assertEquals("gSensorPowerSum", itemTopicGroup);
        assertEquals("ZwaveEye3Motion", itemTopic);
    }
}
