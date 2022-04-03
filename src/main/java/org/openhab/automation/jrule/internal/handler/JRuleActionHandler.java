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
package org.openhab.automation.jrule.internal.handler;

import java.time.Duration;

import org.openhab.core.io.net.exec.ExecUtil;

/**
 * The {@link JRuleEventHandler} is responsible for handling commands and status
 * updates for JRule
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleActionHandler {

    private static volatile JRuleActionHandler instance = null;

    private JRuleActionHandler() {
    }

    public static JRuleActionHandler get() {
        if (instance == null) {
            synchronized (JRuleActionHandler.class) {
                if (instance == null) {
                    instance = new JRuleActionHandler();
                }
            }
        }
        return instance;
    }

    public void executeCommandLine(String... commandLine) {
        ExecUtil.executeCommandLine(commandLine);
    }

    public String executeCommandAndAwaitResponse(long delayInSeconds, String... commandLine) {
        return ExecUtil.executeCommandLineAndWaitResponse(Duration.ofSeconds(delayInSeconds), commandLine);
    }
}
