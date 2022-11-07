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
package org.openhab.automation.jrule.rules.user;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.IntFunction;

import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleCondition;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRulePrecondition;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRuleWhenThingTrigger;
import org.openhab.automation.jrule.rules.event.JRuleChannelEvent;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.event.JRuleThingEvent;
import org.openhab.automation.jrule.rules.event.JRuleTimerEvent;
import org.openhab.automation.jrule.things.JRuleThingStatus;

/**
 * The {@link TestRules}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class TestRules extends JRule {
    public static final String ITEM_RECEIVING_COMMAND_SWITCH = "Receiving_Command_Switch";
    public static final String ITEM_MQTT_ACTION_TRIGGER = "Mqtt_Action_Trigger";
    public static final String NAME_SWITCH_ITEM_RECEIVED_ANY_COMMAND = "Switch-Item Receiving Any Command";
    public static final String NAME_SWITCH_ITEM_RECEIVED_ANY_UPDATE = "Switch-Item Receiving Any Update";
    public static final String NAME_SWITCH_ITEM_CHANGED = "Switch-Item Changed";
    public static final String NAME_SWITCH_ITEM_RECEIVED_ON_COMMAND = "Switch-Item Receiving ON Command";
    public static final String NAME_SWITCH_ITEM_RECEIVED_ON_UPDATE = "Switch-Item Receiving ON Update";
    public static final String NAME_SWITCH_ITEM_CHANGED_TO_ON = "Switch-Item Changed To ON";
    public static final String NAME_INVOKE_MQTT_ACTION = "Invoke Mqtt Action";
    public static final String ITEM_COMMANDLINE_EXEC_TRIGGER = "Commandline_Exec_Trigger";
    public static final String NAME_EXEC_COMMAND_LINE = "Exec Command Line";
    public static final String NAME_MQTT_CHANNEL_TRIGGERED = "Mqtt Channel Triggered";
    public static final String NAME_MQTT_THING_CHANGED_TO_OFFLINE = "Mqtt Thing Changed To Offline";
    public static final String NAME_MEMBER_OF_GROUP_RECEIVED_COMMAND = "Member Of Group Received Command";
    public static final String NAME_MEMBER_OF_GROUP_RECEIVED_UPDATE = "Member Of Group Received Update";
    public static final String NAME_MEMBER_OF_GROUP_CHANGED = "Member Of Group Changed";
    public static final String ITEM_SWITCH_GROUP = "Switch_Group";
    public static final String ITEM_SWITCH_GROUP_MEMBER1 = "Switch_Group_Member1";
    public static final String ITEM_SWITCH_GROUP_MEMBER2 = "Switch_Group_Member2";
    public static final String ITEM_NUMBER_CONDITION = "Number_Condition";
    public static final String NAME_PRECONDITION_LTE_AND_GTE_FOR_NUMBER = "Precondition lte and gte for Number";
    public static final String NAME_CRON_EVERY_5_SEC = "Cron every 5sec";
    public static final String ITEM_PRECONDITION_STRING = "Precondition_String";
    public static final String ITEM_PRECONDITIONED_SWITCH = "Preconditioned_Switch";
    public static final String NAME_PRECONDITION_EXECUTION = "precondition execution";

    @JRuleName(NAME_SWITCH_ITEM_RECEIVED_ANY_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_RECEIVING_COMMAND_SWITCH)
    public void switchItemReceivedCommand(JRuleItemEvent event) {
        logInfo("received command: {}", event.getState().getValue());
    }

    @JRuleName(NAME_SWITCH_ITEM_RECEIVED_ON_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_RECEIVING_COMMAND_SWITCH, command = JRuleSwitchItem.ON)
    public void switchReceivedOnCommand(JRuleItemEvent event) {
        logInfo("received command: {}", event.getState().getValue());
    }

    @JRuleName(NAME_SWITCH_ITEM_RECEIVED_ANY_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_RECEIVING_COMMAND_SWITCH)
    public void switchItemReceivedUpdate(JRuleItemEvent event) {
        logInfo("received update: {}", event.getState().getValue());
    }

    @JRuleName(NAME_SWITCH_ITEM_RECEIVED_ON_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_RECEIVING_COMMAND_SWITCH, state = JRuleSwitchItem.ON)
    public void switchReceivedOnUpdate(JRuleItemEvent event) {
        logInfo("received update: {}", event.getState().getValue());
    }

    @JRuleName(NAME_SWITCH_ITEM_CHANGED)
    @JRuleWhenItemChange(item = ITEM_RECEIVING_COMMAND_SWITCH)
    public void switchItemChanged(JRuleItemEvent event) {
        logInfo("changed from '{}' to '{}'", event.getOldState().getValue(), event.getState().getValue());
    }

    @JRuleName(NAME_SWITCH_ITEM_CHANGED_TO_ON)
    @JRuleWhenItemChange(item = ITEM_RECEIVING_COMMAND_SWITCH, from = JRuleSwitchItem.OFF, to = JRuleSwitchItem.ON)
    public void switchReceivedChangedToOn(JRuleItemEvent event) {
        logInfo("changed: {}", event.getState().getValue());
    }

    @JRuleName(NAME_INVOKE_MQTT_ACTION)
    @JRuleWhenItemChange(item = ITEM_MQTT_ACTION_TRIGGER)
    public void invokeMqttAction(JRuleItemEvent event) throws ClassNotFoundException, NoSuchFieldException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        invokeAction("mqttBrokerMqtt", "publishMQTT", "number/state", "1313131");
        logInfo("mqtt action invoked");
    }

    @JRuleName(NAME_EXEC_COMMAND_LINE)
    @JRuleWhenItemReceivedCommand(item = ITEM_COMMANDLINE_EXEC_TRIGGER)
    public void executeCommandLine(JRuleItemEvent event) {
        executeCommandLine("touch", "/openhab/userdata/example.txt");
        logInfo("created example.txt");
    }

    @JRuleName(NAME_MQTT_CHANNEL_TRIGGERED)
    @JRuleWhenChannelTrigger(channel = "mqtt:topic:mqtt:generic:numberTrigger")
    public void mqttChannelTriggered(JRuleChannelEvent event) {
        logInfo("Channel triggered with value: {}", event.getEvent());
    }

    @JRuleName(NAME_MQTT_THING_CHANGED_TO_OFFLINE)
    @JRuleWhenThingTrigger(to = JRuleThingStatus.OFFLINE)
    public void mqttThingChangedToOffline(JRuleThingEvent event) {
        logInfo("thing '{}' goes '{}'", event.getThing(), event.getStatus());
    }

    // currently not merged
    // @JRuleName(NAME_MEMBER_OF_GROUP_RECEIVED_COMMAND)
    // @JRuleWhenItemReceivedCommand(item = ITEM_SWITCH_GROUP, memberOf = true)
    // public synchronized void memberOfGroupReceivedCommand(JRuleItemEvent event) {
    // logInfo("Member of Group ({}) received command", event.getMemberName());
    // }
    //
    // @JRuleName(NAME_MEMBER_OF_GROUP_RECEIVED_UPDATE)
    // @JRuleWhenItemReceivedUpdate(item = ITEM_SWITCH_GROUP, memberOf = true)
    // public synchronized void memberOfGroupReceivedUpdate(JRuleItemEvent event) {
    // final String memberThatChangedStatus = event.getMemberName();
    // logInfo("Member of Group ({}) received update", event.getMemberName());
    // }
    //
    // @JRuleName(NAME_MEMBER_OF_GROUP_CHANGED)
    // @JRuleWhenItemChange(item = ITEM_SWITCH_GROUP, memberOf = true)
    // public synchronized void memberOfGroupChanged(JRuleItemEvent event) {
    // final String memberThatChangedStatus = event.getMemberName();
    // logInfo("Member of Group ({}) changed", event.getMemberName());
    // }

    @JRuleName(NAME_PRECONDITION_LTE_AND_GTE_FOR_NUMBER)
    @JRuleWhenItemChange(item = ITEM_NUMBER_CONDITION, condition = @JRuleCondition(lte = 20, gte = 18))
    public synchronized void conditionLteAndGteForNumber(JRuleItemEvent event) {
        logInfo("trigger when between 18 and 20, current: {}", event.getState().getValue());
    }

    @JRuleName(NAME_CRON_EVERY_5_SEC)
    @JRuleWhenCronTrigger(cron = "*/5 * * * * *")
    public void cronEvery5Sec(JRuleTimerEvent event) {
        logInfo("cron triggered", event);
    }

    @JRulePrecondition(item = ITEM_PRECONDITION_STRING, condition = @JRuleCondition(eq = "that matches"))
    @JRuleName(NAME_PRECONDITION_EXECUTION)
    @JRuleWhenItemReceivedCommand(item = ITEM_PRECONDITIONED_SWITCH)
    public void preconditionExecution(JRuleItemEvent event) {
        logInfo("received command: {}", event.getState().getValue());
    }

    private static void invokeAction(String fieldName, String methodName, Object... args) throws ClassNotFoundException,
            NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> jRuleActionsClass = Class.forName("org.openhab.automation.jrule.generated.actions.JRuleActions");
        Field mqttBrokerMqttField = jRuleActionsClass.getDeclaredField(fieldName);
        Object fieldInstance = mqttBrokerMqttField.get(null);
        Method actionMethod = fieldInstance.getClass().getDeclaredMethod(methodName,
                Arrays.stream(args).map(Object::getClass).toArray((IntFunction<Class<?>[]>) Class[]::new));
        actionMethod.invoke(fieldInstance, args);
    }
}
