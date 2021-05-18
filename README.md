# J Rule
Write OpenHAB Rules using Java

This binding aims to enable Java development of OpenHAB Rules. The binding will allow the user to create custom OpenHAB rules
in a java-source file. The Java Rules will need defined triggers in order for the engine to know how and when to exectute them. The triggers
are very similar to the triggers in Rules DSL but expressed using java annotations.
In order to execture rules based on items defined in .items-files (the regular OpenHAB Items), the binding need to know about the items and
this is realized by the Rule Engine when it generates .java and .class files for each items in the system. The class files are packaged in a .jar
file which the user can use as dependency when doing Rules Development.
In order for the binding to pick up rules, they need to be compiled first. The source .java -files are placed in a specific rules folder and
will be automatically compiled and loaded when the binding is started.
The syntax for rules as well as the design and thinking behind the binding is to provide something that is similar to 
Rules DSL but more powerful and customizable.

# Why?
 - You will be able to use a standard Java IDE to develop your rules. 
 - Full auto completion (Shift space) for all items, less chance of errors and typos
 - Take full advantage of all java design patters
 - Share and reuse code for you rules
 - Advanced timers and locks are built in and can be used without cluttering the code
 - Possibility to write junit-tests to test your rules
 - Use any 3rd party dependencies and libraries in your rules

# Who?
This binding is not for beginners, you should have knowledge in writing java-programs or a desire to do so.

# Maturity
Alpha, you can expect big changes in syntax and everything else. Please contribute if you can

# Download
Prebuilt jar file is available in the bin folder under https://github.com/seaside1/jrule

# Java Rule Engine
The only configuration that has to be done in the binding is the working directory. For instance the default location is:
/etc/openhab/jrule

Input rules files
will be placed under:
/etc/openhab/jrule/rules/org/openhab/binding/jrule/rules/user/

Output jar files to be added by the user as dependencies when doing rule development will be located under:
/etc/openhab/jrule/jar

The following jar files can be found under the jrule/jar-folder:

| Jar File                               | Description                                                                                   |
| -------------------------------------- | --------------------------------------------------------------------------------------------- |
| jruleItems.jar                         | Contains all generated items, which will be used when developing rules                        |
| jRule.jar                              | JRule Binding classes neeed as dependency when doing development                              |
| org.eclipse.jdt.annotation-2.2.100.jar | eclipse annotations jar file needed for development to be able to compile                     |
| slf4j-api-1.7.16.jar                   | Used for logging also needed in local rule development                                        |
| used-rules.jar                         | The user compiled rules, used internally by the binding in the classpath for rules execution  |


# Get started with the binding
- Install the binding by copying the jrule-xx-xx.jar to openhab-addons folder
- In the GUI add a new JRule Engine specify a working directory (default /etc/openhab/jrule)
- When the binding is started it will:
1. Create JAVA source files for all items 
2. Compile java source files and create a resulting jRule.jar file under /etc/openhab/jrule/jar
3. Compile any java rules file under  /etc/openhab/jrule/rules/org/openhab/binding/jrule/rules/user/
4. Create jar files with dependencies to be used when creating your java-rules

Once the JAVA rule engine has started and compile items successfully you can either copy the jar files
form /etc/openhab/jrule/jar/* to the place where you intend to develop the Java- Rules, or share that folder
using samba / CIFS / NFS or similar.
- Set up your favourite IDEA as a standard java IDEA. 
- Create a new empty java project
- Create a package / folder org.openhab.binding.jrule.rules.user
- Place your Java rules file in this folder.

Designing your Java Rules File (Hello World)
1. Start by adding an item in Openhab.
Group JRule
Switch MyTestSwitch  "Test Switch" (JRule)
Switch MyTestSwitch2  "Test Switch 2" (JRule)

2. Create the following class

```java
package org.openhab.binding.jrule.rules.user;
import static org.openhab.binding.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.binding.jrule.items.generated._MyTestSwitch;
import org.openhab.binding.jrule.rules.JRule;
import org.openhab.binding.jrule.rules.JRuleName;
import org.openhab.binding.jrule.rules.JRuleWhen;
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

Examples 

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
package org.openhab.binding.jrule.rules.user;

import org.openhab.binding.jrule.rules.JRule;

public class JRuleUser extends JRule {

	
}
```

Your class rules can now extend the JRuleUser
package org.openhab.binding.jrule.rules.user;
```java
import static org.openhab.binding.jrule.rules.JRuleOnOffValue.ON;
import org.openhab.binding.jrule.items.generated._MyTestSwitch;
import org.openhab.binding.jrule.rules.JRule;
import org.openhab.binding.jrule.rules.user.JRuleUser;
import org.openhab.binding.jrule.rules.JRuleName;
import org.openhab.binding.jrule.rules.JRuleWhen;
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
package org.openhab.binding.jrule.rules.user;

import org.openhab.binding.jrule.rules.JRule;

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
package org.openhab.binding.jrule.rules.user;

import org.openhab.binding.jrule.items.generated._MyTestSwitch;
import org.openhab.binding.jrule.rules.JRuleEvent;
import org.openhab.binding.jrule.rules.JRuleName;
import org.openhab.binding.jrule.rules.JRuleWhen;
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


Use case: Create a timer
Use case: Create or reschedule lock
Use case: Using say command for tts
Use case: Crond timer
Use case: Greater than, less than, eq

# Current Binding Limitations (this will be fixed)
- Items files will only be generated if the java-source and .class files under /etc/openhab/jrule/items/
is removed. Do a rm /etc/openhab/jrule/items/* before starting openhab and the items will be regenerated
- Adding new .java rules files will the binding is started will often not work, you might have to restart openhab for the binding to pick them up
- Some items are not supported for instance Player, Group:TEMPERATURE Group:SWITCH they will be added later on

# Roadmap
- Locks and timers by annotation
- Built in expire functionality
- Built in shell exec functionality
- 3rd party jar files included in class path
