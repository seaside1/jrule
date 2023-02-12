# openHAB Rules using Java

The JRule Automation Addon aims to enable Java development of openHAB Rules. The automation addon will allow the user to create custom openHAB rules
in one or several .java- or jar-files. The Java Rules will need defined triggers in order for the engine to know how and when to execute them. The triggers are very similar to the triggers in Rules DSL but expressed using java annotations. Rules tend to be written to trigger on changes to either items or things. The addon is compatible with items and things added either in the openHAB GUI or defined in plain .items and .thing files.
JRule will generate java-source files for items and things as well as compiling and package them into a jrule-generated.jar file. The jrule-generated.jar file should be used when the user is developing openHAB rules.

The syntax for rules as well as the design and thinking behind the automation addon is to provide something that is similar to Rules DSL but more powerful, customizable and flexible. JRule relies on strict typing where you are less likely to construct rules that are not working due to syntax error.


# Limitations
- Not supporting OH3 GUI rules, script conditions 

# Why
 - You will be able to use a standard Java IDE to develop your rules. 
 - Full auto-completion (Shift space) for all items, less chance of errors and typos
 - Take full advantage of all java design patters
 - Share and reuse code for you rules
 - Advanced timers and locks are built in and can be used without cluttering the code
 - Possibility to write junit-tests to test your rules
 - Use any 3rd party dependencies and libraries in your rules
 - You will be able to use JRule in parallel with any other Rules engine if you want to give it a try
 - Compile and build your rules with tools such as maven, or provide rules in plain-java files
 - Utilize thing actions and trigger on thing statuses 
 - Reuse you methods and code for many different purposes, reducing the amount of code you have to write.
 - Advanced logging can be used with for instance logstash using MDC-tags

# Who

This addon is not for beginners, you should have knowledge in writing java-programs or a desire to do so.
As you can see in the examples below, rules will become short and readable making it easy to understand once you learn how to write the rules.

# Maturity

Beta, still major changes.

# Download

Prebuilt jar file is available under https://github.com/seaside1/jrule/releases

# Java Rule Engine

Input plain java-rules files under:
/etc/automation/jrule/rules/org/openhab/automation/jrule/rules/user/

It is also possible to add rules as pre-compiled jar files under:
/etc/automation/jrule/rules-jar/

Output jar files to be added by the user as dependencies when doing rule development will be located under:
/etc/openhab/automation/jrule/jar

Add external dependencies as jar-files under:
/etc/openhab/automation/jrule/ext-lib 


The following jar files can be found under the jrule/jar-folder:

| Jar File                               | Description                                                                                   |
| -------------------------------------- | --------------------------------------------------------------------------------------------- |
| jrule-generated.jar                    | Contains all generated items, which will be used when developing rules                        |
| jrule.jar                              | JRule Addon classes neeed as dependency when doing development                              |


# Get started with the JRule Automation Addon

- Install the addon by copying the org.openhab.automation.jrule-3.x.x-BETAX.jar to openhab-addons folder
  Download the latest release from https://github.com/seaside1/jrule/releases
- The default location is /etc/openhab/automation/jrule but can be configured
- When the addon is started it will:
1. Create JAVA source files for all items 
2. Compile java source files and create a resulting jrule.jar file under /etc/openhab/automation/jrule/jar
3. Compile any java rules file under  /etc/openhab/automation/jrule/rules/org/openhab/automation/jrule/rules/user/
 It is possible to use package structure with subdirectories in this folder, or the can be place in a flat structure right under this folder
4. Create jar files with dependencies to be used when creating your java-rules (jrule-generated.jar).
The two jar files needed for Java rules development can be found under /etc/openhab/automation/jrule/jar

