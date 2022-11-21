/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.rules.integration_test;

import org.awaitility.Awaitility;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junitpioneer.jupiter.RetryingTest;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.TestRules;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * The {@link ITJRule}
 *
 * @author Robert Delbrück - Initial contribution
 */
// Please do not reuse items for testing, create new ones, otherwise every change will hurt sometimes
// RetryingTest is used because sometimes the container log output splits a logline into 2 lines and therefor the
// verifyRuleExectuted will not work.
public class ITJRule extends JRuleITBase {
    @RetryingTest(2)
    public void switchItemReceiveCommand() throws IOException {
        sendCommand(TestRules.ITEM_RECEIVING_COMMAND_SWITCH, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_SWITCH_ITEM_RECEIVED_ANY_COMMAND);
        verifyRuleWasExecuted(TestRules.NAME_SWITCH_ITEM_RECEIVED_ANY_UPDATE);
        verifyRuleWasExecuted(TestRules.NAME_SWITCH_ITEM_CHANGED);
    }

    @RetryingTest(2)
    public void switchItemReceiveOnCommand() throws IOException {
        sendCommand(TestRules.ITEM_RECEIVING_COMMAND_SWITCH, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_SWITCH_ITEM_RECEIVED_ON_COMMAND);
        verifyRuleWasExecuted(TestRules.NAME_SWITCH_ITEM_RECEIVED_ON_UPDATE);
        verifyRuleWasExecuted(TestRules.NAME_SWITCH_ITEM_CHANGED_TO_ON);
    }

    @RetryingTest(2)
    public void invokeMqttAction() throws IOException {
        sendCommand(TestRules.ITEM_MQTT_ACTION_TRIGGER, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_INVOKE_MQTT_ACTION);
        verifyMqttMessageReceived("1313131");
    }

    @RetryingTest(2)
    public void executeCommandLine() throws IOException, InterruptedException {
        sendCommand(TestRules.ITEM_COMMANDLINE_EXEC_TRIGGER, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_EXEC_COMMAND_LINE);
        verifyFileExist();
    }

    @RetryingTest(2)
    public void mqttChannelTriggered() throws MqttException {
        publishMqttMessage("number/state", "123");
        verifyRuleWasExecuted(TestRules.NAME_MQTT_CHANNEL_TRIGGERED);
    }

    @RetryingTest(2)
    public void preconditionExecution() throws IOException, InterruptedException {
        sendCommand(TestRules.ITEM_PRECONDITION_STRING, "will not match");
        sendCommand(TestRules.ITEM_PRECONDITIONED_SWITCH, JRuleSwitchItem.ON);
        verifyRuleWasNotExecuted(TestRules.NAME_PRECONDITION_EXECUTION);

        sendCommand(TestRules.ITEM_PRECONDITION_STRING, "that matches");
        Thread.sleep(500);
        sendCommand(TestRules.ITEM_PRECONDITIONED_SWITCH, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_PRECONDITION_EXECUTION);
    }

    @RetryingTest(2)
    public void mqttThingChangedToOffline() {
        Awaitility.await().with().pollDelay(1, TimeUnit.SECONDS).timeout(20, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS).await("thing online")
                .until(() -> getThingState("mqtt:topic:mqtt:generic"), s -> s.equals("ONLINE"));
        mqttProxy.setConnectionCut(true);
        Awaitility.await().with().pollDelay(1, TimeUnit.SECONDS).timeout(30, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS).await("thing offline")
                .until(() -> getThingState("mqtt:topic:mqtt:generic"), s -> s.equals("OFFLINE"));
        verifyRuleWasExecuted(TestRules.NAME_MQTT_THING_CHANGED_TO_OFFLINE);
    }

    @RetryingTest(2)
    public void memberOfGroupReceivedCommand() throws IOException {
        sendCommand(TestRules.ITEM_SWITCH_GROUP_MEMBER1, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_COMMAND);
        clearLog();
        sendCommand(TestRules.ITEM_SWITCH_GROUP_MEMBER1, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_COMMAND);
        clearLog();
        sendCommand(TestRules.ITEM_SWITCH_GROUP_MEMBER2, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_COMMAND);
        clearLog();
        sendCommand(TestRules.ITEM_SWITCH_GROUP_MEMBER2, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_COMMAND);
    }

