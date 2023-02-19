# Examples

## Example 1

Use Case: Invoke another item Switch from rule

```java
public class DemoRule extends JRule {
    @JRuleName("MyRuleTurnSwich2On")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void execChangedToRule() {
        logInfo("||||| --> Executing rule MyRule: changed to on");
        JRuleItems.MySwitch2.sendCommand(ON);
    }
}
```

## Example 2

Use case: Invoke a Doorbell, but only allow the rule to be invoked once every 20 seconds.
This is done by acquiring a lock getTimedLock("MyLockTestRule1", 20).

```java
public class DemoRule extends JRule {
    @JRuleName("MyLockTestRule1")
    @JRuleWhenItemChange(item = MyTestSwitch2, from = OFF, to = ON)
    public void execLockTestRule() {
        if (getTimedLock("MyLockTestRule1", 20)) {
            JRuleItems.MyDoorBellItem.sendCommand(ON);
            logInfo("||||| --> Got Lock! Ding-dong !");
        } else {
            logInfo("||||| --> Ignoring call to rule it is locked!");
        }
    }
}
```

## Example 3

Use case: Use the value that caused the trigger
When the rule is triggered, the triggered value is stored in the event.

```java
public class DemoRule extends JRule {
    @JRuleName("MyEventValueTest")
    @JRuleWhenItemReceivedCommand(item = MyTestSwitch2)
    public void myEventValueTest(JRuleEvent event) {
        logInfo("Got value from event: {}", event.getState().getValue());
    }
}
```

## Example 4

Use case: Or statement for rule trigger
To add an OR statement we simply add multiple @JRuleWhen statements

```java
public class DemoRule extends JRule {
    @JRuleName("MyNumberRule1")
    @JRuleWhenItemChange(item = MyTestNumber, from = "14", to = "10")
    @JRuleWhenItemChange(item = MyTestNumber, from = "10", to = "12")
    public void myOrRuleNumber(JRuleEvent event) {
        logInfo("Got change number: {}", event.getState().asStringValue());
        // or
        logInfo("Got change number: {}", event.getItem().getState().asStringValue());
    }
}
```

## Example 5

Use case: Define your own functionality
Create a Rules class that extends: JRuleUser.java
JRuleUser.java should be placed in the same folder as your rules
The JRuleUser class can contain common functions and functionality you want to reuse in your rules:

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRule;

public class JRuleUser extends JRule {

}
```

## Example 6

Your class rules can now extend the JRuleUser
package org.openhab.automation.jrule.rules.user;

```java
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;

import org.openhab.automation.jrule.rules.user.JRuleUser;

public class MySwitchRule extends JRuleUser {

}
```

## Example 7

Let's say we want to add a common function that should be available for all user rules.
We want to add a function that checks if it is ok to send notifications depends on what time it is.
We'll do this:

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRule;

public class JRuleUser extends JRule {

    private static final int startDay = 8;
    private static final int endDay = 21;

    protected boolean timeIsOkforDisturbance() {
        return nowHour() >= startDay && nowHour() <= endDay;
    }
}
```

We then extend the rule from the Java Rules file:

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;

public class MyTestUserRule extends JRuleUser {