Once the JAVA rule engine has started and compiled items successfully you can either copy the jar files
form /etc/openhab/automation/jrule/jar/* to the place where you intend to develop the Java- Rules, or share that folder
using samba / CIFS / NFS or similar.
- Set up your favourite IDE as a standard java IDE. 
- Create a new empty java project
- Create a package / folder org.openhab.automation.jrule.rules.user
- Place your Java rules file in this folder

NOTE: The rules will be reloaded if they are modified. Any java file you place under /etc/openhab/automation/jrule/rules/org/openhab/automation/jrule/rules/user/
will be compiled or recompiled, you don't have to restart OpenHAB.

Designing your Java Rules File (Hello World)
1. Start by adding an item in OpenHAB.
Group JRule
Switch MyTestSwitch  "Test Switch" (JRule)
Switch MyTestSwitch2  "Test Switch 2" (JRule)

2. Create the following class

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.items.generated._MyTestSwitch;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;

public class MySwitchRule extends JRule {

    @JRuleName("MySwitchRule")
    public void execOffToOnRule() {
        logInfo("||||| --> Hello World!");
    }
}
```

Make sure you add the Jar-files from /etc/openhab/jrule/jar as dependencies.

# Build and Deploy Rules using Maven
See https://github.com/seaside1/jrule-user for an example template project.

# Third Party External Dependencies

You can add any 3rd party library as dependency. Copy the jar files needed to /etc/openhab/automation/jrule/ext-lib
The Automation Engine will automatically pick these dependencies up when it is compiling the rules.

# Core Actions

Built in Core Actions that can be used
| Action                                 | Description                                                                                   |
| -------------------------------------- | --------------------------------------------------------------------------------------------- |
| say                                    | Will use VoiceManager to say action see Example 13                        |
| commandLineExecute                     | See Example 14                               |


# Thing actions
Thing actions are supported. JRule will generate a file that contains all available thing actions.
See example #34

# Logging from rules
Logging from rule can be done in 3 different ways
1. Not specifying anything will result in the usage of JRuleName as prefix when calling JRule.logInfo/Debug/Error etc see example 20
2. Overriding method JRule.getRuleLogName will result in the same log prefix for all rules defined in that file in see example 21
3. Specifically add rependency on log4j and define your own logger to do logging

# Configuration
JRule has some optional configuration. Place config file under: /etc/openhab/automation/jrule/jrule.conf
Example of config file.
```
#Prefix to be used when generating items, files
org.openhab.automation.jrule.itemprefix=_

## Run rules in a separate threadpool
org.openhab.automation.jrule.engine.executors.enable=false

## Minimum number of threads
org.openhab.automation.jrule.engine.executors.min=2

## Maximum number of threads
org.openhab.automation.jrule.engine.executors.max=10

```

# Operators for triggers and preconditions
See example 12, 25 and 26
| Operator                               | Description                                                                                   |
| -------------------------------------- | --------------------------------------------------------------------------------------------- |
| eq                                     | Equals                        |
| neq                                    | Not Equals                              |
| gt                                     | Greater than                        |
| gte                                    | Greater than equals                              |
| lt                                     | Less than                        |
| lte                                    | Less than equals                              |


# Examples 

## Example 1

Use Case: Invoke another item Switch from rule
```java
    @JRuleName("MyRuleTurnSwich2On")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
    public void execChangedToRule() {
    	logInfo("||||| --> Executing rule MyRule: changed to on");
        JRuleItems.MySwitch2.sendCommand(ON);
    }
```

## Example 2

Use case: Invoke a Doorbell, but only allow the rule to be invoked once every 20 seconds.
This is done by acquiring a lock getTimedLock("MyLockTestRule1", 20).

```java
    @JRuleName("MyLockTestRule1")
    @JRuleWhenItemChange(item = _MyTestSwitch2.ITEM, from = JRuleSwitchItem.OFF, to = JRuleSwitchItem.ON)
    public void execLockTestRule() {
        if (getTimedLock("MyLockTestRule1", 20)) {
            JRuleItems.MyDoorBellItem.sendCommand(ON);
            logInfo("||||| --> Got Lock! Ding-dong !");
        } else {
            logInfo("||||| --> Ignoring call to rule it is locked!");
        }
    }
```
## Example 3

Use case: Use the value that caused the trigger
When the rule is triggered, the triggered value is stored in the event.

```java
    @JRuleName("MyEventValueTest")
    @JRuleWhenItemReceivedCommand(item = _MyTestSwitch2.ITEM)
    public void myEventValueTest(JRuleEvent event) {
        logInfo("Got value from event: {}", event.getState().getValue());
    }
```
## Example 4

Use case: Or statement for rule trigger
To add an OR statement we simply add multiple @JRuleWhen statements

```java
    @JRuleName("MyNumberRule1")
    @JRuleWhenItemChange(item = _MyTestNumber.ITEM, from = "14", to = "10")
    @JRuleWhenItemChange(item = _MyTestNumber.ITEM, from = "10", to = "12")
    public void myOrRuleNumber(JRuleEvent event) {
        logInfo("Got change number: {}", event.getState().getValue());
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

import org.openhab.automation.jrule.items.generated._MyTestSwitch;
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

import org.openhab.automation.jrule.items.generated._MyTestSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;

public class MyTestUserRule extends JRuleUser {

    @JRuleName("TestUserDefinedRule")
    public void mySendNotificationRUle(JRuleEvent event) {
        if (timeIsOkforDisturbance()) {
            logInfo("It's ok to send a disturbing notification");
        }
    }
}
```
## Example 8

Use case create a timer for automatically turning off a light when it is turned on. If it's running cancel it and schedule a new one. 
```java
    @JRuleName("myTimerRule")
    @JRuleWhenItemChange(item = _MyLightSwitch.ITEM, to = JRuleSwitchItem.ON)
    public synchronized void myTimerRule(JRuleEvent event) {
        logInfo("Turning on light it will be turned off in 2 mins");
        createOrReplaceTimer(_MyLightSwitch.ITEM, Duration.ofMinutes(2), () -> { // Lambda Expression
            logInfo("Time is up! Turning off lights");
            JRuleItems.MyLightSwitch.sendCommand(OFF);
        });
    }
```
## Example 9

Use case: Let's say we have a 433 MHz wall socket with no ON/OFF feedback and a bit of bad radio reception. We can then create a repeating timer
to send multiple ON statements to be sure it actually turns on.
 createOrReplaceRepeatingTimer("myRepeatingTimer", 7, 4, will create a repeating timer that will trigger after 0 seconds, 7s, 14s and 21s 
 If the Timer is already running it will cancel it and create a new one.
 
```java
    @JRuleName("repeatRuleExample")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
    public synchronized void repeatRuleExample(JRuleEvent event) {
        createOrReplaceRepeatingTimer("myRepeatingTimer", 7, Duration.ofSeconds(10), () -> { // Lambda Expression
            final String messageOn = "repeatRuleExample Repeating.....";
            logInfo(messageOn);
            JRuleItems.MyBad433Switch.sendCommand(ON);
        });
    }
```

## Example 10

Use case Create a simple timer. When MyTestSwitch turns on it will wait 10 seconds and then turn MyTestSwitch2 to on. Note that
it will not reschedule the timer, if the timer is already running it won't reschedule it.
```java
    @JRuleName("timerRuleExample")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
    public synchronized void timerRuleExample(JRuleEvent event) {
        createTimer("myTimer", Duration.ofSeconds(10), () -> { // Lambda Expression
            final String messageOn = "timer example.";
            logInfo(messageOn);
            JRuleItems.MyTestWitch2.sendCommand(ON);
        });
    }
```
## Example 11

Use case trigger a rule at 22:30 in the evening to set initial brightness for a ZwaveDimmer to 30%
```java
    @JRuleName("setDayBrightness")
    @JRuleWhenTimeTrigger(hours=22, minutes=30)
    public synchronized void setDayBrightness(JRuleEvent event) {
        logInfo("Setting night brightness to 30%");
        int dimLevel = 30;
        JRuleItems.MyDimmerBrightness.sendCommand(dimLevel);
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
```java
    @JRuleName("turnOnFanIfTemperatureIsLow")
    @JRuleWhenItemChange(item = _MyTemperatureSensor.ITEM, condition = @JRuleCondition(lte = 20))
    public synchronized void turnOnFanIfTemperatureIsLow(JRuleEvent event) {
        logInfo("Starting fan since temperature dropped below 20");
        JRuleItems.MyHeatingFanSwitch.sendCommand(JRuleOnOffValue.ON);
    }
```

## Example 13

Use case: Using say command for tts
```java
    @JRuleName("testSystemTts")
    @JRuleWhenItemChange(item = _TestSystemTts.ITEM, to = JRuleSwitchItem.ON)
    public synchronized void testSystemTts(JRuleEvent event) {
        logInfo("System TTS Test");
        String message = "Testing tts! I hope you can hear it!";
        say(message, null, "sonos:PLAY5:RINCON_XXYY5857B06E0ZZOO");
    }
```

## Example 14

Use case: Executing command from CLI
```java

    @JRuleName("TestExecutingCommandLine")
    @JRuleWhenItemReceivedCommand(item = _MySwitchGroup.ITEM)
    public synchronized void testExecutingCommandLine(JRuleEvent event) {
        logInfo("Creating dummy file using CLI");
        executeCommandLine("touch", "/openhab/userdata/example.txt");
    }
```
## Example 15

Use case: A group of switches, see if status is changed, and also which member in the group changed state
```java
    @JRuleName("groupMySwitchesChanged")
    @JRuleWhenItemChange(item = _MySwitchGroup.ITEM)
    public synchronized void groupMySwitchGroupChanged(JRuleEvent event) {
    final boolean groupIsOnline = ((JRuleItemEvent) event).getState().getValueAsOnOffValue() == JRuleOnOffValue.ON;
    final String memberThatChangedStatus = ((JRuleItemEvent) event).getMemberName();
        logInfo("Member that changed the status of the Group of switches: {}", memberThatChangedStatus);
    }
```

## Example 16

Use case: A group of switches , trigger when it's changed from OFF to ON
```java
    @JRuleName("groupMySwitchesChangedOffToOn")
    @JRuleWhenItemChange(item = _MySwitchGroup.ITEM, from = JRuleSwitchItem.OFF, to = JRuleSwitchItem.ON)
    public synchronized void groupMySwitchesChangedOffToOn(JRuleEvent event) {
        logInfo("Member that changed the status of the Group from OFF to ON: {}", event.getMemberName());
    }
```

## Example 17

Use case: Listen for a Channel Trigger Event
```java
    @JRuleName("ChannelTriggered")
    @JRuleWhenChannelTrigger(channel = binding_thing.buttonevent)
    public synchronized void channelTriggered(JRuleEvent event) {
        logInfo("Channel triggered with value: {}", ((JRuleChannelEvent) event).getEvent());
    }
```

## Example 18

Use case: Cron based expression to trigger rule
```java
    @JRuleName("testCron")
    @JRuleWhenCronTrigger(cron = "*/5 * * * * *")
    public void testCron(JRuleEvent event) {
        logInfo("CRON: Running cron from string every 5 seconds: {}", event);
    }
