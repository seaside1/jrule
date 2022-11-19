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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.openhab.automation.jrule.exception.JRuleExecutionException;
import org.openhab.automation.jrule.items.JRuleColorItem;
import org.openhab.automation.jrule.items.JRuleContactItem;
import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.items.JRuleImageItem;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.JRuleLocationItem;
import org.openhab.automation.jrule.items.JRuleNumberGroupItem;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.items.JRuleRollershutterItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchGroupItem;
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
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRulePointValue;
import org.openhab.automation.jrule.rules.value.JRuleRawValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
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
    public static final String ITEM_GET_MEMBERS_OF_GROUP_SWITCH = "Get_Members_Of_Group_Switch";
    public static final String NAME_GET_MEMBERS_OF_GROUP = "get members of group";
    public static final String NAME_GET_MEMBERS_OF_NUMBER_GROUP = "get members of number group";
    public static final String ITEM_CAST_ALL_TYPES_SWITCH = "Cast_All_Types_Switch";
    public static final String NAME_CAST_ALL_TYPES = "cast all types";
    public static final String ITEM_SWITCH_TO_CAST = "Switch_To_Cast";
    public static final String ITEM_NUMBER_TO_CAST = "Number_To_Cast";
    public static final String ITEM_DIMMER_TO_CAST = "Dimmer_To_Cast";
    public static final String ITEM_COLOR_TO_CAST = "Color_To_Cast";
    public static final String ITEM_STRING_TO_CAST = "String_To_Cast";
    public static final String ITEM_DATETIME_TO_CAST = "DateTime_To_Cast";
    public static final String ITEM_PLAYER_TO_CAST = "Player_To_Cast";
    public static final String ITEM_CONTACT_TO_CAST = "Contact_To_Cast";
    public static final String ITEM_IMAGE_TO_CAST = "Image_To_Cast";
    public static final String ITEM_ROLLERSHUTTER_TO_CAST = "Rollershutter_To_Cast";
    public static final String ITEM_LOCATION_TO_CAST = "Location_To_Cast";
    private static final String ITEM_NUMBER_GROUP = "Number_Group";

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
        logInfo("will invoke mqtt action");
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

    @JRuleName(NAME_MEMBER_OF_GROUP_RECEIVED_COMMAND)
    @JRuleWhenItemReceivedCommand(item = ITEM_SWITCH_GROUP, memberOf = true)
    public synchronized void memberOfGroupReceivedCommand(JRuleItemEvent event) {
        logInfo("Member of Group ({}) received command", event.getMemberName());
    }

    @JRuleName(NAME_MEMBER_OF_GROUP_RECEIVED_UPDATE)
    @JRuleWhenItemReceivedUpdate(item = ITEM_SWITCH_GROUP, memberOf = true)
    public synchronized void memberOfGroupReceivedUpdate(JRuleItemEvent event) {
        final String memberThatChangedStatus = event.getMemberName();
        logInfo("Member of Group ({}) received update", event.getMemberName());
    }

    @JRuleName(NAME_MEMBER_OF_GROUP_CHANGED)
    @JRuleWhenItemChange(item = ITEM_SWITCH_GROUP, memberOf = true)
    public synchronized void memberOfGroupChanged(JRuleItemEvent event) {
        final String memberThatChangedStatus = event.getMemberName();
        logInfo("Member of Group ({}) changed", event.getMemberName());
    }

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

    @JRuleName(NAME_GET_MEMBERS_OF_GROUP)
    @JRuleWhenItemReceivedCommand(item = ITEM_GET_MEMBERS_OF_GROUP_SWITCH)
    public void getMembersOfGroup(JRuleItemEvent event) throws JRuleExecutionException {
        Set<JRuleItem<? extends JRuleValue>> members = JRuleSwitchGroupItem.forName(ITEM_SWITCH_GROUP).memberItems();
        if (members.size() != 2) {
            throw new JRuleExecutionException("expected 2 childs");
        }
        logInfo("contains members: {}", members.stream()
                .map(jRuleItem -> jRuleItem.getName() + ":" + jRuleItem.getType()).collect(Collectors.joining(", ")));
    }

    @JRuleName(NAME_GET_MEMBERS_OF_NUMBER_GROUP)
    @JRuleWhenItemReceivedCommand(item = ITEM_GET_MEMBERS_OF_GROUP_SWITCH)
    public void getMembersOfNumberGroup(JRuleItemEvent event) throws JRuleExecutionException {
        Set<JRuleItem<? extends JRuleValue>> members = JRuleNumberGroupItem.forName(ITEM_NUMBER_GROUP).memberItems();
        if (members.size() != 2) {
            throw new JRuleExecutionException("expected 2 childs");
        }
        logInfo("contains members: {}", members.stream()
                .map(jRuleItem -> jRuleItem.getName() + ":" + jRuleItem.getType()).collect(Collectors.joining(", ")));

        Set<JRuleItem<? extends JRuleValue>> recursiveMembers = JRuleNumberGroupItem.forName(ITEM_NUMBER_GROUP)
                .memberItems(true);
        if (recursiveMembers.size() != 4) {
            throw new JRuleExecutionException("expected 4 childs");
        }

        Set<JRuleItem<? extends JRuleValue>> recursiveNonGroupMembers = recursiveMembers.stream()
                .filter(jRuleItem -> !jRuleItem.isGroup()).collect(Collectors.toSet());
        if (recursiveNonGroupMembers.size() != 3) {
            throw new JRuleExecutionException("expected 3 childs");
        }

        logInfo("contains recursive members: {}", recursiveMembers.stream()
                .map(jRuleItem -> jRuleItem.getName() + ":" + jRuleItem.getType()).collect(Collectors.joining(", ")));
    }

    @JRuleName(NAME_CAST_ALL_TYPES)
    @JRuleWhenItemReceivedCommand(item = ITEM_CAST_ALL_TYPES_SWITCH)
    public void castAllTypes(JRuleItemEvent event) throws JRuleExecutionException {
        castSwitch();
        castNumber();
        castString();
        castDateTime();
        castPlayer();
        castContact();

        // TODO: strange error in OH when using this
        // castImage();
        castRollershutter();
        castDimmer();
        castColor();
        castLocation();
    }

    private static void castLocation() {
        JRuleLocationItem locationItem = JRuleLocationItem.forName(ITEM_LOCATION_TO_CAST);

        locationItem.sendCommand(new JRulePointValue(37.33D, 11.12D));
        assert locationItem.getState().getLatitude().doubleValue() == 37.33D;
        assert locationItem.getState().getLongitude().doubleValue() == 11.12D;

        locationItem.sendCommand(new JRulePointValue(78.78D, 55.66D));
        assert locationItem.getState().getLatitude().doubleValue() == 78.78D;
        assert locationItem.getState().getLongitude().doubleValue() == 55.66D;
    }

    private static void castRollershutter() {
        JRuleRollershutterItem rollershutterItem = JRuleRollershutterItem.forName(ITEM_ROLLERSHUTTER_TO_CAST);

        rollershutterItem.sendCommand(17);
        assert rollershutterItem.getState().doubleValue() == 17;
        assert rollershutterItem.getStateAs(JRulePercentValue.class).doubleValue() == 17;
        assert rollershutterItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.ON;

        rollershutterItem.sendCommand(0);
        assert rollershutterItem.getState().doubleValue() == 0;
        assert rollershutterItem.getStateAs(JRulePercentValue.class).doubleValue() == 0;
        assert rollershutterItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.OFF;
    }

    private static void castImage() {
        JRuleImageItem imageItem = JRuleImageItem.forName(ITEM_IMAGE_TO_CAST);

        imageItem.sendCommand(new JRuleRawValue("jpg", new byte[16]));
        assert imageItem.getState().getMimeType().equals("jpg");
        assert Arrays.equals(imageItem.getState().getData(), new byte[16]);

        imageItem.sendCommand(new JRuleRawValue("png", new byte[8]));
        assert imageItem.getState().getMimeType().equals("png");
        assert Arrays.equals(imageItem.getState().getData(), new byte[8]);
    }

    private static void castContact() {
        JRuleContactItem contactItem = JRuleContactItem.forName(ITEM_CONTACT_TO_CAST);

        contactItem.sendCommand(JRuleOpenClosedValue.OPEN);
        assert contactItem.getState() == JRuleOpenClosedValue.OPEN;
        assert contactItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.ON;

        contactItem.sendCommand(JRuleOpenClosedValue.CLOSED);
        assert contactItem.getState() == JRuleOpenClosedValue.CLOSED;
        assert contactItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.OFF;
    }

    private static void castPlayer() {
        JRulePlayerItem playerItem = JRulePlayerItem.forName(ITEM_PLAYER_TO_CAST);

        playerItem.sendCommand(JRulePlayPauseValue.PLAY);
        assert playerItem.getState() == JRulePlayPauseValue.PLAY;
        assert playerItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.ON;

        playerItem.sendCommand(JRulePlayPauseValue.PAUSE);
        assert playerItem.getState() == JRulePlayPauseValue.PAUSE;
        assert playerItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.OFF;
    }

    private static void castDateTime() {
        JRuleDateTimeItem dateTimeItem = JRuleDateTimeItem.forName(ITEM_DATETIME_TO_CAST);

        ZonedDateTime date = ZonedDateTime.of(2021, 12, 22, 12, 17, 10, 0, ZoneId.systemDefault());
        dateTimeItem.sendCommand(date);
        assert dateTimeItem.getState().getValue().equals(date);

        ZonedDateTime date2 = ZonedDateTime.of(2050, 12, 22, 12, 17, 10, 0, ZoneId.systemDefault());
        dateTimeItem.sendCommand(date2);
        assert dateTimeItem.getState().getValue().equals(date2);
    }

    private static void castString() {
        JRuleStringItem stringItem = JRuleStringItem.forName(ITEM_STRING_TO_CAST);

        stringItem.sendCommand("abc");
        assert stringItem.getState().stringValue().equals("abc");
        assert stringItem.getState().getValue().equals("abc");

        stringItem.sendCommand("xyz");
        assert stringItem.getState().stringValue().equals("xyz");
        assert stringItem.getState().getValue().equals("xyz");
    }

    private static void castColor() {
        JRuleColorItem colorItem = JRuleColorItem.forName(ITEM_COLOR_TO_CAST);

        colorItem.sendCommand(new JRuleHsbValue(22, 17, 99));
        assert colorItem.getState().getHue().intValue() == 22;
        assert colorItem.getState().getSaturation().intValue() == 17;
        assert colorItem.getState().getBrightness().intValue() == 99;
        assert colorItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.ON;

        colorItem.sendCommand(new JRuleHsbValue(56, 77, 0));
        assert colorItem.getState().getHue().intValue() == 56;
        assert colorItem.getState().getSaturation().intValue() == 77;
        assert colorItem.getState().getBrightness().intValue() == 0;
        assert colorItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.OFF;
    }

    private static void castDimmer() {
        JRuleDimmerItem numberItem = JRuleDimmerItem.forName(ITEM_DIMMER_TO_CAST);

        numberItem.sendCommand(0);
        assert numberItem.getState().doubleValue() == 0;
        assert numberItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.OFF;

        numberItem.sendCommand(22);
        assert numberItem.getState().doubleValue() == 22;
        assert numberItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.ON;
    }

    private static void castNumber() {
        JRuleNumberItem numberItem = JRuleNumberItem.forName(ITEM_NUMBER_TO_CAST);

        numberItem.sendCommand(0);
        assert numberItem.getState().doubleValue() == 0;
        assert numberItem.getState().intValue() == 0;
        assert numberItem.getState().floatValue() == 0;
        assert numberItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.OFF;

        numberItem.sendCommand(22);
        assert numberItem.getState().doubleValue() == 22;
        assert numberItem.getState().intValue() == 22;
        assert numberItem.getState().floatValue() == 22;
        assert numberItem.getStateAs(JRuleOnOffValue.class) == JRuleOnOffValue.ON;
    }

    private static void castSwitch() {
        JRuleSwitchItem switchItem = JRuleSwitchItem.forName(ITEM_SWITCH_TO_CAST);

        switchItem.sendCommand(JRuleOnOffValue.ON);
        assert switchItem.getState() == JRuleOnOffValue.ON;
        assert switchItem.getStateAs(JRuleDecimalValue.class).doubleValue() == 100D;

        switchItem.sendCommand(JRuleOnOffValue.OFF);
        assert switchItem.getState() == JRuleOnOffValue.OFF;
        assert switchItem.getStateAs(JRuleDecimalValue.class).doubleValue() == 0D;
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
