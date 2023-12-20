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
    private static final String LOG_FORMAT = "{}{}";

    public static void debug(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.debug(LOG_FORMAT, getPrefix(logPrefix, true), logMessage.getMessage());
    }

    public static void debug(Logger logger, String logPrefix, String message, String... parameters) {
        debug(logger, logPrefix, message, (Object[]) parameters);
    }

    public static void info(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.info(LOG_FORMAT, getPrefix(logPrefix, false), logMessage.getMessage());
    }

    public static void warn(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.warn(LOG_FORMAT, getPrefix(logPrefix, false), logMessage.getMessage());
    }

    public static void error(Logger logger, String logPrefix, Throwable t, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        final FormattingTuple finalLogmessage = MessageFormatter.format(LOG_FORMAT,
                new String[] { logPrefix, logMessage.getMessage() });
        error(logger, t, finalLogmessage.getMessage());
    }

    private static void error(Logger logger, Throwable t, String message) {
        logger.error(message, t);
    }

    public static void error(Logger logger, String logPrefix, String message, Object... parameters) {
        final FormattingTuple logMessage = MessageFormatter.arrayFormat(message, parameters);
        logger.error(LOG_FORMAT, getPrefix(logPrefix, false), logMessage.getMessage());
    }

    private static String getPrefix(String logPrefix, boolean debug) {
        if (logPrefix.isBlank()) {
            return "";
        }
        if (debug) {
            return "[+%s+] ".formatted(logPrefix);
        } else {
            return "[%s] ".formatted(logPrefix);
        }
    }
}
