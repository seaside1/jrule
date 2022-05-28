<#include "Header.ftlh">

package ${item.itemPackage};

import java.util.Set;

import org.openhab.automation.jrule.items.JRuleGroupItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRuleCommonTrigger {

<#include "StaticVariables.ftlh">
    
    public static String getState() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getState();
    }

    public static JRuleOnOffValue getStateAsOnOffValue() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getStateAsOnOffValue();
    }

    public static JRulePlayPauseValue getStateAsPlayPauseValue() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getStateAsPlayPauseValue();
    }

    public static JRuleUpDownValue getStateAsUpDownValue() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).getStateAsUpDownValue();
    }

    public static void sendCommand(String command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(command);
    }

    public static void sendCommandToAll(String command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommandToAll(command);
    }

    public static Set<String> members() {
        return JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).members();
    }

    public static void postUpdate(String value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }

    public static void sendCommand(JRuleOnOffValue command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(command);
    }

    public static void postUpdate(JRuleOnOffValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }

    public static void sendCommand(JRulePlayPauseValue command) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(command);
    }

    public static void postUpdate(JRulePlayPauseValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }

    public static void sendCommand(JRuleUpDownValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).sendCommand(value);
    }

    public static void postUpdate(JRuleUpDownValue value) {
        JRuleItemRegistry.get(ITEM, JRuleGroupItem.class).postUpdate(value);
    }
}