    @JRuleName("TestUserDefinedRule")
    @JRuleWhenItemChange(item = MyTestSwitch, from = OFF, to = ON)
    public void mySendNotificationRUle(JRuleEvent event) {
        if (timeIsOkforDisturbance()) {
            logInfo("It's ok to send a disturbing notification");
        }
    }
}
```

## Example 8

Use case create a timer for automatically turning off a light when it is turned on. If it's running cancel it and
schedule a new one.

```java
public class DemoRule extends JRule {
    @JRuleName("myTimerRule")
    @JRuleWhenItemChange(item = MyLightSwitch, to = ON)
    public synchronized void myTimerRule(JRuleEvent event) {
        logInfo("Turning on light it will be turned off in 2 mins");
        createOrReplaceTimer(MyLightSwitch, Duration.ofMinutes(2), new Runnable() {
            @Override
            public void run() {
                logInfo("Time is up! Turning off lights");
                JRuleItems.MyLightSwitch.sendCommand(OFF);
            }
        });
    }
}
```

## Example 9

Use case: Let's say we have a 433 MHz wall socket with no ON/OFF feedback and a bit of bad radio reception. We can then
create a repeating timer
to send multiple ON statements to be sure it actually turns on.
createOrReplaceRepeatingTimer("myRepeatingTimer", 7, 4, will create a repeating timer that will trigger after 0 seconds,
7s, 14s and 21s
If the Timer is already running it will cancel it and create a new one.

```java
public class DemoRule extends JRule {
    @JRuleName("repeatRuleExample")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public synchronized void repeatRuleExample(JRuleEvent event) {
        createOrReplaceRepeatingTimer("myRepeatingTimer", 7, Duration.ofSeconds(10), new Runnable() {
            @Override
            public void run() {
                String messageOn = "repeatRuleExample Repeating.....";
                logInfo(messageOn);
                JRuleItems.MyBad433Switch.sendCommand(ON);
            });
        }
    }
```

## Example 10

Use case Create a simple timer. When MyTestSwitch turns on it will wait 10 seconds and then turn MyTestSwitch2 to on.
Note that
it will not reschedule the timer, if the timer is already running it won't reschedule it.

```java
public class DemoRule extends JRule {
    @JRuleName("timerRuleExample")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public synchronized void timerRuleExample(JRuleEvent event) {
        createTimer("myTimer", Duration.ofSeconds(10), new Runnable() {
            @Override
            public void run() {
                String messageOn = "timer example.";
                logInfo(messageOn);
                JRuleItems.MyTestWitch2.sendCommand(ON);
            });
        }
    }
```

## Example 11

Use case trigger a rule at 22:30 in the evening to set initial brightness for a ZwaveDimmer to 30%

```java
public class DemoRule extends JRule {
    @JRuleName("setDayBrightness")
    @JRuleWhenTimeTrigger(hours = 22, minutes = 30)
    public synchronized void setDayBrightness(JRuleEvent event) {
        logInfo("Setting night brightness to 30%");
        int dimLevel = 30;
        JRuleItems.MyDimmerBrightness.sendCommand(dimLevel);
    }
}
```

## Example 12

Use case: If temperature is below or equals to 20 degrees send command on to a heating fan  
It is possible to use:  
lte = less than or equals  
lt = less than  
gt = greater than  
gte = greater than or equals  
eq = equals
neq = not equals

```java
public class DemoRule extends JRule {
    @JRuleName("turnOnFanIfTemperatureIsLow")
    @JRuleWhenItemChange(item = MyTemperatureSensor, condition = @JRuleCondition(lte = 20))
    public synchronized void turnOnFanIfTemperatureIsLow(JRuleEvent event) {
        logInfo("Starting fan since temperature dropped below 20");
        JRuleItems.MyHeatingFanSwitch.sendCommand(JRuleOnOffValue.ON);
    }
}
```

## Example 13

Use case: Using say command for tts

```java
public class DemoRule extends JRule {
    @JRuleName("testSystemTts")
    @JRuleWhenItemChange(item = TestSystemTts, to = ON)
    public synchronized void testSystemTts(JRuleEvent event) {
        logInfo("System TTS Test");
        String message = "Testing tts! I hope you can hear it!";
        say(message, null, "sonos:PLAY5:RINCON_XXYY5857B06E0ZZOO");
    }
}
```

## Example 14

Use case: Executing command from CLI

```java
public class DemoRule extends JRule {
    @JRuleName("TestExecutingCommandLine")
    @JRuleWhenItemReceivedCommand(item = MySwitchGroup)
    public synchronized void testExecutingCommandLine(JRuleEvent event) {
        logInfo("Creating dummy file using CLI");
        executeCommandLine("touch", "/openhab/userdata/example.txt");
    }
}
```

## Example 15

Use case: A group of switches, see if status is changed, and also which member in the group changed state

```java
public class DemoRule extends JRule {
    @JRuleName("groupMySwitchesChanged")
    @JRuleWhenItemChange(item = MySwitchGroup)
    public synchronized void groupMySwitchGroupChanged(JRuleEvent event) {
        final boolean groupIsOnline = ((JRuleItemEvent) event).getState().getValueAsOnOffValue() == JRuleOnOffValue.ON;
        final String memberThatChangedStatus = ((JRuleItemEvent) event).getMemberName();
        logInfo("Member that changed the status of the Group of switches: {}", memberThatChangedStatus);
    }
}
```

## Example 16

Use case: A group of switches , trigger when it's changed from OFF to ON

```java
public class DemoRule extends JRule {
    @JRuleName("groupMySwitchesChangedOffToOn")
    @JRuleWhenItemChange(item = MySwitchGroup, from = OFF, to = ON)
    public synchronized void groupMySwitchesChangedOffToOn(JRuleEvent event) {
        logInfo("Member that changed the status of the Group from OFF to ON: {}", event.getMemberName());
    }
}
```

## Example 17

Use case: Listen for a Channel Trigger Event

```java
public class DemoRule extends JRule {
    @JRuleName("ChannelTriggered")
    @JRuleWhenChannelTrigger(channel = binding_thing.buttonevent)
    public synchronized void channelTriggered(JRuleEvent event) {
        logInfo("Channel triggered with value: {}", ((JRuleChannelEvent) event).getEvent());
    }
}
```

## Example 18

Use case: Cron based expression to trigger rule

```java
public class DemoRule extends JRule {
    @JRuleName("testCron")
    @JRuleWhenCronTrigger(cron = "*/5 * * * * *")
    public void testCron(JRuleEvent event) {
        logInfo("CRON: Running cron from string every 5 seconds: {}", event);
    }
}
```

## Example 19

Use case: getLastUpdated for an item  
Note that `ZonedDateTime lastUpdate = JRuleStringItem.forName(_MyCoolItem.ITEM).getLastUpdated("mapdb");`
can be called without serviceId
argument: `ZonedDateTime lastUpdate = JRuleStringItem.forName(_MyCoolItem.ITEM).getLastUpdated();`

```java
public class DemoRule extends JRule {
    @JRuleName("testLastUpdate")
    @JRuleWhenCronTrigger(cron = "4 * * * * *")
    public void testLastUpdate(JRuleEvent event) {
        logInfo("CRON: Running cron from string: {}", event.getState().getValue());
        ZonedDateTime lastUpdate = JRuleItems.MyCoolItem.getLastUpdated("mapdb");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss Z");
        String lastUpdateFormatted = lastUpdate.format(formatter);
        logInfo("Last Update: {}", lastUpdateFormatted);
    }
}
```

## Example 20

Use case: Get the brigtness from a color item, set a color item to white (HSB 0, 0, 100)

```java
public class DemoRule extends JRule {