```

## Example 19

Use case: getLastUpdated for an item  
Note that `ZonedDateTime lastUpdate = JRuleStringItem.forName(_MyCoolItem.ITEM).getLastUpdated("mapdb");`
can be called without serviceId argument: `ZonedDateTime lastUpdate = JRuleStringItem.forName(_MyCoolItem.ITEM).getLastUpdated();`
```java
@JRuleName("testLastUpdate")
@JRuleWhenCronTrigger(cron = "4 * * * * *")
public void testLastUpdate(JRuleEvent event){
    logInfo("CRON: Running cron from string: {}",event.getState().getValue());
    ZonedDateTime lastUpdate = JRuleItems.MyCoolItem.getLastUpdated("mapdb");
    DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss Z");
    String lastUpdateFormatted=lastUpdate.format(formatter);
    logInfo("Last Update: {}",lastUpdateFormatted);
}
```

## Example 20

Use case: Get the brigtness from a color item, set a color item to white (HSB 0, 0, 100)
```java
  
    @JRuleName("testBrightnessFromColorItem")
    @JRuleWhenItemChange(item = _MyTestColorItem.ITEM)
    public void testBrightnessFromColorItem(JRuleEvent event) {
       JRuleColorValue color = JRuleItems.MyTestColorItem.getState();
       int brightness = color.getHsbValue().getBrightness();
    }

    @JRuleWhenItemChange(item = _MyTestColorItem.ITEM)
    public void testSetWhiteOnColorItem(JRuleEvent event) {
        JRuleItems.MyTestColorItem.sendCommand(JRuleColorValue.fromHsb(0,0,100));
    }
