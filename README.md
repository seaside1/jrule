# OpenHAB Rules using Java

This automation package aims to enable Java development of OpenHAB Rules. The addon will allow the user to create custom OpenHAB rules
in one or several .java-files. The Java Rules will need defined triggers in order for the engine to know how and when to execute them. The triggers
are very similar to the triggers in Rules DSL but expressed using java annotations. In order to execute rules based on items defined in OpenHAB either in .items-files or the GUI. The addon needs to know about these items and this is realized by the Rule Engine where it generates a .java and a .class file for each item in the system. The class files are then packaged in a .jar-file which the user can use as dependency when doing Rules Development.
For the addon to be able to pick up rules, they first need to be compiled by the addon. The source .java rules-files are placed in a specific rules folder and
will be automatically compiled and loaded into OpenHAB when the addon is started. The syntax for rules as well as the design and thinking behind the addon is to provide something that is similar to Rules DSL but more powerful and customizable.

# Limitations

- Not supporting OH3 GUI rules, script actions and script conditions 

# Why

 - You will be able to use a standard Java IDE to develop your rules. 
 - Full auto-completion (Shift space) for all items, less chance of errors and typos
 - Take full advantage of all java design patters
 - Share and reuse code for you rules
 - Advanced timers and locks are built in and can be used without cluttering the code
 - Possibility to write junit-tests to test your rules
 - Use any 3rd party dependencies and libraries in your rules
 - You will be able to use JRule in parallel with any other Rules engine if you want to give it a try

# Who

This addon is not for beginners, you should have knowledge in writing java-programs or a desire to do so.

# Maturity

Beta, still major changes.

# Download

Prebuilt jar file is available in the bin folder under https://github.com/seaside1/jrule/releases

# Java Rule Engine

Input rules files
will be placed under:
/etc/automation/jrule/rules/org/openhab/automation/jrule/rules/user/

It is also possible to add rules as pre-compiled jar files under:
/etc/automation/jrule/rules-jar/

Output jar files to be added by the user as dependencies when doing rule development will be located under:
/etc/openhab/automation/jrule/jar

The following jar files can be found under the jrule/jar-folder:

| Jar File                               | Description                                                                                   |
| -------------------------------------- | --------------------------------------------------------------------------------------------- |
| jrule-items.jar                        | Contains all generated items, which will be used when developing rules                        |
| jrule.jar                              | JRule Addon classes neeed as dependency when doing development                              |


# Get started with the JRule Automation Addon

- Install the addon by copying the org.openhab.automation.jrule-3.x.x-ALPHAX.jar to openhab-addons folder
  Download the latest release from https://github.com/seaside1/jrule/releases
- In default location is /etc/openhab/automation/jrule
- When the addon is started it will:
1. Create JAVA source files for all items 
2. Compile java source files and create a resulting jrule.jar file under /etc/openhab/automation/jrule/jar
3. Compile any java rules file under  /etc/openhab/automation/jrule/rules/org/openhab/automation/jrule/rules/user/
 It is possible to use package structure with subdirectories in this folder, or the can be place in a flat structure right under this folder
4. Create jar files with dependencies to be used when creating your java-rules (jrule-items.jar).
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
1. Start by adding an item in Openhab.
Group JRule
Switch MyTestSwitch  "Test Switch" (JRule)
Switch MyTestSwitch2  "Test Switch 2" (JRule)

2. Create the following class

```java
package org.openhab.automation.jrule.rules.user;
import static org.openhab.automation.jrule.rules.value.JRuleOnOffValue.ON;
import org.openhab.automation.jrule.items.generated._MyTestSwitch;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhen;

public class MySwitchRule extends JRule {
    
    @JRuleName("MySwitchRule")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execOffToOnRule() {
        logInfo("||||| --> Hello World!");
    }
}
```

Make sure you add the Jar-files from /etc/openhab/jrule/jar as dependencies.

# Third Party External Dependencies

You can add any 3rd party library as dependency. Copy the jar files needed to /etc/openhab/automation/jrule/ext-lib
The Automation Engine will automatically pick these dependencies up when it is compiling the rules.

# Core Actions

Built in Core Actions that can be used
| Action                                 | Description                                                                                   |
| -------------------------------------- | --------------------------------------------------------------------------------------------- |
| say                                    | Will use VoiceManager to say action see Example 13                        |
| commandLineExecute                     | See Example 14                               |

# Logging from rules
Logging from rule can be done in 3 different ways
1. Not specifying anything will result in the usage of JRuleName as perfix when calling JRule.logInfo/Debug/Error etc see example 20
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
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execChangedToRule() {
    	logInfo("||||| --> Executing rule MyRule: changed to on");
        _MySwitch2.sendCommand(ON);
    }
