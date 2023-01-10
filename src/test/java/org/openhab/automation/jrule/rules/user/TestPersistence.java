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
package org.openhab.automation.jrule.rules.user;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Function;

import org.openhab.automation.jrule.items.*;
import org.openhab.automation.jrule.rules.*;
import org.openhab.automation.jrule.rules.value.*;

/**
 * The {@link TestPersistence}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class TestPersistence extends JRule {
    public static final String ITEM_SWITCH_TO_PERSIST = "Switch_To_Persist";
    public static final String ITEM_NUMBER_TO_PERSIST = "Number_To_Persist";
    public static final String ITEM_QUANTITY_TO_PERSIST = "Quantity_To_Persist";
    public static final String ITEM_DIMMER_TO_PERSIST = "Dimmer_To_Persist";
    public static final String ITEM_COLOR_TO_PERSIST = "Color_To_Persist";
    public static final String ITEM_STRING_TO_PERSIST = "String_To_Persist";
    public static final String ITEM_DATETIME_TO_PERSIST = "DateTime_To_Persist";
    public static final String ITEM_PLAYER_TO_PERSIST = "Player_To_Persist";
    public static final String ITEM_CONTACT_TO_PERSIST = "Contact_To_Persist";
    public static final String ITEM_IMAGE_TO_PERSIST = "Image_To_Persist";
    public static final String ITEM_ROLLERSHUTTER_TO_PERSIST = "Rollershutter_To_Persist";
    public static final String ITEM_LOCATION_TO_PERSIST = "Location_To_Persist";
    public static final String ITEM_TRIGGER_RULE = "Trigger_Rule";
    public static final String NAME_PERSIST_ALL_TYPES = "persist all types";
    public static final String COMMMAND_PERISTENCE = "peristence";

    @JRuleName(NAME_PERSIST_ALL_TYPES)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_RULE, condition = @JRuleCondition(eq = COMMMAND_PERISTENCE))
    public void persistenceAllTypes() {
        persist(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, i -> ((JRuleSwitchItem) i).getStateAsOnOff());
        persist(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, i -> ((JRuleNumberItem) i).getStateAsDecimal());
        persist(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName,
                i -> ((JRuleQuantityItem) i).getStateAsQuantity());
        persist(ITEM_STRING_TO_PERSIST, JRuleStringItem::forName, i -> i.getStateAsString());
        persist(ITEM_DATETIME_TO_PERSIST, JRuleDateTimeItem::forName,
                i -> ((JRuleDateTimeItem) i).getStateAsDateTime());
        persist(ITEM_PLAYER_TO_PERSIST, JRulePlayerItem::forName, i -> ((JRulePlayerItem) i).getStateAsPlayPause());
        persist(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, i -> ((JRuleContactItem) i).getStateAsOpenClose());
        persist(ITEM_IMAGE_TO_PERSIST, JRuleImageItem::forName, i -> ((JRuleImageItem) i).getStateAsRaw());
        persist(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName,
                i -> ((JRuleRollershutterItem) i).getStateAsPercent());
        persist(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, i -> ((JRuleDimmerItem) i).getStateAsPercent());
        persist(ITEM_COLOR_TO_PERSIST, JRuleColorItem::forName, i -> ((JRuleColorItem) i).getStateAsHsb());
        persist(ITEM_LOCATION_TO_PERSIST, JRuleLocationItem::forName, i -> ((JRuleLocationItem) i).getStateAsPoint());
    }

    private void persist(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        JRuleItem jRuleItem = getItem.apply(itemName);
        logInfo("{} now: {}", itemName, currentValue.apply(jRuleItem));
        Optional<JRuleValue> historicState = jRuleItem.getHistoricState(ZonedDateTime.now().minusSeconds(1),
                "influxdb");
        logInfo("{} before: {}", itemName, historicState.map(JRuleValue::stringValue).orElse("NULL"));
        Optional<JRuleValue> initialState = jRuleItem.getHistoricState(ZonedDateTime.now().minusSeconds(10),
                "influxdb");
        logInfo("{} initial: {}", itemName, initialState.map(JRuleValue::stringValue).orElse("NULL"));
    }
}