```

## Example 21
Use case: Set logging name for a specific rule
```java
    @JRuleName("MyCustomLoggingRule")
    @JRuleLogName("MYLOG")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
    public void execChangedToRule() {
    	logInfo("||||| --> Executing rule MyRule: changed to on");
        JRuleItems.MySwitch2.sendCommand(ON);
    }
```

## Example 22
Use case: Override logging for all rules defined in one file
```java
   public class ColorRules extends JRule {

    @JRuleName("MyCustomLoggingRuleOnClass")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
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
    @JRuleWhenItemReceivedCommand(item = _MyStringValue.ITEM)
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
    @JRulePrecondition(item = _MyTestDisturbanceSwitch.ITEM, condition = @JRuleCondition(eq = "ON"))
    @JRuleName("MyTestPreConditionRule1")
    @JRuleWhenItemReceivedCommand(item = _MyMessageNotification.ITEM)
    public void testPrecondition(JRuleEvent event) {
        String notificationMessage = ((JRuleItemEvent) event).getState().getValue();
        logInfo("It is ok to send notification: {}", notificationMessage);
//        JRuleItems.MySendNoticationItemMqtt.sendCommand(notificationMessage);
        }
```
## Example 25
Use case: Use precondition annotation in order to create "AND" logic. Example when the temperature is above 30 degrees (celcius probably) and
a motion detector is triggered we will turn on a fan.

```java
   public class PreConditionTestTemperature extends JRule {

    @JRulePrecondition(item=_MyTestTemperatureSensor.ITEM, gt = 30)
    @JRuleName("MyTestPreConditionRuleTemperature")
    @JRuleWhenItemChange(item = _MyMotionDetector.ITEM, from = JRuleSwitchItem.OFF, to = JRuleSwitchItem.ON)
    public void testPrecondition(JRuleEvent event) {
        logInfo("Temperature is above 30 and we should start the fan since the motiondetector is triggered");
        JRuleItems.MyFan.sendCommand(ON);
    }
}
```

# Example 26

Use case: Send Quantity type Watt (W) from rule.

```java
   public class QuantityTypeRule extends JRule {
  
    @JRuleName("testQuantityPowerWatt")
    @JRuleWhenItemChange(item = _MyTestMeterPower.ITEM)
    public void testQuantityPower(JRuleEvent event) {
        logInfo("TestQuantity power will send this value as Watt: {}", event.getState().getValue());
        JRuleItems.TestPowerQuantityType.sendCommand(event.getState().getValueAsDouble(), "W");
    }
}
```
# Example 27

Use case: Use forName to create and item and send commands and get status

```java
 public class ForNameExampleRule extends JRule {
  
    @JRuleName("testForName")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
    public void testForName(JRuleEvent event) {
        JRuleSwitchItem switchItem = JRuleSwitchItem.forName("MyOtherTestSwitch");
        switchItem.sendItemCommand(OFF);
        if (switchItem.getItemStatus == ON) {
            switchItem.sendItemCommand(OFF);
        }
    }
 }
```
# Example 27b

Use case: Use forNameOptional to create and item and send commands and get status

```java
 public class ForNameExampleRule extends JRule {
  
    @JRuleName("testForNameOptional")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
    public void testForName(JRuleEvent event) {
        JRuleSwitchItem.forNameOptional("MyOtherTestSwitch").ifPresent(item -> item.sendCommand(true));
    }
 }
```

# Example 28

Use case: Get the name of the item that triggered the rule as well as new and old state value. 
This can be useful if you have multiple JRuleWhen with different items, and you want to know which item
triggered the rule.

```java
 public class TriggerNameExample extends JRule {
  
    @JRuleName("triggerNameExample")
    @JRuleWhenItemChange(item = _MyTestSwitch1.ITEM, to = JRuleSwitchItem.ON)
    @JRuleWhenItemChange(item = _MyTestSwitch2.ITEM, to = JRuleSwitchItem.ON)
    public void triggerNameExample(JRuleEvent event) {
     logInfo("The rule was triggered by the following item: {}", event.getItemName());
     logInfo("The rule was Old Value was: {} and new value: {}", event.getOldState().getValue(), event.getState().getValue());  
      
    }
 }
```

## Example 29

Use case: get average value for a Number item last hour 
```java
@JRuleName("testAverageLastHour")
@JRuleWhenCronTrigger(cron = "4 * * * * *")
public void testAverage(JRuleEvent event){
    Double average = JRuleNumberItem.forName(_MyNumberItem.ITEM).averageSince(ZonedDateTime.now().minus(1,ChronoUnit.HOURS));
    logInfo("Average value last hour: {}",average);
}
```


## Example 30

Use case: Use generated JRuleItems.java to get hold of items. For instance get state of an item. 
```java
 public class ItemsExampleRule extends JRule {
  
    @JRuleName("testItems")
    @JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)
    public void testItems(JRuleEvent event) {
        JRuleItems.MyOtherTestSwitch.getState();
    }
 }

