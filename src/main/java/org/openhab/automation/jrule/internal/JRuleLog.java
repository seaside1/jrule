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
package org.openhab.automation.jrule.internal;

import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * The {@link JRuleLog} Utilities
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleLog {
    private static final String PREFIX_INFO_LOG = "[{}] {}";
    private static final String PREFIX_DEBUG_LOG = "[+{}+] {}";
    private static final String PREFIX_WARN_LOG = "[{}] {}";
    private static final String PREFIX_ERROR_LOG = "[{}] {}";

    public static void debug(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.debug(PREFIX_DEBUG_LOG, logPrefix, logMessage.getMessage());
    }

    public static void debug(Logger logger, String logPrefix, String message, String... parameters) {
        debug(logger, logPrefix, message, (Object[]) parameters);
    }

    public static void info(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.info(PREFIX_INFO_LOG, logPrefix, logMessage.getMessage());
    }

    public static void warn(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.warn(PREFIX_WARN_LOG, logPrefix, logMessage.getMessage());
    }

    public static void error(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.error(PREFIX_ERROR_LOG, logPrefix, logMessage.getMessage());
    }
}
