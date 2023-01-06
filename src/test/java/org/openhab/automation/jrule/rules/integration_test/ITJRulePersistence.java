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
package org.openhab.automation.jrule.rules.integration_test;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.TestPersistence;

/**
 * The {@link ITJRulePersistence}
 *
 * @author Robert Delbrück - Initial contribution
 */
// Please do not reuse items for testing, create new ones, otherwise every change will hurt sometimes
public class ITJRulePersistence extends JRuleITBase {
    public void initItems() throws IOException {
        postUpdate(TestPersistence.ITEM_SWITCH_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_NUMBER_TO_PERSIST, "UNDEF");
    }

    @Test
    public void persistenceAllTypes() throws IOException, InterruptedException {
        Thread.sleep(2000);
        sendCommand(TestPersistence.ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem.OFF);
        sendCommand(TestPersistence.ITEM_NUMBER_TO_PERSIST, "20");
        Thread.sleep(2000);
        sendCommand(TestPersistence.ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem.ON);
        sendCommand(TestPersistence.ITEM_NUMBER_TO_PERSIST, "100");

        sendCommand(TestPersistence.ITEM_TRIGGER_RULE, TestPersistence.COMMMAND_PERISTENCE);
        verifyRuleWasExecuted(TestPersistence.NAME_PERSIST_ALL_TYPES);
        verifyNoError();
    }
}