```

## Example 31

Use case: Restart thing every night due to binding flakyness

```java
@JRuleName("Restart thing every night")
@JRuleWhenTimeTrigger(hours=3)
public void restartThing() {
    JRuleThings.my_flaky_thing.restart();
}
```

## Example 32

Use case: Detect if a specific thing goes offline, wait for it to come online again within a given time

```java
@JRuleName("Notify if thing stays offline")
@JRuleWhenThingTrigger(thing = remoteopenhab_thing.ID, from = JRuleThingStatus.ONLINE)
public void warnIfThingStaysOffline() {
    createOrReplaceTimer("MY_TIMER", Duration.ofMinutes(3), () -> {
        if (JRuleThings.remoteopenhab_thing.getStatus() != JRuleThingStatus.ONLINE) {
            logWarn("Thing {} is still offline, restarting",remoteopenhab_thing.ID);
            JRuleThings.remoteopenhab_thing.restart();
        }
    });
}
```

## Example 33

Use case: Listen for thing status events on _all_ things

```java
@JRuleName("Log every thing that goes offline")
@JRuleWhenThingTrigger(from = JRuleThingStatus.ONLINE)
public void startTrackingNonOnlineThing(JRuleEvent event) {
    String offlineThingUID = event.getThing();
    // ...
}
```

## Example 34

Use case: Thing actions, send message with pushover and other services.
Note that you will have to set up a pusheover account as thing in openHAB.

```java
@JRuleName("PushOverTest")
@JRuleWhenItemChange(item = _MyTestSendPushOverButton.ITEM, to = _MyTestSendPushOverButton.ON)
public void testPower(JRuleEvent event) {
       logInfo("Sending Test message using pushover via actions");
       JRuleActions.pushoverPushoverAccountXYZ.sendMessage("MyMessage", "MyTitle");
}
```

## Example 35

Use case: Want to listen on all Item events of a group (without the groupstate must change). 
    Alternatively you could just listen to just Group changes or (real) Item changes

```java
    @JRuleName("MemberOfUpdateTrigger")
    @JRuleWhenItemReceivedUpdate(item = _MySwitchGroup.ITEM, memberOf = JRuleMemberOf.All)
    //@JRuleWhenItemReceivedUpdate(item = _MySwitchGroup.ITEM, memberOf = JRuleMemberOf.Items)
    //@JRuleWhenItemReceivedUpdate(item = _MySwitchGroup.ITEM, memberOf = JRuleMemberOf.Groups)
    public synchronized void memberOfUpdateTrigger(JRuleItemEvent event) {
        final String memberThatChangedStatus = event.getMemberName();
        logInfo("Member that changed the status of the Group of switches: {}", memberThatChangedStatus);
    }
