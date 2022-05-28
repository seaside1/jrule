<#include "Header.ftlh">

package ${item.itemPackage};

import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

import java.util.Date;
import java.time.ZonedDateTime;

/**
 * Automatically Generated Class
 * for Item ${item.itemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${item.itemClassName} implements JRuleCommonTrigger {

<#include "StaticVariables.ftlh">
     
    public static Date getState() {
        return JRuleItemRegistry.get(ITEM, JRuleDateTimeItem.class).getState();
    }

    public static ZonedDateTime getZonedDateTimeState() {
        return JRuleItemRegistry.get(ITEM, JRuleDateTimeItem.class).getZonedDateTimeState();
    }

    public static void sendCommand(Date date) {
        JRuleItemRegistry.get(ITEM, JRuleDateTimeItem.class).sendCommand(date);
    }

    public static void sendCommand(ZonedDateTime zonedDateTime) {
        JRuleItemRegistry.get(ITEM, JRuleDateTimeItem.class).sendCommand(zonedDateTime);
    }

    public static void postUpdate(Date date) {
        JRuleItemRegistry.get(ITEM, JRuleDateTimeItem.class).postUpdate(date);
    }

    public static void postUpdate(ZonedDateTime zonedDateTime) {
        JRuleItemRegistry.get(ITEM, JRuleDateTimeItem.class).postUpdate(zonedDateTime);
    }
}
