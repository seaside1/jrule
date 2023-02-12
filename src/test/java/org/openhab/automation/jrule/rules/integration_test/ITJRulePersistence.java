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
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.regex.Matcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.items.JRuleContactItem;
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.TestPersistence;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePointValue;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;

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
        postUpdate(TestPersistence.ITEM_QUANTITY_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_DIMMER_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_COLOR_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_STRING_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_DATETIME_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_PLAYER_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_CONTACT_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_IMAGE_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_ROLLERSHUTTER_TO_PERSIST, "UNDEF");
        postUpdate(TestPersistence.ITEM_LOCATION_TO_PERSIST, "UNDEF");
    }

    @Test
    public void persistenceAllTypes() throws IOException, InterruptedException {
        postUpdate(TestPersistence.ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem.OFF);
        postUpdate(TestPersistence.ITEM_NUMBER_TO_PERSIST, "20");
        postUpdate(TestPersistence.ITEM_QUANTITY_TO_PERSIST, "22W");
        postUpdate(TestPersistence.ITEM_DIMMER_TO_PERSIST, "50");
        postUpdate(TestPersistence.ITEM_COLOR_TO_PERSIST, "12,12,12");
        postUpdate(TestPersistence.ITEM_STRING_TO_PERSIST, "Test1");
        postUpdate(TestPersistence.ITEM_DATETIME_TO_PERSIST, new JRuleDateTimeValue(ZonedDateTime.now()).stringValue());
        postUpdate(TestPersistence.ITEM_PLAYER_TO_PERSIST, JRulePlayerItem.PAUSE);
        postUpdate(TestPersistence.ITEM_CONTACT_TO_PERSIST, JRuleContactItem.CLOSED);
        postUpdate(TestPersistence.ITEM_IMAGE_TO_PERSIST, new JRuleRawValue("jpeg", new byte[16]).stringValue());
        postUpdate(TestPersistence.ITEM_ROLLERSHUTTER_TO_PERSIST, "30");
        postUpdate(TestPersistence.ITEM_LOCATION_TO_PERSIST, new JRulePointValue(14.4D, 15.5D).stringValue());

        Thread.sleep(2000);

        postUpdate(TestPersistence.ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem.ON);
        postUpdate(TestPersistence.ITEM_NUMBER_TO_PERSIST, "100");
        postUpdate(TestPersistence.ITEM_QUANTITY_TO_PERSIST, "100W");
        postUpdate(TestPersistence.ITEM_DIMMER_TO_PERSIST, "100");
        postUpdate(TestPersistence.ITEM_COLOR_TO_PERSIST, "100,100,100");
        postUpdate(TestPersistence.ITEM_STRING_TO_PERSIST, "Test100");
        postUpdate(TestPersistence.ITEM_DATETIME_TO_PERSIST,
                new JRuleDateTimeValue(ZonedDateTime.now().plusYears(1)).stringValue());
        postUpdate(TestPersistence.ITEM_PLAYER_TO_PERSIST, JRulePlayerItem.PLAY);
        postUpdate(TestPersistence.ITEM_CONTACT_TO_PERSIST, JRuleContactItem.OPEN);
        postUpdate(TestPersistence.ITEM_IMAGE_TO_PERSIST, new JRuleRawValue("jpeg", new byte[160]).stringValue());
        postUpdate(TestPersistence.ITEM_ROLLERSHUTTER_TO_PERSIST, "100");
        postUpdate(TestPersistence.ITEM_LOCATION_TO_PERSIST, new JRulePointValue(100D, 100D).stringValue());

        Thread.sleep(2000);

        postUpdate(TestPersistence.ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem.OFF);
        postUpdate(TestPersistence.ITEM_NUMBER_TO_PERSIST, "0");
        postUpdate(TestPersistence.ITEM_QUANTITY_TO_PERSIST, "0W");
        postUpdate(TestPersistence.ITEM_DIMMER_TO_PERSIST, "0");
        postUpdate(TestPersistence.ITEM_COLOR_TO_PERSIST, "0,0,0");
        postUpdate(TestPersistence.ITEM_STRING_TO_PERSIST, "Test0");
        postUpdate(TestPersistence.ITEM_DATETIME_TO_PERSIST,
                new JRuleDateTimeValue(ZonedDateTime.now().minusYears(1)).stringValue());
        postUpdate(TestPersistence.ITEM_PLAYER_TO_PERSIST, JRulePlayerItem.PAUSE);
        postUpdate(TestPersistence.ITEM_CONTACT_TO_PERSIST, JRuleContactItem.CLOSED);
        postUpdate(TestPersistence.ITEM_IMAGE_TO_PERSIST, new JRuleRawValue("jpeg", new byte[1]).stringValue());
        postUpdate(TestPersistence.ITEM_ROLLERSHUTTER_TO_PERSIST, "0");
        postUpdate(TestPersistence.ITEM_LOCATION_TO_PERSIST, new JRulePointValue(1D, 1D).stringValue());
        Thread.sleep(2000);

        sendCommand(TestPersistence.ITEM_TRIGGER_RULE, TestPersistence.COMMMAND_PERISTENCE);
        verifyRuleWasExecuted(TestPersistence.NAME_PERSIST_ALL_TYPES);
        verifyNoError();

        verifySwitch();
        verifyNumber();
        verifyQuantity();
    }

    private void verifyNumber() {
        verifyPersistence("historicState", "Number_To_Persist", 7, null, 10);
        verifyPersistence("historicState", "Number_To_Persist", 5, 20.0, 10);
        verifyPersistence("historicState", "Number_To_Persist", 3, 100.0, 10);
        verifyPersistence("historicState", "Number_To_Persist", 1, 0.0, 10);

        verifyPersistence("sumSince", "Number_To_Persist", 7, 120.0, 10);
        verifyPersistence("sumSince", "Number_To_Persist", 5, 100.0, 10);
        verifyPersistence("sumSince", "Number_To_Persist", 3, 0.0, 10);
        verifyPersistence("sumSince", "Number_To_Persist", 1, 0.0, 10);

        verifyPersistence("averageSince", "Number_To_Persist", 7, 34.4, 10);
        verifyPersistence("averageSince", "Number_To_Persist", 5, 22.5, 10);
        verifyPersistence("averageSince", "Number_To_Persist", 3, 0.0, 10);
        verifyPersistence("averageSince", "Number_To_Persist", 1, null, 10);

        verifyPersistence("minimumSince", "Number_To_Persist", 7, 0.0, 10);
        verifyPersistence("minimumSince", "Number_To_Persist", 5, 0.0, 10);
        verifyPersistence("minimumSince", "Number_To_Persist", 3, 0.0, 10);
        verifyPersistence("minimumSince", "Number_To_Persist", 1, 0.0, 10);

        verifyPersistence("maximumSince", "Number_To_Persist", 7, 100.0, 10);
        verifyPersistence("maximumSince", "Number_To_Persist", 5, 100.0, 10);
        verifyPersistence("maximumSince", "Number_To_Persist", 3, 0.0, 10);
        verifyPersistence("maximumSince", "Number_To_Persist", 1, 0.0, 10);

        verifyPersistence("deviationSince", "Number_To_Persist", 7, 43.7, 10);
        verifyPersistence("deviationSince", "Number_To_Persist", 5, 57.3, 10);
        verifyPersistence("deviationSince", "Number_To_Persist", 3, 0.0, 10);
        verifyPersistence("deviationSince", "Number_To_Persist", 1, null, 10);

        verifyPersistence("varianceSince", "Number_To_Persist", 7, 1908.0, 10);
        verifyPersistence("varianceSince", "Number_To_Persist", 5, 3297.0, 10);
        verifyPersistence("varianceSince", "Number_To_Persist", 3, 0.0, 10);
        verifyPersistence("varianceSince", "Number_To_Persist", 1, null, 10);

        verifyPersistenceChangedSince("changedSince", "Number_To_Persist", 7, true);
        verifyPersistenceChangedSince("changedSince", "Number_To_Persist", 5, true);
        verifyPersistenceChangedSince("changedSince", "Number_To_Persist", 3, true);
        verifyPersistenceChangedSince("changedSince", "Number_To_Persist", 1, false);
    }

    private void verifyQuantity() {
        verifyPersistence("historicState", "Quantity_To_Persist", 7, null, 10);
        verifyPersistence("historicState", "Quantity_To_Persist", 5, 122.0, 10);
        verifyPersistence("historicState", "Quantity_To_Persist", 3, 100.0, 10);
        verifyPersistence("historicState", "Quantity_To_Persist", 1, 0.0, 10);

        verifyPersistence("sumSince", "Quantity_To_Persist", 7, 120.0, 10);
        verifyPersistence("sumSince", "Quantity_To_Persist", 5, 100.0, 10);
        verifyPersistence("sumSince", "Quantity_To_Persist", 3, 0.0, 10);
        verifyPersistence("sumSince", "Quantity_To_Persist", 1, 0.0, 10);

        verifyPersistence("averageSince", "Quantity_To_Persist", 7, 34.4, 10);
        verifyPersistence("averageSince", "Quantity_To_Persist", 5, 22.5, 10);
        verifyPersistence("averageSince", "Quantity_To_Persist", 3, 0.0, 10);
        verifyPersistence("averageSince", "Quantity_To_Persist", 1, null, 10);

        verifyPersistence("minimumSince", "Quantity_To_Persist", 7, 0.0, 10);
        verifyPersistence("minimumSince", "Quantity_To_Persist", 5, 0.0, 10);
        verifyPersistence("minimumSince", "Quantity_To_Persist", 3, 0.0, 10);
        verifyPersistence("minimumSince", "Quantity_To_Persist", 1, 0.0, 10);

        verifyPersistence("maximumSince", "Quantity_To_Persist", 7, 100.0, 10);
        verifyPersistence("maximumSince", "Quantity_To_Persist", 5, 100.0, 10);
        verifyPersistence("maximumSince", "Quantity_To_Persist", 3, 0.0, 10);
        verifyPersistence("maximumSince", "Quantity_To_Persist", 1, 0.0, 10);

        verifyPersistence("deviationSince", "Quantity_To_Persist", 7, 43.7, 10);
        verifyPersistence("deviationSince", "Quantity_To_Persist", 5, 57.3, 10);
        verifyPersistence("deviationSince", "Quantity_To_Persist", 3, 0.0, 10);
        verifyPersistence("deviationSince", "Quantity_To_Persist", 1, null, 10);

        verifyPersistence("varianceSince", "Quantity_To_Persist", 7, 1908.0, 10);
        verifyPersistence("varianceSince", "Quantity_To_Persist", 5, 3297.0, 10);
        verifyPersistence("varianceSince", "Quantity_To_Persist", 3, 0.0, 10);
        verifyPersistence("varianceSince", "Quantity_To_Persist", 1, null, 10);

        verifyPersistenceChangedSince("changedSince", "Quantity_To_Persist", 7, true);
        verifyPersistenceChangedSince("changedSince", "Quantity_To_Persist", 5, true);
        verifyPersistenceChangedSince("changedSince", "Quantity_To_Persist", 3, true);
        verifyPersistenceChangedSince("changedSince", "Quantity_To_Persist", 1, false);
    }

    private void verifySwitch() {
        verifyPersistence("historicState", "Switch_To_Persist", 7, null, 10);
        verifyPersistence("historicState", "Switch_To_Persist", 5, JRuleOnOffValue.OFF, 10);
        verifyPersistence("historicState", "Switch_To_Persist", 3, JRuleOnOffValue.ON, 10);
        verifyPersistence("historicState", "Switch_To_Persist", 1, JRuleOnOffValue.OFF, 10);

        verifyPersistence("sumSince", "Switch_To_Persist", 7, 1.0, 10);
        verifyPersistence("sumSince", "Switch_To_Persist", 5, 1.0, 10);
        verifyPersistence("sumSince", "Switch_To_Persist", 3, 0.0, 10);
        verifyPersistence("sumSince", "Switch_To_Persist", 1, 0.0, 10);

        verifyPersistence("averageSince", "Switch_To_Persist", 7, 0.3, 10);
        verifyPersistence("averageSince", "Switch_To_Persist", 5, 0.2, 10);
        verifyPersistence("averageSince", "Switch_To_Persist", 3, 0.0, 10);
        verifyPersistence("averageSince", "Switch_To_Persist", 1, null, 10);

        verifyPersistence("minimumSince", "Switch_To_Persist", 7, 0.0, 10);
        verifyPersistence("minimumSince", "Switch_To_Persist", 5, 0.0, 10);
        verifyPersistence("minimumSince", "Switch_To_Persist", 3, 0.0, 10);
        verifyPersistence("minimumSince", "Switch_To_Persist", 1, 0.0, 10);

        verifyPersistence("maximumSince", "Switch_To_Persist", 7, 1.0, 10);
        verifyPersistence("maximumSince", "Switch_To_Persist", 5, 1.0, 10);
        verifyPersistence("maximumSince", "Switch_To_Persist", 3, 0.0, 10);
        verifyPersistence("maximumSince", "Switch_To_Persist", 1, 0.0, 10);

        verifyPersistence("deviationSince", "Switch_To_Persist", 7, 0.5, 10);
        verifyPersistence("deviationSince", "Switch_To_Persist", 5, 0.6, 10);
        verifyPersistence("deviationSince", "Switch_To_Persist", 3, 0.0, 10);
        verifyPersistence("deviationSince", "Switch_To_Persist", 1, null, 10);

        verifyPersistence("varianceSince", "Switch_To_Persist", 7, 0.2, 10);
        verifyPersistence("varianceSince", "Switch_To_Persist", 5, 0.3, 10);
        verifyPersistence("varianceSince", "Switch_To_Persist", 3, 0.0, 10);
        verifyPersistence("varianceSince", "Switch_To_Persist", 1, null, 10);

        verifyPersistenceChangedSince("changedSince", "Switch_To_Persist", 7, true);
        verifyPersistenceChangedSince("changedSince", "Switch_To_Persist", 5, true);
        verifyPersistenceChangedSince("changedSince", "Switch_To_Persist", 3, true);
        verifyPersistenceChangedSince("changedSince", "Switch_To_Persist", 1, false);
    }

    private void verifyPersistence(String persistenceMethod, String itemName, int beforeSeconds, Object expectedValue, int deviation) {
        String ruleLogLine = String.format(".*\\[persist all types\\] %s: %s since/before %ss: '(.+)'", persistenceMethod,
                itemName, beforeSeconds);
        Matcher matcher = getLogEntry(ruleLogLine);
        String givenValue = matcher.group(1);
        if (!givenValue.equals(String.valueOf(expectedValue))) {
            float givenFloat = Float.parseFloat(givenValue);
            float expectedFloat = Float.parseFloat(expectedValue.toString());
            float givenDeviation = 100f - (Math.min(givenFloat, expectedFloat) / Math.max(givenFloat, expectedFloat) * 100f);
            if (givenDeviation > deviation) {
                Assertions.fail(String.format("deviation of values (given=%s, expected=%s) %s is higher then expected deviation %s", givenFloat, expectedFloat, givenDeviation, deviation));
            }
        }
        System.out.println(givenValue);
    }

    private void verifyPersistenceChangedSince(String persistenceMethod, String itemName, int beforeSeconds,
            Object value) {
        String ruleLogLine = String.format("[persist all types] %s: %s since/before %ss: %s", persistenceMethod,
                itemName, beforeSeconds, value);
        verifyLogEntry(ruleLogLine);
    }
}