    @JRuleName("testBrightnessFromColorItem")
    @JRuleWhenItemChange(item = MyTestColorItem)
    public void testBrightnessFromColorItem(JRuleEvent event) {
        JRuleColorValue color = JRuleItems.MyTestColorItem.getState();
        int brightness = color.getHsbValue().getBrightness();
    }

    @JRuleWhenItemChange(item = MyTestColorItem)
    public void testSetWhiteOnColorItem(JRuleEvent event) {
        JRuleItems.MyTestColorItem.sendCommand(JRuleColorValue.fromHsb(0, 0, 100));
    }
}
```

## Example 21

Use case: Set logging name for a specific rule

```java 
public class DemoRule extends JRule {

    @JRuleName("MyCustomLoggingRule")
    @JRuleLogName("MYLOG")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void execChangedToRule() {
        logInfo("||||| --> Executing rule MyRule: changed to on");
        JRuleItems.MySwitch2.sendCommand(ON);
    }
}
```

## Example 22

Use case: Override logging for all rules defined in one file

```java
public class ColorRules extends JRule {
    @JRuleName("MyCustomLoggingRuleOnClass")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void execChangedToRule() {
        logInfo("||||| --> Executing rule MyRule: changed to on");
        JRuleItems.MySwitch2.sendCommand(ON);
    }

    @Override
    protected String getRuleLogName() {
        return "CustomLogExample";
    }
}
```

## Example 23

Use case: Apply transformation using openHAB transformation service

```java
public class TransformationRule extends JRule {
    @JRuleName("MyTransformation")
    @JRuleWhenItemReceivedCommand(item = MyStringValue)
    public void applyTransformation(JRuleEvent event) {
        String transformedValue = transform("MAP(my.map):%s", event.getState().getValue());
        logInfo("Transformed {} to {}", event.getState().getValue(), transformedValue);
        JRuleItems.MyTransformationReceiver.sendCommand(transformedValue);
    }
}
```

## Example 24

Use case: Use precondition annotation in order to create "AND" logic. Example we have a switch that will tell
if it is ok for disturbance. If it is ok the switch is set to ON and we can send a notification if the notification
message is updated.

```java
public class DemoRule extends JRule {

