<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleColorItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleColorValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRuleCommonTrigger {

<#include "StaticVariables.ftlh">

    public static JRuleColorValue getState() {
        return JRuleItemRegistry.get(ITEM, JRuleColorItem.class).getState();
    }

    public static JRuleOnOffValue getOnOffState() {
        return JRuleItemRegistry.get(ITEM, JRuleColorItem.class).getOnOffState();
    }

    public static int getPercentState() {
        return JRuleItemRegistry.get(ITEM, JRuleColorItem.class).getPercentState();
    }

    public static void sendCommand(JRuleColorValue command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void sendCommand(JRuleIncreaseDecreaseValue command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void sendCommand(int command) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleColorValue value) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).postUpdate(value);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).postUpdate(value);
    }

    public static void postUpdate(int value) {
        JRuleItemRegistry.get(ITEM, JRuleColorItem.class).postUpdate(value);
    }

}