```

## Example 36

Use case: Want to listen just on changes where the state is now greater/equals then 12 and was before less then 12.
    Without the previous condition the rule will be triggered every time the state is greater/equals then 12.

```java
    @JRuleName("Change from something less to something greater")
    @JRuleWhenItemChange(item = ITEM_FROM_TO, previousCondition = @JRuleCondition(lt = 12), condition = @JRuleCondition(gte = 12))
    public void itemChangeFromTo(JRuleEvent event) {
        logInfo("state change to something >= 12 and was before < 12");
    }
```

## Example 37

Use case: Chain timers. Execute one and after this is expired, execute the next one.

```java
@JRuleName("Notify if thing stays offline")
@JRuleWhenItemChange(item = _MySwitchGroup.ITEM)
public void chainSomeTimers() {
    createTimer(Duration.ofSeconds(3), () -> {
        logInfo("First timer finished after 3 seconds");
    }).createTimerAfter(Duration.ofSeconds(10), () -> {
        logInfo("Second timer finished after 10 more seconds");
    });
}
```

## Example 38

Use case: Do not execute a rule too often

```java
@JRuleDebounce(10)
@JRuleName("Notify if thing stays offline")
@JRuleWhenItemChange(item = _MySwitchGroup.ITEM)
public void debounceMethod() {
    // super critical stuff which shouldn't be called too often
}
```

## Example 39

Use case: Execute a rule delayed

```java
@JRuleDelayed(10)
@JRuleName("Execute after ten seconds")
@JRuleWhenItemChange(item = _MySwitchGroup.ITEM)
public void delayedMethod() {
    // delay the execution of this
}
```

# Changelog
## BETA15
- BREAKING: All JRuleWhen has to be change to corresponding JRuleWhenItemChanged (as an example, look at JRule Examples documentation)
- JRule When refactoring by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/61
- Thing Channel triggers by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/62
- Generate Actions by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/63
- Add option to get groupMembers as Items by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/65
- Memberof Trigger by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/66
- Fix buffer being read twice and breaking classloading by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/67
- Fix missing precondition support for timer rules by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/68
- Fix timer trigger by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/70
- Initial tests for JRuleWhenItemChange triggers by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/73
- Threadlocal logging - some improvements by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/79
- Junit test for duplicate rule invocations by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/75
- Add docker integration test by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/77
- Include old thing status in event by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/80
- Use thread safe list instead of arraylist by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/81
- Defer to parent classloader if file not found by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/83
- Fix inheritance in actions by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/87
- Fix mqtt for tests by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/91
- Fix ConcurrentModificationException in test by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/92
- Added typing for thing channel triggers, ie `JRuleWhen(channel = binding_thing.triggerChannel)` instead of typing the channel id string

## BETA14
- Thing support in rules by [seime](https://github.com/seime) pr https://github.com/seaside1/jrule/pull/59
  - BREAKING: jrule-items.jar has been renamed to jrule-generated.jar
- Added missing sendCommand for StopMove commands by [seime](https://github.com/seime) pr https://github.com/seaside1/jrule/pull/57
- Fixed parsing of double value for Quantity type by [seime](https://github.com/seime) pr https://github.com/seaside1/jrule/pull/56
- Added generic action handler by [querdenker2k](https://github.com/querdenker2k) pr https://github.com/seaside1/jrule/pull/55 see exampe #34
- Refactoring of event for channel plus cleanup by [querdenker2k](https://github.com/querdenker2k) pr https://github.com/seaside1/jrule/pull/52
- Refactoring of persistance functions and item handling with exceptions by [querdenker2k](https://github.com/querdenker2k) pr https://github.com/seaside1/jrule/pull/51
- Added item id and fixes for generated items by [LumnitzF](https://github.com/LumnitzF) pr https://github.com/seaside1/jrule/pull/50
- Added MDC Logging tags to be used with elastic search (logstash,kibana and similar) by [querdenker2k](https://github.com/querdenker2k) pr https://github.com/seaside1/jrule/pull/49
- Fixed parsing of double values in rule conditions by [seime](https://github.com/seime) pr https://github.com/seaside1/jrule/pull/48
- Fixed parsing of UNDEF by [seime](https://github.com/seime) pr https://github.com/seaside1/jrule/pull/45

## BETA13
- Fixed a bug with naming of JRuleItems.java
- Fixed issues with post and sendCommand to groups

## BETA12
 - Major refactoring by seime https://github.com/seaside1/jrule/pull/42
   - Replaces the templating mechanism with Freemarker, mainly to allow more advanced constructs such as loops - 
     and to avoid all the repetitive code in the template files
   - Generates a new file Items.java which looks a bit like
     public class Items {
          public static MySwitchItem SwitchItem = JRuleItemRegistry.get("MySwitchItem", MySwitchItem.class);
          public static MyStringItem StringItem = JRuleItemRegistry.get("MyStringItem", MyStringItem.class);
      } 
    - Adds a skeleton support for LocationItem (which was missing)
    - Adds a new field LABEL (item label)
    - Adds a few convenience methods such as getLabel() and getName()
    - Adds more typing of Group items

## BETA11
 - Wrap TransformationException in JRuleExecutionException by seime https://github.com/seaside1/jrule/pull/39
 - Add equivalent postUpdate logging as sendCommand by seime https://github.com/seaside1/jrule/pull/38
 - Fix group sendCommand for UpDown by seime https://github.com/seaside1/jrule/pull/36
 - Added eq and neq to channel event by gerrieg https://github.com/seaside1/jrule/pull/35
 - Added support for ZonedDateTime in DateTimeItem by gerrieg https://github.com/seaside1/jrule/pull/34
 - Fixed issued with undef item for state 
 - Added mocked eventbus for testing rules with junit 
## BETA10
 - Optimized items by gerrieg https://github.com/seaside1/jrule/pull/33
 - Syntax change: event.getValue(), event.getValuesAsDouble() etc replaced with event.getState().getValue() and event.getState().getValueAsDouble()
 - Syntax change JRuleSwitchItem.sendCommand(myItem, ON) replaced with JRuleSwitchItem.forName(myItem).sendCommand(ON)
## BETA9
 - Fixed bug with item generation and forName overloading
## BETA8
 - Added forName for items see example 27 https://github.com/seaside1/jrule/commit/0952ae497d998c5728a85df407bfbf3f1909f8e9
 - Added itemName and OldState by gerrieg https://github.com/seaside1/jrule/pull/31
## BETA7
- Fixed item for Number:Quantity, you can now send a quantity type in the command see example 26
- Added precondition see example 24 and 25 by seime: https://github.com/seaside1/jrule/pull/30
- Added possibility to use subdirs and packages for rules by seime: https://github.com/seaside1/jrule/pull/30
## BETA6
- Added seprate thread executors supplied by seime: https://github.com/seaside1/jrule/pull/23
- Added Windows Support by LumnitzF https://github.com/seaside1/jrule/pull/24
- Added cancellation of repeating timers by seime: https://github.com/seaside1/jrule/pull/26
- Null check on timers with futures by seime https://github.com/seaside1/jrule/pull/29
- Update to Openhab 3.3.0
- Fixed rescheduling of timers by seime: https://github.com/seaside1/jrule/pull/28
- Added check for repeating timers by seime: https://github.com/seaside1/jrule/pull/27
- Fixed repeated timer executing directly by seime: https://github.com/seaside1/jrule/pull/25
- Added transformation sevice example 23 by seime: https://github.com/seaside1/jrule/pull/20
- Improved compiler error logging by seime: https://github.com/seaside1/jrule/pull/12
- Added UP/Down support for group items by seime: https://github.com/seaside1/jrule/pull/13
- Quantity Type support by seime: https://github.com/seaside1/jrule/pull/14
- Configurable item-prefix by weberjn https://github.com/seaside1/jrule/pull/16
- Improve exception handling by No3x https://github.com/seaside1/jrule/pull/19
- Made thread executors configurable, default disabled 
- Possible to build jrule standalone https://github.com/seaside1/jrule/pull/24 or together with Openhab-addons
- Cleaned up repostiory from binary files. Remove and old clones of jrule and clone a fresh repo
## BETA5
- Addes support for adding rules in jar-files, as an alternative.
## BETA4
- Added config for character to be used when generating items files
## BETA3
- Major refactoring of logging
## BETA2
- Fixed color item
- Added annotation for setting logger on a rule see example 21 and 22
- Optional to override getLogName on class
- Contact item update
- Rollershutter item support added
- UpDown, Increase Decrease support added to various items
- OnOff and Percent commands added to ColorItem
## BETA1
- Added color item see example 20
- Moved org.openhab.automation.jrule.rules.JRuleOnOffvalue, JRulePlayPause etc to org.openhab.automation.jrule.rules.value

## ALPHA12
-  Fix some language typos, some refactor of java classes, improved initialization of singletons due to concurrency aspects
## ALPHA11
- Added check for working dir via system properties
## ALPHA10
- Added LatUpdate via JRulePersistenceExtentions see example 19

## ALPHA9
- Added cron expressions for rules see example 18
- Bug fix by @roth for reloading channel triggers

## ALPHA8
- Channel triggers provided by @roth see example 17

## ALPHA7
- Fixed bug with group member value was null for non StringType types

## ALPHA6
- Added group functionality getMember will return who triggered a change for a group

## ALPHA5
- Removed dependencies on slf4japi and eclipse annotations
- Added logInfo logDebug (to wrap slf4j and remove dep)
- Fixed compilation of rules to be more robust with internal dependencies 

## ALPHA4
- Refactored completable futures
- Added 5 seconds of delay for initialization of the rule engine to avoid multiple reloads
- Added support for play & pause for player item
- Added commandLineExecute

## ALPHA3
- Fixed issue when reloading rules if they are changed with monitored items
- Fixed classpath issue when executing rules using 3rd party libraries 

## ALPHA2
- Added possibility to include 3rd party libraries when developing rules

## ALPHA1
- Refactored internal jar dependencies and jar-generation
- Added eq comparator for number triggers in rules

# Roadmap
- Locks and timers by annotation
- Built in expire functionality