    @JRulePrecondition(item = MyTestDisturbanceSwitch, condition = @JRuleCondition(eq = "ON"))
    @JRuleName("MyTestPreConditionRule1")
    @JRuleWhenItemReceivedCommand(item = MyMessageNotification)
    public void testPrecondition(JRuleEvent event) {
        String notificationMessage = ((JRuleItemEvent) event).getState().getValue();
        logInfo("It is ok to send notification: {}", notificationMessage);
        // JRuleItems.MySendNoticationItemMqtt.sendCommand(notificationMessage);
    }
}
```

## Example 25

Use case: Use precondition annotation in order to create "AND" logic. Example when the temperature is above 30 degrees (
celcius probably) and
a motion detector is triggered we will turn on a fan.

```java
public class DemoRule extends JRule {
    @JRulePrecondition(item = MyTestTemperatureSensor, gt = 30)
    @JRuleName("MyTestPreConditionRuleTemperature")
    @JRuleWhenItemChange(item = MyMotionDetector, from = OFF, to = ON)
    public void testPrecondition(JRuleEvent event) {
        logInfo("Temperature is above 30 and we should start the fan since the motiondetector is triggered");
        JRuleItems.MyFan.sendCommand(ON);
    }
}
```

## Example 26

Use case: Send Quantity type Watt (W) from rule.

```java
public class DemoRule extends JRule {
    @JRuleName("testQuantityPowerWatt")
    @JRuleWhenItemChange(item = MyTestMeterPower)
    public void testQuantityPower(JRuleEvent event) {
        logInfo("TestQuantity power will send this value as Watt: {}", event.getState().getValue());
        JRuleItems.TestPowerQuantityType.sendCommand(event.getState().getValueAsDouble(), "W");
    }
}
```

## Example 27

Use case: Use forName to create and item and send commands and get status

```java
public class DemoRule extends JRule {
    @JRuleName("testForName")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void testForName(JRuleEvent event) {
        JRuleSwitchItem switchItem = JRuleSwitchItem.forName("MyOtherTestSwitch");
        switchItem.sendItemCommand(OFF);
        if (switchItem.getItemStatus == ON) {
            switchItem.sendItemCommand(OFF);
        }
    }
}
```

## Example 27b

Use case: Use forNameOptional to create and item and send commands and get status

```java
public class DemoRule extends JRule {
    @JRuleName("testForNameOptional")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void testForName(JRuleEvent event) {
        JRuleSwitchItem.forNameOptional("MyOtherTestSwitch").ifPresent(item -> item.sendCommand(true));
    }
}
```

## Example 28

Use case: Get the name of the item that triggered the rule as well as new and old state value.
This can be useful if you have multiple JRuleWhen with different items, and you want to know which item
triggered the rule.

```java
public class DemoRule extends JRule {
    @JRuleName("triggerNameExample")
    @JRuleWhenItemChange(item = MyTestSwitch1, to = ON)
    @JRuleWhenItemChange(item = MyTestSwitch2, to = ON)
    public void triggerNameExample(JRuleEvent event) {
        logInfo("The rule was triggered by the following item: {}", event.getItem().getName());
        logInfo("The rule was Old Value was: {} and new value: {}", event.getOldState().getValue(), event.getState().getValue());
    }
}
```

## Example 29

Use case: get average value for a Number item last hour

```java
public class DemoRule extends JRule {
    @JRuleName("testAverageLastHour")
    @JRuleWhenCronTrigger(cron = "4 * * * * *")
    public void testAverage(JRuleEvent event) {
        Double average = JRuleNumberItem.forName(_MyNumberItem.ITEM).averageSince(ZonedDateTime.now().minus(1, ChronoUnit.HOURS));
        logInfo("Average value last hour: {}", average);
    }
}
```

## Example 30

Use case: Use generated JRuleItems.java to get hold of items. For instance get state of an item.

```java
public class DemoRule extends JRule {
    @JRuleName("testItems")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void testItems(JRuleEvent event) {
        JRuleItems.MyOtherTestSwitch.getState();
    }
}
```

## Example 31

Use case: Restart thing every night due to binding flakyness

```java
public class DemoRule extends JRule {
    @JRuleName("Restart thing every night")
    @JRuleWhenTimeTrigger(hours = 3)
    public void restartThing() {
        JRuleThings.my_flaky_thing.restart();
    }
}
```

## Example 32

Use case: Detect if a specific thing goes offline, wait for it to come online again within a given time

```java
public class DemoRule extends JRule {
    @JRuleName("Notify if thing stays offline")
    @JRuleWhenThingTrigger(thing = remoteopenhab_thing.ID, from = JRuleThingStatus.ONLINE)
    public void warnIfThingStaysOffline() {
        createOrReplaceTimer("MY_TIMER", Duration.ofMinutes(3), () -> {
            if (JRuleThings.remoteopenhab_thing.getStatus() != JRuleThingStatus.ONLINE) {
                logWarn("Thing {} is still offline, restarting", remoteopenhab_thing.ID);
                JRuleThings.remoteopenhab_thing.restart();
            }
        });
    }
}
```

## Example 33

Use case: Listen for thing status events on _all_ things

```java
public class DemoRule extends JRule {
    @JRuleName("Log every thing that goes offline")
    @JRuleWhenThingTrigger(from = JRuleThingStatus.ONLINE)
    public void startTrackingNonOnlineThing(JRuleEvent event) {
        String offlineThingUID = event.getThing();
        // ...
    }
}
```

## Example 34

Use case: Thing actions, send message with pushover and other services.
Note that you will have to set up a pusheover account as thing in openHAB.

```java
public class DemoRule extends JRule {
    @JRuleName("PushOverTest")
    @JRuleWhenItemChange(item = MyTestSendPushOverButton, to = ON)
    public void testPower(JRuleEvent event) {
        logInfo("Sending Test message using pushover via actions");
        JRuleActions.pushoverPushoverAccountXYZ.sendMessage("MyMessage", "MyTitle");
    }
}
```

## Example 35

Use case: Want to listen on all Item events of a group (without the groupstate must change).
Alternatively you could just listen to just Group changes or (real) Item changes

```java
public class DemoRule extends JRule {
    @JRuleName("MemberOfUpdateTrigger")
    @JRuleWhenItemReceivedUpdate(item = MySwitchGroup, memberOf = JRuleMemberOf.All)
//@JRuleWhenItemReceivedUpdate(item = MySwitchGroup, memberOf = JRuleMemberOf.Items) // .. or this
//@JRuleWhenItemReceivedUpdate(item = MySwitchGroup, memberOf = JRuleMemberOf.Groups) // .. or this
    public synchronized void memberOfUpdateTrigger(JRuleItemEvent event) {
        final String memberThatChangedStatus = event.getMemberName();
        logInfo("Member that changed the status of the Group of switches: {}", memberThatChangedStatus);
    }
}
```

## Example 36

Use case: Want to listen just on changes where the state is now greater/equals then 12 and was before less then 12.
Without the previous condition the rule will be triggered every time the state is greater/equals then 12.

```java
public class DemoRule extends JRule {
    @JRuleName("Change from something less to something greater")
    @JRuleWhenItemChange(item = ITEM_FROM_TO, previousCondition = @JRuleCondition(lt = 12), condition = @JRuleCondition(gte = 12))
    public void itemChangeFromTo(JRuleEvent event) {
        logInfo("state change to something >= 12 and was before < 12");
    }
}
```

## Example 37

Use case: Chain timers. Execute one and after this is expired, execute the next one.

```java
public class DemoRule extends JRule {
    @JRuleName("Notify if thing stays offline")
    @JRuleWhenItemChange(item = MySwitchGroup)
    public void chainSomeTimers() {
        createTimer(Duration.ofSeconds(3), () -> {
            logInfo("First timer finished after 3 seconds");
        }).createTimerAfter(Duration.ofSeconds(10), () -> {
            logInfo("Second timer finished after 10 more seconds");
        });
    }
}
```

## Example 38

Use case: Do not execute a rule too often

```java
public class DemoRule extends JRule {
    @JRuleDebounce(10)
    @JRuleName("Notify if thing stays offline")
    @JRuleWhenItemChange(item = MySwitchGroup)
    public void debounceMethod() {
        // super critical stuff which shouldn't be called too often
    }
}
```

## Example 39

Use case: Send some requests to http endpoints

```java
public class DemoRule extends JRule {
    @JRuleName("send http methods")
    @JRuleWhenItemReceivedCommand(item = MyHttpTrigger, command = "send http calls")
    public void sendHttpCalls() {
        String responseGet = sendHttpGetRequest("http://http-mock:8080" + HTTP_GET_SOMETHING, null);
        logInfo("send Http: {}", responseGet);
        sendHttpDeleteRequest("http://http-mock:8080" + HTTP_DELETE_SOMETHING, Duration.ofSeconds(5));
    }
}
```

## Example 40

Use case: Execute a rule delayed

```java
public class DemoRule extends JRule {
    @JRuleDelayed(10)
    @JRuleName("Execute after ten seconds")
    @JRuleWhenItemChange(item = MySwitchGroup)
    public void delayedMethod() {
        // delay the execution of this
    }
}

```
