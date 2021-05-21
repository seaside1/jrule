# J Rule
Write OpenHAB Rules using Java

This automation package aims to enable Java development of OpenHAB Rules. The addon will allow the user to create custom OpenHAB rules
in one or several .java-files. The Java Rules will need defined triggers in order for the engine to know how and when to execute them. The triggers
are very similar to the triggers in Rules DSL but expressed using java annotations. In order to execute rules based on items defined in OpenHAB either in .items-files or the GUI. The addon needs to know about these items and this is realized by the Rule Engine where it generates a .java and a .class file for each item in the system. The class files are then packaged in a .jar-file which the user can use as dependency when doing Rules Development.
For the addon to be able to pick up rules, they first need to be compiled by the addon. The source .java rules-files are placed in a specific rules folder and
will be automatically compiled and loaded into OpenHAB when the addon is started. The syntax for rules as well as the design and thinking behind the addon is to provide something that is similar to Rules DSL but more powerful and customizable.

# Limitations
- Currently only working for OpenHab installations under Linux / Unix Operating Systems, not supported in Windows (for rules development its fine to use windows)
- Not supporting OH3 GUI rules, script actions and script conditions 

# Why?
 - You will be able to use a standard Java IDE to develop your rules. 
 - Full auto completion (Shift space) for all items, less chance of errors and typos
 - Take full advantage of all java design patters
 - Share and reuse code for you rules
 - Advanced timers and locks are built in and can be used without cluttering the code
 - Possibility to write junit-tests to test your rules
 - Use any 3rd party dependencies and libraries in your rules
 - You will be able to use JRule in parallell with any other Rules engine if you want to give it a try

# Who?
This addon is not for beginners, you should have knowledge in writing java-programs or a desire to do so.

# Maturity
Alpha, you can expect big changes in syntax and everything else. Please contribute if you can

# Download
Prebuilt jar file is available in the bin folder under https://github.com/seaside1/jrule

# Java Rule Engine

Input rules files
will be placed under:
/etc/openhab/automation/jrule/rules/org/openhab/automation/jrule/rules/user/

Output jar files to be added by the user as dependencies when doing rule development will be located under:
/etc/openhab/automation/jrule/jar

The following jar files can be found under the jrule/jar-folder:

| Jar File                               | Description                                                                                   |
| -------------------------------------- | --------------------------------------------------------------------------------------------- |
| jrule-items.jar                        | Contains all generated items, which will be used when developing rules                        |
| jrule.jar                              | JRule Addon classes neeed as dependency when doing development                              |
| org.eclipse.jdt.annotation-2.2.100.jar | Eclipse annotations jar file needed for development to be able to compile                     |
| slf4j-api-1.7.16.jar                   | Used for logging in local rule development                                        |
| user-rules.jar                         | The user compiled rules, used internally by the addon in the classpath for rules execution  |


# Get started with the JRule Automation Addon
- Install the addon by copying the org.openhab.automation.jrule-3.x.x-ALPHAX.jar to openhab-addons folder
  Download latest release from https://github.com/seaside1/jrule/tree/main/bin
- In default location is /etc/openhab/automation/jrule
- When the addon is started it will:
1. Create JAVA source files for all items 
2. Compile java source files and create a resulting jrule.jar file under /etc/openhab/automation/jrule/jar
3. Compile any java rules file under  /etc/openhab/automation/jrule/rules/org/openhab/automation/jrule/rules/user/
4. Create jar files with dependencies to be used when creating your java-rules (jrule-items.jar).
All dependencies need for Java rules development can be found under /etc/openhab/automation/jrule/jar

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
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.automation.jrule.items.generated._MyTestSwitch;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySwitchRule extends JRule {

    private final Logger logger = LoggerFactory.getLogger(MySwitchRule.class);

    @JRuleName("MySwitchRule")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execOffToOnRule() {
        logger.info("||||| --> Hello World!");
    }
}
```

Make sure you add the Jar-files from /etc/openhab/jrule/jar as dependencies.

# Third Party External Dependencies
You can add any 3rd party library as dependency. Copy the jar files needed to /etc/openhab/automation/jrule/ext-lib
The Automation Engine will automatically pick these dependencies up when it is compiling the rules.

# Examples 

Use Case: Invoke another item Switch from rule
```java
    @JRuleName("MyRuleTurnSwich2On")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execChangedToRule() {
        logger.info("||||| --> Executing rule MyRule: changed to on");
        _MySwitch2.sendCommand(ON);
    }
```

Use case: Invoke a Doorbell, but only allow the rule to be invoke once every 20 seconds.
This is done by aquiring a lock getTimedLock("MyLockTestRule1", 20).

```java
    @JRuleName("MyLockTestRule1")
    @JRuleWhen(item = _MyTestSwitch2.ITEM, trigger = _MyTestSwitch2.TRIGGER_CHANGED_FROM_OFF_TO_ON)
    public void execLockTestRule() {
        if (getTimedLock("MyLockTestRule1", 20)) {
            _MyDoorBellItem.sendCommand(ON);
            logger.info("||||| --> Got Lock! Ding-dong !");
        } else {
            logger.info("||||| --> Ignoring call to rule it is locked!");
        }
    }
