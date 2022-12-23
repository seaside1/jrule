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
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.rules.user.TestReceiveCommands;

/**
 * The {@link ITJRuleReceiveCommands}
 *
 * @author Robert Delbrück - Initial contribution
 */
// Please do not reuse items for testing, create new ones, otherwise every change will hurt sometimes
public class ITJRuleReceiveCommands extends JRuleITBase {
    public static final String NAME_RECEIVE_QUANTITY_UPDATE = "receive quantity update";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]Z");

    @Test
    public void receiveSwitchCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_SWITCH_EVENT, "ON");
        sendCommand(TestReceiveCommands.ITEM_SWITCH_EVENT, "OFF");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_SWITCH_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveNumberCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_NUMBER_EVENT, "123");
        sendCommand(TestReceiveCommands.ITEM_NUMBER_EVENT, "17.5");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_NUMBER_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveStringCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_STRING_EVENT, "abc");
        sendCommand(TestReceiveCommands.ITEM_STRING_EVENT, "a longer value");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_STRING_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveDatetimeCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_DATETIME_EVENT, ZonedDateTime.now().format(FORMATTER));
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_DATETIME_COMMAND);
        verifyNoError();
    }

    @Test
    public void receivePlayerCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_PLAYER_EVENT, "PLAY");
        sendCommand(TestReceiveCommands.ITEM_PLAYER_EVENT, "PAUSE");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_PLAYER_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveRollershutterCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "UP");
        sendCommand(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "DOWN");
        sendCommand(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "STOP");
        sendCommand(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "MOVE");
        sendCommand(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "0");
        sendCommand(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "50");
        sendCommand(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "100");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_ROLLERSHUTTER_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveDimmerCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_DIMMER_EVENT, "0");
        sendCommand(TestReceiveCommands.ITEM_DIMMER_EVENT, "50");
        sendCommand(TestReceiveCommands.ITEM_DIMMER_EVENT, "100");
        sendCommand(TestReceiveCommands.ITEM_DIMMER_EVENT, "ON");
        sendCommand(TestReceiveCommands.ITEM_DIMMER_EVENT, "OFF");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_DIMMER_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveColorCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_COLOR_EVENT, "1,2,3");
        sendCommand(TestReceiveCommands.ITEM_COLOR_EVENT, "359,100,100");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_COLOR_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveLocationCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_LOCATION_EVENT, "17.5,35.66");
        sendCommand(TestReceiveCommands.ITEM_LOCATION_EVENT, "1,1");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_LOCATION_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveQuantityCommand() throws IOException {
        sendCommand(TestReceiveCommands.ITEM_QUANTITY_EVENT, "25 W");
        sendCommand(TestReceiveCommands.ITEM_QUANTITY_EVENT, "0 W");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_QUANTITY_COMMAND);
        verifyNoError();
    }

    @Test
    public void receiveSwitchUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_SWITCH_EVENT, "ON");
        postUpdate(TestReceiveCommands.ITEM_SWITCH_EVENT, "OFF");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_SWITCH_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveNumberUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_NUMBER_EVENT, "123");
        postUpdate(TestReceiveCommands.ITEM_NUMBER_EVENT, "17.5");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_NUMBER_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveStringUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_STRING_EVENT, "abc");
        postUpdate(TestReceiveCommands.ITEM_STRING_EVENT, "a longer value");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_STRING_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveDatetimeUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_DATETIME_EVENT, ZonedDateTime.now().format(FORMATTER));
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_DATETIME_UPDATE);
        verifyNoError();
    }

    @Test
    public void receivePlayerUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_PLAYER_EVENT, "PLAY");
        postUpdate(TestReceiveCommands.ITEM_PLAYER_EVENT, "PAUSE");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_PLAYER_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveContactUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_CONTACT_EVENT, "OPEN");
        postUpdate(TestReceiveCommands.ITEM_CONTACT_EVENT, "CLOSED");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_CONTACT_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveRollershutterUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "UP");
        postUpdate(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "DOWN");
        postUpdate(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "0");
        postUpdate(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "50");
        postUpdate(TestReceiveCommands.ITEM_ROLLERSHUTTER_EVENT, "100");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_ROLLERSHUTTER_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveDimmerUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_DIMMER_EVENT, "0");
        postUpdate(TestReceiveCommands.ITEM_DIMMER_EVENT, "50");
        postUpdate(TestReceiveCommands.ITEM_DIMMER_EVENT, "100");
        postUpdate(TestReceiveCommands.ITEM_DIMMER_EVENT, "ON");
        postUpdate(TestReceiveCommands.ITEM_DIMMER_EVENT, "OFF");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_DIMMER_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveColorUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_COLOR_EVENT, "1,2,3");
        postUpdate(TestReceiveCommands.ITEM_COLOR_EVENT, "359,100,100");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_COLOR_UPDATE);
        verifyNoError();
    }

    @Disabled
    @Test
    public void receiveImageUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_IMAGE_EVENT,
                String.format("data:image/jpg;base64,%s", Base64.getEncoder().encodeToString(new byte[3])));
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_COLOR_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveLocationUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_LOCATION_EVENT, "17.5,35.66");
        postUpdate(TestReceiveCommands.ITEM_LOCATION_EVENT, "1,1");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_LOCATION_UPDATE);
        verifyNoError();
    }

    @Test
    public void receiveQuantityUpdate() throws IOException {
        postUpdate(TestReceiveCommands.ITEM_QUANTITY_EVENT, "25 W");
        postUpdate(TestReceiveCommands.ITEM_QUANTITY_EVENT, "0 W");
        verifyRuleWasExecuted(TestReceiveCommands.NAME_RECEIVE_QUANTITY_UPDATE);
        verifyNoError();
    }
}
