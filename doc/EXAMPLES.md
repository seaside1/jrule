# Examples

- [Examples](#examples)
    + [Example 1 - Invoke another item Switch from rule](#example-1---invoke-another-item-switch-from-rule)
    + [Example 2 - Using a timed lock](#example-2---using-a-timed-lock)
    + [Example 3 - Using event data](#example-3---using-event-data)
    + [Example 4 - Multiple rule triggers](#example-4---multiple-rule-triggers)
    + [Example 5 - Creating a custom parent rule class](#example-5---creating-a-custom-parent-rule-class)
    + [Example 6 - Using a custom parent rule class](#example-6---using-a-custom-parent-rule-class)
    + [Example 7 - Reusing rule functionality](#example-7---reusing-rule-functionality)
    + [Example 8 - Creating a timer](#example-8---creating-a-timer)
    + [Example 9 - Creating a repeating timer](#example-9---creating-a-repeating-timer)
    + [Example 10 - Creating a timer #2](#example-10---creating-a-timer--2)
    + [Example 11 - Using JRuleWhenTimeTrigger](#example-11---using-jrulewhentimetrigger)
    + [Example 12 - Condition on trigger](#example-12---condition-on-trigger)
    + [Example 13 - Text to speech](#example-13---text-to-speech)
    + [Example 14 - Executing a shell command](#example-14---executing-a-shell-command)
    + [Example 15 - Group items](#example-15---group-items)
    + [Example 16 - Group items #2](#example-16---group-items--2)
    + [Example 17 - Channel triggers](#example-17---channel-triggers)
    + [Example 18 - Cron based trigger](#example-18---cron-based-trigger)
    + [Example 19 - Persistence and lastUpdated](#example-19---persistence-and-lastupdated)
    + [Example 20 - Color item](#example-20---color-item)
    + [Example 21 - Set logging name for a specific rule](#example-21---set-logging-name-for-a-specific-rule)
    + [Example 22 - Override logging for all rules defined in one file](#example-22---override-logging-for-all-rules-defined-in-one-file)
    + [Example 23 - Apply transformation using openHAB transformation service](#example-23---apply-transformation-using-openhab-transformation-service)
    + [Example 24 - Preconditions #1](#example-24---preconditions--1)
    + [Example 25 - Preconditions #2](#example-25---preconditions--2)
    + [Example 26 - Send Quantity type Watt (W) from rule](#example-26---send-quantity-type-watt--w--from-rule)
    + [Example 27 - Use forName to create and item and send commands and get status](#example-27---use-forname-to-create-and-item-and-send-commands-and-get-status)
    + [Example 27b - Use forNameOptional to create and item and send commands and get status](#example-27b---use-fornameoptional-to-create-and-item-and-send-commands-and-get-status)
    + [Example 28 - Get the name of the item that triggered the rule as well as new and old state value](#example-28---get-the-name-of-the-item-that-triggered-the-rule-as-well-as-new-and-old-state-value)
    + [Example 29 - Get average value for a Number item last hour](#example-29---get-average-value-for-a-number-item-last-hour)
    + [Example 30 - Use generated JRuleItems.java to get hold of items](#example-30---use-generated-jruleitemsjava-to-get-hold-of-items)
    + [Example 31 - Restart thing every night due to binding flakyness](#example-31---restart-thing-every-night-due-to-binding-flakyness)
    + [Example 32 - Detect if a specific thing goes offline, wait for it to come online again within a given time](#example-32---detect-if-a-specific-thing-goes-offline--wait-for-it-to-come-online-again-within-a-given-time)
    + [Example 33 - Listen for thing status events on _all_ things](#example-33---listen-for-thing-status-events-on--all--things)
    + [Example 34 - Thing actions, send message with pushover and other services](#example-34---thing-actions--send-message-with-pushover-and-other-services)
    + [Example 35 - Listen on all Item events of a group (without the groupstate must change)](#example-35---listen-on-all-item-events-of-a-group--without-the-groupstate-must-change-)
    + [Example 36 - Listen for group changes - with conditions](#example-36---listen-for-group-changes---with-conditions)
    + [Example 37 - Timer chaining](#example-37---timer-chaining)
    + [Example 38 - Debounce](#example-38---debounce)
    + [Example 39 - HTTP requests](#example-39---http-requests)
    + [Example 40 - Delay rule execution](#example-40---delay-rule-execution)
    + [Example 41 - Get Metadata and Tags](#example-41---get-metadata-and-tags)
    + [Example 42 - Persist future data](#example-42---persist-future-data)
    + [Example 43 - Creating a rule dynamically using JRuleBuilder](#example-43---creating-a-rule-dynamically-using-jrulebuilder)

### Example 1 - Invoke another item Switch from rule

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("MyRuleTurnSwich2On")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void execChangedToRule() {
        logInfo("||||| --> Executing rule MyRule: changed to on");
        JRuleItems.MySwitch2.sendCommand(ON);
    }
}
```

### Example 2 - Using a timed lock

Use case: Invoke a Doorbell, but only allow the rule to be invoked once every 20 seconds.
This is done by acquiring a lock getTimedLock("MyLockTestRule1", 20).

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.OFF;
import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch2;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 3 - Using event data

Use case: Use the value that caused the trigger
When the rule is triggered, the triggered value is stored in the event.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch2;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("MyEventValueTest")
    @JRuleWhenItemReceivedCommand(item = MyTestSwitch2)
    public void myEventValueTest(JRuleEvent event) {
        logInfo("Got value from event: {}", event.getState().getValue());
    }
}
```

### Example 4 - Multiple rule triggers

Use case: Or statement for rule trigger
To add an OR statement we simply add multiple @JRuleWhenXXX statements

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestNumber;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 5 - Creating a custom parent rule class

Use case: Define your own functionality
Create a Rules class that extends: JRuleUser.java
JRuleUser.java should be placed in the same folder as your rules
The JRuleUser class can contain common functions and functionality you want to reuse in your rules:

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRule;

public abstract class JRuleUser extends JRule {

}
```

### Example 6 - Using a custom parent rule class

Your class rules can now extend the JRuleUser
package org.openhab.automation.jrule.rules.user;

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.automation.jrule.rules.user.JRuleUser;

public class MySwitchRule extends JRuleUser {

}
```

### Example 7 - Reusing rule functionality

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

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.OFF;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;

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

### Example 8 - Creating a timer

Use case create a timer for automatically turning off a light when it is turned on. If it's running cancel it and
schedule a new one.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyLightSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 9 - Creating a repeating timer

Use case: Let's say we have a 433 MHz wall socket with no ON/OFF feedback and a bit of bad radio reception. We can then
create a repeating timer
to send multiple ON statements to be sure it actually turns on.
createOrReplaceRepeatingTimer("myRepeatingTimer", 7, 4, will create a repeating timer that will trigger after 0 seconds,
7s, 14s and 21s
If the Timer is already running it will cancel it and create a new one.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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
            }
        });
    }
}
```

### Example 10 - Creating a timer #2

Use case Create a simple timer. When MyTestSwitch turns on it will wait 10 seconds and then turn MyTestSwitch2 to on.
Note that it will not reschedule the timer, if the timer is already running it won't reschedule it.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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
            }
        });
    }
}
```

### Example 11 - Using JRuleWhenTimeTrigger

Use case trigger a rule at 22:30 in the evening to set initial brightness for a ZwaveDimmer to 30%

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 12 - Condition on trigger

Use case: If temperature is below or equals to 20 degrees send command on to a heating fan
It is possible to use:
lte = less than or equals
lt = less than
gt = greater than
gte = greater than or equals
eq = equals
neq = not equals

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("turnOnFanIfTemperatureIsLow")
    @JRuleWhenItemChange(item = MyTemperatureSensor, condition = @JRuleCondition(lte = 20))
    public synchronized void turnOnFanIfTemperatureIsLow(JRuleEvent event) {
        logInfo("Starting fan since temperature dropped below 20");
        JRuleItems.MyHeatingFanSwitch.sendCommand(JRuleOnOffValue.ON);
    }
}
```

### Example 13 - Text to speech

Use case: Using say command for tts

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 14 - Executing a shell command

Use case: Executing command from CLI

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MySwitchGroup;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("TestExecutingCommandLine")
    @JRuleWhenItemReceivedCommand(item = MySwitchGroup)
    public synchronized void testExecutingCommandLine(JRuleEvent event) {
        logInfo("Creating dummy file using CLI");
        executeCommandLine("touch", "/openhab/userdata/example.txt");
    }
}
```

### Example 15 - Group items

Use case: A group of switches, see if status is changed, and also which member in the group changed state

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MySwitchGroup;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("groupMySwitchesChanged")
    @JRuleWhenItemChange(item = MySwitchGroup)
    public synchronized void groupMySwitchGroupChanged(JRuleEvent event) {
        final boolean groupIsOnline = ((JRuleItemEvent) event).getState().getValueAsOnOffValue() == JRuleOnOffValue.ON;
        final String memberThatChangedStatus = ((JRuleItemEvent) event).getMemberItem.getName();
        logInfo("Member that changed the status of the Group of switches: {}", memberThatChangedStatus);
    }
}
```

### Example 16 - Group items #2

Use case: A group of switches , trigger when it's changed from OFF to ON

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MySwitchGroup;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("groupMySwitchesChangedOffToOn")
    @JRuleWhenItemChange(item = MySwitchGroup, from = OFF, to = ON)
    public synchronized void groupMySwitchesChangedOffToOn(JRuleEvent event) {
        logInfo("Member that changed the status of the Group from OFF to ON: {}", event.getMemberItem().getName());
    }
}
```

### Example 17 - Channel triggers

Use case: Listen for a Channel Trigger Event

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("ChannelTriggered")
    @JRuleWhenChannelTrigger(channel = binding_thing.buttonevent)
    public synchronized void channelTriggered(JRuleEvent event) {
        logInfo("Channel triggered with value: {}", ((JRuleChannelEvent) event).getEvent());
    }
}
```

### Example 18 - Cron based trigger

Use case: Cron based expression to trigger rule

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("testCron")
    @JRuleWhenCronTrigger(cron = "*/5 * * * * *")
    public void testCron(JRuleEvent event) {
        logInfo("CRON: Running cron from string every 5 seconds: {}", event);
    }
}
```

### Example 19 - Persistence and lastUpdated

Use case: lastUpdate for an item
Note that `ZonedDateTime lastUpdate = JRuleItems.MyCoolItem.lastUpdate("mapdb");`
can be called without serviceId
argument: `ZonedDateTime lastUpdate = JRuleItems.MyCoolItem.lastUpdate();`

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("testLastUpdate")
    @JRuleWhenCronTrigger(cron = "4 * * * * *")
    public void testLastUpdate(JRuleEvent event) {
        logInfo("CRON: Running cron from string: {}", event.getState().getValue());
        ZonedDateTime lastUpdate = JRuleItems.MyCoolItem.lastUpdate("mapdb");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss Z");
        String lastUpdateFormatted = lastUpdate.format(formatter);
        logInfo("Last Update: {}", lastUpdateFormatted);
    }
}
```

### Example 20 - Color item

Use case: Get the brigtness from a color item, set a color item to white (HSB 0, 0, 100)

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestColorItem;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 21 - Set logging name for a specific rule

```java 
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 22 - Override logging for all rules defined in one file

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 23 - Apply transformation using openHAB transformation service

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyStringValue;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 24 - Preconditions #1

Use case: Use precondition annotation in order to create "AND" logic. Example we have a switch that will tell
if it is ok for disturbance. If it is ok the switch is set to ON and we can send a notification if the notification
message is updated.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyDisturbanceSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 25 - Preconditions #2

Use case: Use precondition annotation in order to create "AND" logic. Example when the temperature is above 30 degrees (
celcius probably) and
a motion detector is triggered we will turn on a fan.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyMotionDetector;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.OFF;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 26 - Send Quantity type Watt (W) from rule

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestMeterPower;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("testQuantityPowerWatt")
    @JRuleWhenItemChange(item = MyTestMeterPower)
    public void testQuantityPower(JRuleEvent event) {
        logInfo("TestQuantity power will send this value as Watt: {}", event.getState().getValue());
        JRuleItems.TestPowerQuantityType.sendCommand(event.getState().getValueAsDouble(), "W");
    }
}
```

### Example 27 - Use forName to create and item and send commands and get status

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 27b - Use forNameOptional to create and item and send commands and get status

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("testForNameOptional")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void testForName(JRuleEvent event) {
        JRuleSwitchItem.forNameOptional("MyOtherTestSwitch").ifPresent(item -> item.sendCommand(true));
    }
}
```

### Example 28 - Get the name of the item that triggered the rule as well as new and old state value

This can be useful if you have multiple JRuleWhen with different items, and you want to know which item
triggered the rule.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch1;
import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch2;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 29 - Get average value for a Number item last hour

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("testAverageLastHour")
    @JRuleWhenCronTrigger(cron = "4 * * * * *")
    public void testAverage(JRuleEvent event) {
        Double average = JRuleItems.MyNumberItem.averageSince(ZonedDateTime.now().minus(1, ChronoUnit.HOURS));
        logInfo("Average value last hour: {}", average);
    }
}
```

### Example 30 - Use generated JRuleItems.java to get hold of items

For instance get state of an item.

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.items.JRuleItemNames.MyTestSwitch;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("testItems")
    @JRuleWhenItemChange(item = MyTestSwitch, to = ON)
    public void testItems(JRuleEvent event) {
        JRuleItems.MyOtherTestSwitch.getState();
    }
}
```

### Example 31 - Restart thing every night due to binding flakyness

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenTimeTrigger;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("Restart thing every night")
    @JRuleWhenTimeTrigger(hours = 3)
    public void restartThing() {
        JRuleThings.my_flaky_thing.restart();
    }
}
```

### Example 32 - Detect if a specific thing goes offline, wait for it to come online again within a given time

```java
package org.openhab.automation.jrule.rules.user;

import static org.openhab.automation.jrule.generated.things.JRuleThings.remoteopenhab_thing;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenThingTrigger;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 33 - Listen for thing status events on _all_ things

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenThingTrigger;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("Log every thing that goes offline")
    @JRuleWhenThingTrigger(from = JRuleThingStatus.ONLINE)
    public void startTrackingNonOnlineThing(JRuleEvent event) {
        String offlineThingUID = event.getThing();
        // ...
    }
}
```

### Example 34 - Thing actions, send message with pushover and other services

Note that you will have to set up a pushover account as thing in openHAB.

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("PushOverTest")
    @JRuleWhenItemChange(item = MyTestSendPushOverButton, to = ON)
    public void testPower(JRuleEvent event) {
        logInfo("Sending Test message using pushover via actions");
        JRuleActions.pushoverPushoverAccountXYZ.sendMessage("MyMessage", "MyTitle");
    }
}
```

### Example 35 - Listen on all Item events of a group (without the groupstate must change)

Alternatively you could just listen to just Group changes or (real) Item changes

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 36 - Listen for group changes - with conditions

Use case: Want to listen just on changes where the state is now greater/equals then 12 and was before less then 12.
Without the previous condition the rule will be triggered every time the state is greater/equals then 12.

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

public class DemoRule extends JRule {
    @JRuleName("Change from something less to something greater")
    @JRuleWhenItemChange(item = ITEM_FROM_TO, previousCondition = @JRuleCondition(lt = 12), condition = @JRuleCondition(gte = 12))
    public void itemChangeFromTo(JRuleEvent event) {
        logInfo("state change to something >= 12 and was before < 12");
    }
}
```

### Example 37 - Timer chaining

Use case: Chain timers. Execute one and after this is expired, execute the next one.

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 38 - Debounce

Use case: Do not execute a rule too often

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleDebounce;

public class DemoRule extends JRule {
    @JRuleDebounce(unit = ChronoUnit.MINUTES, value = 60)
    @JRuleName("Notify if thing stays offline")
    @JRuleWhenItemChange(item = MySwitchGroup)
    public void debounceMethod() {
        // super critical stuff which shouldn't be called too often
    }
}
```

### Example 39 - HTTP requests

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRule;

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

### Example 40 - Delay rule execution

Use case: Execute a rule delayed without manually creating a timer

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleDelayed;

public class DemoRule extends JRule {
    @JRuleDelayed(10)
    @JRuleName("Execute after ten seconds")
    @JRuleWhenItemChange(item = MySwitchGroup)
    public void delayedMethod() {
        // delay the execution of this
    }
}
```

## Example 41 - Get Metadata and Tags

Use case: Get Tags and Metadata of Items

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleDelayed;

public class DemoRule extends JRule {
  @JRuleName("read some metadata and tags")
  @JRuleWhenItemReceivedCommand(item = MyTrigger)
  public void getTagsAndMetadata() {
    JRuleDimmerItem item = JRuleItems.ItemWithMetadataAndTags;
    logInfo("Tags: '{}'", item.getTags());
    logInfo("Metadata: '{}'", item.getMetadata());
    logInfo("Metadata Value: '{}'", item.getMetadata().get("Speech").getValue());
    logInfo("Metadata Configuration: '{}'", item.getMetadata().get("Speech").getConfiguration());
  }
}
```

## Example 42 - Persist future data

Use case: Persist future data for e.g. Tibber future prices

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleDelayed;

public class DemoRule extends JRule {
  @JRuleName("persist new tibber prices")
  @JRuleWhenTimeTrigger(hours = 13, minutes = 30)
  public void persistNewPrices() {
    var availPrices = getPrices();  
    availPrices.forEach(t -> {
      ZonedDateTime zonedDateTime = t.getStartsAt().atZone(ZoneId.systemDefault());
      Optional<Double> historicState = JRuleItems.Tibber_Hourly_Cost_Future.historicStateAsDecimal(zonedDateTime, PERSISTENCE);
      logDebug("adding available price for: {}, existing: {}", zonedDateTime, historicState);
      if (historicState.isEmpty()) {
        JRuleItems.Tibber_Hourly_Cost_Future.persist(new JRuleDecimalValue(t.getTotal()), zonedDateTime, PERSISTENCE);
      }
    });
  }
}
```

## Example 43 - Creating a rule dynamically using JRuleBuilder

Use case: Build a rule dynamically during runtime without static annotations. Can be used when rule parameters are not
known at compile time, e.g. when they are read from a configuration file

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleMemberOf;

public class DynamicRuleModule extends JRule {

    public DynamicRuleModule() {
        registerDynamicRules();
    }

    private void registerDynamicRules() {
        logInfo("Registering Dynamic JRules");

        JRuleEngine.get().createJRuleBuilder("Example dynamic rule", event ->
                        logInfo("Received command {}", event)
                )
                .whenItemChange("MyItem", JRuleMemberOf.None, "OFF", "ON", null, null)
                .build();
    }
}
```
