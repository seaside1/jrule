/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package ${package};

import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.trigger.JRuleCommonTrigger;

import java.util.Date;
import java.time.ZonedDateTime;

/**
 * Automatically Generated Class
 * for Item ${ItemName}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class ${ItemClass} implements JRuleCommonTrigger {

    public static final String ITEM = "${ItemName}";
     
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
