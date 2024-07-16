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
package org.openhab.automation.jrule.rules.integration_test;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test for module action testcase
 *
 * @author Arne Seime - Initial contribution
 */
public class ITModuleAction extends JRuleITBase {

    @Test
    public void testModuleAction() throws IOException {
        sendCommand("ModuleActionCommandItem", "Trigger rule");
        verifyRuleWasExecuted("Call module action");
        verifyCommandEventFor("ModuleActionRecipientItem");
        verifyNoError();
    }
}