    @RetryingTest(2)
    public void memberOfGroupReceivedUpdate() throws IOException {
        postUpdate(TestRules.ITEM_SWITCH_GROUP_MEMBER1, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_UPDATE);
        clearLog();
        postUpdate(TestRules.ITEM_SWITCH_GROUP_MEMBER1, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_UPDATE);
        clearLog();
        postUpdate(TestRules.ITEM_SWITCH_GROUP_MEMBER2, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_UPDATE);
        clearLog();
        postUpdate(TestRules.ITEM_SWITCH_GROUP_MEMBER2, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_RECEIVED_UPDATE);
    }

    @RetryingTest(2)
    public void memberOfGroupChanged() throws IOException {
        postUpdate(TestRules.ITEM_SWITCH_GROUP_MEMBER1, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_CHANGED);
        clearLog();
        postUpdate(TestRules.ITEM_SWITCH_GROUP_MEMBER2, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_MEMBER_OF_GROUP_CHANGED);
    }

    @RetryingTest(2)
    public void conditionLteAndGteForNumber() throws IOException {
        sendCommand(TestRules.ITEM_NUMBER_CONDITION, "21");
        verifyRuleWasNotExecuted(TestRules.NAME_PRECONDITION_LTE_AND_GTE_FOR_NUMBER);
        clearLog();
        sendCommand(TestRules.ITEM_NUMBER_CONDITION, "17");
        verifyRuleWasNotExecuted(TestRules.NAME_PRECONDITION_LTE_AND_GTE_FOR_NUMBER);
        clearLog();
        sendCommand(TestRules.ITEM_NUMBER_CONDITION, "20");
        verifyRuleWasExecuted(TestRules.NAME_PRECONDITION_LTE_AND_GTE_FOR_NUMBER);
        sendCommand(TestRules.ITEM_NUMBER_CONDITION, "18");
        verifyRuleWasExecuted(TestRules.NAME_PRECONDITION_LTE_AND_GTE_FOR_NUMBER);
    }

    @RetryingTest(2)
    public void membersOfGroup() throws IOException {
        sendCommand(TestRules.ITEM_GET_MEMBERS_OF_GROUP_SWITCH, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_GET_MEMBERS_OF_GROUP);
        verifyNoError();
    }

    @RetryingTest(2)
    public void membersOfNumberGroup() throws IOException {
        sendCommand(TestRules.ITEM_GET_MEMBERS_OF_GROUP_SWITCH, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_GET_MEMBERS_OF_NUMBER_GROUP);
        verifyNoError();
    }

    @RetryingTest(2)
    public void cronEvery5Sec() {
        verifyRuleWasExecuted(TestRules.NAME_CRON_EVERY_5_SEC);
    }

    @RetryingTest(2)
    public void castAllTypes() throws IOException {
        sendCommand(TestRules.ITEM_CAST_ALL_TYPES_SWITCH, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_CAST_ALL_TYPES);

        verifyStateChangeEventFor(TestRules.ITEM_SWITCH_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_SWITCH_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_NUMBER_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_NUMBER_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_DIMMER_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_DIMMER_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_COLOR_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_COLOR_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_STRING_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_STRING_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_DATETIME_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_DATETIME_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_PLAYER_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_PLAYER_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_CONTACT_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_CONTACT_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_ROLLERSHUTTER_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_ROLLERSHUTTER_TO_CAST);

        verifyStateChangeEventFor(TestRules.ITEM_LOCATION_TO_CAST);
        verifyCommandEventFor(TestRules.ITEM_LOCATION_TO_CAST);

        verifyNoError();
    }

    @RetryingTest(2)
    public void triggerRuleFromRule() throws IOException {
        sendCommand(TestRules.ITEM_RULE_FROM_RULE, JRuleSwitchItem.ON);
        verifyRuleWasExecuted(TestRules.NAME_TRIGGER_RULE_FROM_RULE);
        verifyRuleWasExecuted(TestRules.NAME_TRIGGER_ANOTHER_RULE);
        verifyNoError();
    }

    @RetryingTest(2)
    public void nullTesting() throws IOException {
        sendCommand(TestRules.ITEM_TRIGGER_RULE, TestRules.COMMAND_NULL_TESTING);
        verifyRuleWasExecuted(TestRules.NAME_NULL_TESTING);
        verifyNoError();
    }
}
