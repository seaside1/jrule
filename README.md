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

package org.openhab.binding.jrule.rules.user;
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

Make sure you add the Jar-files from /opt/jrule/jar as dependencies.

Use Case Invoke another item Switch from rule

    @JRuleName("MyRuleTurnSwich2On")
    @JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)
    public void execChangedToRule() {
        logger.debug("||||| --> Executing rule MyRule: changed to on");
        _MySwitch2.sendCommand(JRuleOnOffValue.ON);
    }

Use case Invoke a Doorbell, but only allow the rule to be invoke once every 20 seconds:
    @JRuleName("MyLockTestRule1")
    @JRuleWhen(item = _MyTestSwitch2.ITEM, trigger = _MyTestSwitch2.TRIGGER_CHANGED_FROM_OFF_TO_ON)
    public void execLockTestRule() {
        if (getTimedLock("MyLockTestRule1", 20)) {
            _MyDoorBellItem.sendCommand(JRuleOnOffValue.ON);
            logger.debug("||||| --> Got Lock! Ding-dong !");
        } else {
            logger.debug("||||| --> Ignoring call to rule it is locked!");
        }
    }
    
    
- Dynamic reloading of single rule http://tutorials.jenkov.com/java-reflection/dynamic-class-loading-reloading.html#dynamicreloading