```

## Example 2

Use case: Invoke a Doorbell, but only allow the rule to be invoked once every 20 seconds.
This is done by acquiring a lock getTimedLock("MyLockTestRule1", 20).

```java
    @JRuleName("MyLockTestRule1")
    @JRuleWhen(item = _MyTestSwitch2.ITEM, trigger = _MyTestSwitch2.TRIGGER_CHANGED_FROM_OFF_TO_ON)
    public void execLockTestRule() {
        if (getTimedLock("MyLockTestRule1", 20)) {
            _MyDoorBellItem.sendCommand(ON);
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
   @JRuleWhen(item = __MyTestSwitch2.ITEM, trigger = __MyTestSwitch2.TRIGGER_RECEIVED_COMMAND)
   public void myEventValueTest(JRuleEvent event) {
      logInfo("Got value from event: {}", event.getValue());
   }
```
## Example 4

Use case: Or statement for rule trigger
To add an OR statement we simply add multiple @JRuleWhen statements

```java
   @JRuleName("MyNumberRule1")
   @JRuleWhen(item = _MyTestNumber.ITEM, trigger = _MyTestNumber.TRIGGER_CHANGED, from = "14", to = "10")
   @JRuleWhen(item = _MyTestNumber.ITEM, trigger = _MyTestNumber.TRIGGER_CHANGED, from = "10", to = "12")
   public void myOrRuleNumber(JRuleEvent event) {
      logInfo("Got change number: {}", event.getValue());
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
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.user.JRuleUser;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhen;

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

``
We then extend the rule from the Java Rules file:

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.items.generated._MyTestSwitch;
import org.openhab.automation.jrule.rules.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhen;

public class MyTestUserRule extends JRuleUser {
  
    @JRuleName("TestUserDefinedRule")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_RECEIVED_COMMAND)
    public void mySendNotificationRUle(JRuleEvent event) {
        if (timeIsOkforDisturbance()) {
            logInfo("It's ok to send a disturbing notification");
        }
    }
}
```
## Example 8

Use case create a timer for automatically turning of a light when it is turned on. If it's running cancel it and schedule a new one. 
```java
    @JRuleName("myTimerRule")
    @JRuleWhen(item = _MyLightSwitch.ITEM, trigger = _MyLightSwitch.TRIGGER_CHANGED_TO_ON)
    public synchronized void myTimerRule(JRuleEvent event) {
        logInfo("Turning on light it will be turned off in 2 mins");
        createOrReplaceTimer(_MyLightSwitch.ITEM, 2 * 60, (Void) -> { // Lambda Expression
            logInfo("Time is up! Turning off lights");
            _MyLightSwitch.sendCommand(OFF);
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
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public synchronized void repeatRuleExample(JRuleEvent event) {
        createOrReplaceRepeatingTimer("myRepeatingTimer", 7, 10, (Void) -> { // Lambda Expression
            final String messageOn = "repeatRuleExample Repeating.....";
            logInfo(messageOn);
            _MyBad433Switch.sendCommand(ON);
        });
    }
```

## Example 10

Use case Create a simple timer. When MyTestSwitch turns on it will wait 10 seconds and then turn MyTestSwitch2 to on. Note that
it will not reschedule the timer, if the timer is already running it won't reschedule it.
```java
    @JRuleName("timerRuleExample")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public synchronized void timerRuleExample(JRuleEvent event) {
        createTimer("myTimer", 10, (Void) -> { // Lambda Expression
            final String messageOn = "timer example.";
            logInfo(messageOn);
            _MyTestWitch2.sendCommand(ON);
        });
    }
```
## Example 11

Use case trigger a rule at 22:30 in the evening to set initial brightness for a ZwaveDimmer to 30%
```java
  @JRuleName("setDayBrightness")
  @JRuleWhen(hours=22, minutes=30)
  public synchronized void setDayBrightness(JRuleEvent event) {
      logInfo("Setting night brightness to 30%");
      int dimLevel = 30;
      _ZwaveDimmerBrightness.sendCommand(dimLevel);
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
  @JRuleWhen(item = _MyTemperatureSensor.ITEM, trigger = _MyTemperatureSensor.TRIGGER_RECEIVED_UPDATE, lte = 20)
  public synchronized void turnOnFanIfTemperatureIsLow(JRuleEvent event) {
      logInfo("Starting fan since temeprature dropped below 20");
      _MyHeatinFanSwitch.sendCommand(ON);
  }
```

## Example 13

Use case: Using say command for tts
```java
    @JRuleName("testSystemTts")
    @JRuleWhen(item = _TestSystemTts.ITEM, trigger = _TestSystemTts.TRIGGER_CHANGED_TO_ON)
    public synchronized void testSystemTts(JRuleEvent event) {
        logInfo("System TTS Test");
        String message = "Testing tts! I hope you can hear it!";
        say(message, null, "sonos:PLAY5:RINCON_XXYY5857B06E0ZZOO");
    }
```

## Example 14

Use case: Executing command from CLI
```java

    @JRuleName("testExecutingCommandLine")
    @JRuleWhen(item = _gMySwitchGroup.ITEM, trigger = _gMySwitchGroup.TRIGGER_CHANGED)
    public synchronized void testExecutingCommandLine(JRuleEvent event) {
        logInfo("Creating dummy file using CLI");
        executeCommandLine("touch ~/example.txt");
    }
```
## Example 15

Use case: A group of switches, see if status is changed, and also which member in the group changed state
```java
    @JRuleName("groupMySwitchesChanged")
    @JRuleWhen(item = _gMySwitchGroup.ITEM, trigger = _gMySwitchGroup.TRIGGER_CHANGED)
    public synchronized void groupMySwitchGroupChanged(JRuleEvent event) {
        final boolean groupIsOnline = event.getValueAsOnOffValue() == ON;
        final String memberThatChangedStatus = event.getMemberName();
        logInfo("Member that changed the status of the Group of switches: {}", memberThatChangedStatus);
    }
```

## Example 16

Use case: A group of switches , trigger when it's changed from OFF to ON
```java
    @JRuleName("groupMySwitchesChangedOffToOn")
    @JRuleWhen(item = _gMySwitchGroup.ITEM, trigger = _gMySwitchGroup.TRIGGER_CHANGED, from="OFF", to="ON")
    public synchronized void groupMySwitchesChangedOffToOn(JRuleEvent event) {
        logInfo("Member that changed the status of the Group from OFF to ON: {}", event.getMemberName());
    }
```

## Example 17

Use case: Listen for a Channel Trigger Event
```java
    @JRuleName("channelTriggered")
    @JRuleWhen(channel = "binding:thing:buttonevent")
    public synchronized void channelTriggered(JRuleEvent event) {
        logInfo("Channel triggered with value: {}", event.getValue());
    }
```

## Example 18

Use case: Cron based expression to trigger rule
```java
    @JRuleName("testCron")
    @JRuleWhen(cron = "4 * * * * *")
    public void testCron(JRuleEvent event) {
        logInfo("CRON: Running cron from string every minute when seconds is at 4: {}", event.getValue());
    }
```


## Example 19

Use case: getLastUpdated for an item
Note that JRulePersistenceExtentions.getLastUpdate(_MyCoolItem.ITEM, "mapdb");
can be called without serviceId argument:
JRulePersistenceExtentions.getLastUpdate(_MyCoolItem.ITEM);
```java
  
    @JRuleName("testLastUpdate")
    @JRuleWhen(cron = "4 * * * * *")
    public void testLastUpdate(JRuleEvent event) {
        logInfo("CRON: Running cron from string: {}", event.getValue());
        ZonedDateTime lastUpdate = JRulePersistenceExtentions.getLastUpdate(_MyCoolItem.ITEM, "mapdb");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss Z");
        String lastUpdateFormatted = lastUpdate.format(formatter);
        logInfo("Last Update: {}", lastUpdateFormatted);    
```


## Example 20

Use case: Get the brigtness from a color item, set a color item to white (HSB 0, 0, 100)
```java
  
    @JRuleName("testBrightnessFromColorItem")
    @JRuleWhen(item = _MyTestColorItem.ITEM, trigger = _MyTestColorItem.TRIGGER_CHANGED)
    public void testBrightnessFromColorItem(JRuleEvent event) {
       JRuleColorValue color = _MyTestColorItem.getState();
       int brightness = color.getHsbValue().getBrightness();
    }
   
    @JRuleWhen(item = _MyTestColorItem.ITEM, trigger = _MyTestColorItem.TRIGGER_CHANGED) 
    public void testSetWhiteOnColorItem(JRuleEvent event) {
        _MyTestColorItem.sendCommand(JRuleColorValue.fromHsb(0,  0,  100));
    }
```

## Example 21
Use case: Set logging name for a specific rule
```java
    @JRuleName("MyCustomLoggingRule")
    @JRuleLogName("MYLOG")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execChangedToRule() {
    	logInfo("||||| --> Executing rule MyRule: changed to on");
        _MySwitch2.sendCommand(ON);
    }
```

## Example 22
Use case: Override logging for all rules defined in one file
```java
   public class ColorRules extends JRule {

    @JRuleName("MyCustomLoggingRuleOnClass")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execChangedToRule() {
    	logInfo("||||| --> Executing rule MyRule: changed to on");
        _MySwitch2.sendCommand(ON);
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
    @JRuleWhen(item = _MyStringValue.ITEM, trigger = _MyStringValue.TRIGGER_RECEIVED_COMMAND)
    public void applyTransformation(JRuleEvent event) {
        String transformedValue = transform("MAP(my.map):%s", event.getValue());
        logInfo("Transformed {} to {}", event.getValue(), transformedValue);
        _MyTransformationReceiver.sendCommand(transformedValue);
    }
}
```

## Example 24

Use case: Use precondition annotation in order to create "AND" logic. Example we have a switch that will tell 
if it is ok for disturbance. If it is ok the switch is set to ON and we can send a notification if the notification 
message is updated.

```java
   public class PreConditionTestDisturbance extends JRule {

    @JRulePrecondition(item=_MyTestDisturbanceSwitch.ITEM, eq = "ON")
    @JRuleName("MyTestPreConditionRule1")
    @JRuleWhen(item = _MyMessageNotification.ITEM, trigger = _MyMessageNotification.TRIGGER_RECEIVED_COMMAND)
    public void testPrecondition(JRuleEvent event) {
        String notificationMessage = event.getValue();
        logInfo("It is ok to send notification: {}", notificationMessage);
        _MySendNoticationItemMqtt.sendCommand(notificationMessage);
    }
}
```
## Example 25
Use case: Use precondition annotation in order to create "AND" logic. Example when the temperature is above 30 degrees (celcius probably) and
a motion detector is triggered we will turn on a fan.

```java
   public class PreConditionTestTemperature extends JRule {

    @JRulePrecondition(item=_MyTestTemperatureSensor.ITEM, gt = 30)
    @JRuleName("MyTestPreConditionRuleTemperature")
    @JRuleWhen(item = _MyMotionDetector.ITEM, trigger = _MyMotionDectetor.TRIGGER_CHANGED_FROM_OFF_TO_ON)
    public void testPrecondition(JRuleEvent event) {
        logInfo("Temperature is above 30 and we should start the fan since the motiondetector is triggered");
        _MyFan.sendCommand("ON");
    }
}
```

# Example 26

Use case: Send Quantity type Watt (W) from rule.

```java
   public class QuantityTypeRule extends JRule {
  
    @JRuleName("testQuantityPowerWatt")
    @JRuleWhen(item=_MyTestMeterPower.ITEM, trigger=_MyTestMeterPower.TRIGGER_CHANGED)
    public void testQuantityPower(JRuleEvent event) {
        logInfo("TestQuantity power will send this value as Watt: {}", event.getValue());
        _TestPowerQuantityType.sendCommand(event.getValueAsDouble(), "W");
    }
}
```
# Example 27

Use case: Use forName to create and item and send commands and get status

```java
 public class ForNameExampleRule extends JRule {
  
    @JRuleName("testForName")
    @JRuleWhen(item=_MyTestSwitch.ITEM, trigger=_MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void testForName(JRuleEvent event) {
        JRuleSwitchItem switchItem = JRuleSwitchItem.forName("MyOtherTestSwitch");
        switchItem.sendCommand(OFF);
        if (switchItem.getStatus == ON) {
            switchItem.sendCommand(OFF);
        }
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
    @JRuleWhen(item=_MyTestSwitch1.ITEM, trigger=_MyTestSwitch1.TRIGGER_CHANGED_TO_ON)
    @JRuleWhen(item=_MyTestSwitch2.ITEM, trigger=_MyTestSwitch2.TRIGGER_CHANGED_TO_ON)
    public void triggerNameExample(JRuleEvent event) {
     logInfo("The rule was triggered by the following item: {}", event.getItemName());
     logInfo("The rule was Old Value was: {} and new value: {}", event.getOldState(), event.getState());  
      
    }
 }
```


# Changelog
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

## Resources

[https://github.com/seaside1/jrule/releases/download/jrule-3.x.x-BETA8/org.openhab.automation.jrule-3.x.x-BETA8.jar](https://github.com/seaside1/jrule/releases/download/jrule-3.x.x-BETA7/org.openhab.automation.jrule-3.x.x-BETA8.jar)