```
Use case: Use the value that caused the trigger
When the rule is triggered, the triggered value is stored in the event.

```java
   @JRuleName("MyEventValueTest")
   @JRuleWhen(item = __MyTestSwitch2.ITEM, trigger = __MyTestSwitch2.TRIGGER_RECEIVED_COMMAND)
   public void myEventValueTest(JRuleEvent event) {
	  logger.info("Got value from event: {}", event.getValue());
   }
```
Use case: Or statement for rule trigger
To add an OR statement we simply add multiple @JRuleWhen statements

```java
   @JRuleName("MyNumberRule1")
   @JRuleWhen(item = _MyTestNumber.ITEM, trigger = _MyTestNumber.TRIGGER_CHANGED, from = "14", to = "10")
   @JRuleWhen(item = _MyTestNumber.ITEM, trigger = _MyTestNumber.TRIGGER_CHANGED, from = "10", to = "12")
   public void myOrRuleNumber(JRuleEvent event) {
	logger.info("Got change number: {}", event.getValue());
   }
```

Use case: Define your own functionality
Create a Rules class that extends: JRuleUser.java
JRuleUser.java should be placed in the same folder as your rules:

```java
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRule;

public class JRuleUser extends JRule {

	
}
```

Your class rules can now extend the JRuleUser
package org.openhab.automation.jrule.rules.user;
```java
import static org.openhab.automation.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.automation.jrule.items.generated._MyTestSwitch;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.user.JRuleUser;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySwitchRule extends JRuleUser {

    private final Logger logger = LoggerFactory.getLogger(MySwitchRule.class);
}
```

Let's say we want to add a common function that should be available for all user rules.
We want to add a function that checks if it is ok to send notifications debing on what time it is.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTestUserRule extends JRuleUser {
	private final Logger logger = LoggerFactory.getLogger(MyTestUserRule.class);

	@JRuleName("TestUserDefinedRule")
	@JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_RECEIVED_COMMAND)
	public void mySendNotificationRUle(JRuleEvent event) {
		if (timeIsOkforDisturbance()) {
			logger.info("It's ok to send a distrubing notification");
		}
	}

}
```

Use case create a timer for automatically turning of a light when it is turned on. If it's running cancel it and schedule a new one. 
```java
    @JRuleName("myTimerRule")
    @JRuleWhen(item = _MyLightSwitch.ITEM, trigger = _MyLightSwitch.TRIGGER_CHANGED_TO_ON)
    public synchronized void myTimerRule(JRuleEvent event) {
        logger.info("myTimerRule Turning on light it will be turned off in 2 mins");
        createOrReplaceTimer(_MyLightSwitch.ITEM, 2 * 60, (Void) -> { // Lambda Expression
            logger.info("Time is up! Turning off lights");
            _MyLightSwitch.sendCommand(OFF);
        });
    }
```

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
            looger.info(messageOn);
            _MyBad433Switch.sendCommand(ON);
        });
    }
```

Use case Create a simple timer. When MyTestSwitch turns on it will wait 10 seconds and then turn MyTestSwitch2 to on. Note that
it will not reschedule the timer, if the timer is already running it won't reschedule it.
```java
    @JRuleName("timerRuleExample")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public synchronized void timerRuleExample(JRuleEvent event) {
        createTimer("myTimer", 10, (Void) -> { // Lambda Expression
            final String messageOn = "timer example.";
            looger.info(messageOn);
            _MyTestWitch2.sendCommand(ON);
        });
    }
```

Use case trigger a rule at 22:30 in the evening to set initial brightness for a ZwaveDimmer to 30%
```java
  @JRuleName("setDayBrightness")
  @JRuleWhen(hours=22, minutes=30)
  public synchronized void setDayBrightness(JRuleEvent event) {
      logger.info("Setting night brightness to 30%");
      int dimLevel = 30;
      _ZwaveDimmerBrightness.sendCommand(dimLevel);
  }
```

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
      logger.info("Starting fan since temeprature dropped below 20");
      _MyHeatinFanSwitch.sendCommand(ON);
  }
```


Use case: Using say command for tts


# Current Addon Limitations (this will be fixed)
- Some items are not supported for instance Player, Group:TEMPERATURE Group:SWITCH they will be added later on

# Changelog
## ALPHA2
- Added possibility to include 3rd party libraries when developing rules
## ALPHA1
- Refactored internal jar dependencies and jar-generation
- Added eq comparator for number triggers in rules

# Roadmap
- Locks and timers by annotation
- Built in expire functionality
- Built in shell exec functionality
