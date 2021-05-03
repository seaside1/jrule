# J Rule
Write OpenHab Rules using Java

When the binding is started by it will generate items

- Get started with the binding
- Install the binding by copying the jrule-xx-xx.jar to openhab-addons folder
- In the GUI add a new JRule Engine specify a working directory (default /opt/jrule)
- When the binding is started it will:
1. Create JAVA source files for all items
2. Compile java source files and create a resulting jRule.jar file under /opt/jrule/jruleItems.jar
3. Compile any java rules file under /opt/jrule/rules/org/openhab/binding/jrule/rules/user/
4. Create jar files with dependencies used when creating your java-rules
      -  jruleItems.jar - Jar file containing the items class needed when developing java rules as a dependency
        -  jrule.jar - Contains binding dependencies, also needed when developing rules
        -   org.eclipse.jdt.annotation-2.2.100.jar Needed for @Nullable annotation (which is in the source)
        - slf4j-api-1.7.16.jar Dependency for logging

Once the JAVA rule engine has started and compile items successfully you can either copy the jar files
form /opt/jrule/jar/* to the place where you intend to develop the Java- Rules, or share that folder
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
        logger.debug("||||| --> Hello World!");
    }
}
```

Make sure you add the Jar-files from /opt/jrule/jar as dependencies.

Examples 

Use Case: Invoke another item Switch from rule
```java
    @JRuleName("MyRuleTurnSwich2On")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execChangedToRule() {
        logger.debug("||||| --> Executing rule MyRule: changed to on");
        _MySwitch2.sendCommand(ON);
    }
```

Use case: Invoke a Doorbell, but only allow the rule to be invoke once every 20 seconds:

```java
    @JRuleName("MyLockTestRule1")
    @JRuleWhen(item = _MyTestSwitch2.ITEM, trigger = _MyTestSwitch2.TRIGGER_CHANGED_FROM_OFF_TO_ON)
    public void execLockTestRule() {
        if (getTimedLock("MyLockTestRule1", 20)) {
            _MyDoorBellItem.sendCommand(ON);
            logger.debug("||||| --> Got Lock! Ding-dong !");
        } else {
            logger.debug("||||| --> Ignoring call to rule it is locked!");
        }
    }
```
Use case: Use the value that caused the trigger
```java
   @JRuleName("MyEventValueTest")
   @JRuleWhen(item = __MyTestSwitch2.ITEM, trigger = __MyTestSwitch2.TRIGGER_RECEIVED_COMMAND)
   public void myEventValueTest(JRuleEvent event) {
	  logger.info("Got value from event: {}", event.getValue());
   }
```
Use case: Or statement for rule trigger
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
I want to add a function that checks if it is ok to send notifications debing on what time it is.
I'll do this:

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
I then extend the rule from my Java Rules file:

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

Use case: Create a timer

Use case: create a lock

Use case: Create or reschedule lock
Use case: Using say command for tts
``
`
