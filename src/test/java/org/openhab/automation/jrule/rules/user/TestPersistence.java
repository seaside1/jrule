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

import org.openhab.automation.jrule.items.*;
import org.openhab.automation.jrule.rules.*;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
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
    public void persistenceAllTypes(JRuleItemEvent event) throws InterruptedException {
        persistSwitch();
        persistNumber();
        // castQuantity();
        // castString();
        // castDateTime();
        // castPlayer();
        // castContact();
        //
        // // TODO: strange error in OH when using this
        // // castImage();
        // castRollershutter();
        // castDimmer();
        // castColor();
        // castLocation();
    }

    private void persistSwitch() {
        JRuleSwitchItem switchItem = JRuleSwitchItem.forName(ITEM_SWITCH_TO_PERSIST);
        logInfo("SWITCH now: {}", switchItem.getStateAsOnOff());
        Optional<JRuleValue> historicState = switchItem.getHistoricState(ZonedDateTime.now().minusSeconds(1),
                "influxdb");
        logInfo("SWITCH before: {}", historicState.map(JRuleValue::stringValue).orElse("NULL"));
        Optional<JRuleValue> initialState = switchItem.getHistoricState(ZonedDateTime.now().minusSeconds(2),
                "influxdb");
        logInfo("SWITCH initial: {}", initialState.map(JRuleValue::stringValue).orElse("NULL"));
    }

    private void persistNumber() {
        JRuleNumberItem switchItem = JRuleNumberItem.forName(ITEM_NUMBER_TO_PERSIST);
        logInfo("NUMBER now: {}", switchItem.getStateAsDecimal());
        Optional<JRuleValue> historicState = switchItem.getHistoricState(ZonedDateTime.now().minusSeconds(1),
                "influxdb");
        logInfo("NUMBER before: {}", historicState.map(JRuleValue::stringValue).orElse("NULL"));
        Optional<JRuleValue> initialState = switchItem.getHistoricState(ZonedDateTime.now().minusSeconds(3),
                "influxdb");
        logInfo("NUMBER initial: {}", initialState.map(JRuleValue::stringValue).orElse("NULL"));
    }
}
