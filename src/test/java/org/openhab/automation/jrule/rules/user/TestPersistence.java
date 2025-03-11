/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.openhab.automation.jrule.items.*;
import org.openhab.automation.jrule.persistence.JRuleHistoricState;
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
    public static final String NAME_PERSIST_IN_FUTURE = "persist in future";
    public static final String NAME_QUERY_IN_FUTURE = "query in future";
    public static final String COMMMAND_PERISTENCE = "persistence";
    public static final String COMMMAND_PERISTENCE_IN_FUTURE = "persistence_in_future";
    public static final String COMMMAND_QUERY_IN_FUTURE = "query_in_future";
    public static final String PERSISTENCE_SERVICE_ID = "influxdb";
    public static final String ITEM_NUMBER_TO_PERSIST_FUTURE = "Number_To_Persist_Future";

    private final ZonedDateTime now = ZonedDateTime.now();

    @JRuleName(NAME_PERSIST_IN_FUTURE)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_RULE, condition = @JRuleCondition(eq = COMMMAND_PERISTENCE_IN_FUTURE))
    public void persistFuture() {
        JRuleNumberItem jRuleNumberItem = JRuleNumberItem.forName(ITEM_NUMBER_TO_PERSIST_FUTURE);

        jRuleNumberItem.persist(now, new JRuleDecimalValue(10), PERSISTENCE_SERVICE_ID);
        jRuleNumberItem.persist(now.plusHours(1), new JRuleDecimalValue(20), PERSISTENCE_SERVICE_ID);
        jRuleNumberItem.persist(now.plusHours(2), new JRuleDecimalValue(30), PERSISTENCE_SERVICE_ID);
    }

    @JRuleName(NAME_QUERY_IN_FUTURE)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_RULE, condition = @JRuleCondition(eq = COMMMAND_QUERY_IN_FUTURE))
    public void queryFuture() {
        JRuleNumberItem jRuleNumberItem = JRuleNumberItem.forName(ITEM_NUMBER_TO_PERSIST_FUTURE);

        logInfo("now: {}", jRuleNumberItem.historicState(now, PERSISTENCE_SERVICE_ID).map(JRuleHistoricState::getValue)
                .orElseThrow());
        logInfo("now +1: {}", jRuleNumberItem.historicState(now.plusHours(1), PERSISTENCE_SERVICE_ID)
                .map(JRuleHistoricState::getValue).orElseThrow());
        logInfo("now +2: {}", jRuleNumberItem.historicState(now.plusHours(2), PERSISTENCE_SERVICE_ID)
                .map(JRuleHistoricState::getValue).orElseThrow());

        logInfo("now stateAt: {}", jRuleNumberItem.historicState(now, PERSISTENCE_SERVICE_ID)
                .map(JRuleHistoricState::getValue).orElseThrow());
        logInfo("now stateAt +1: {}", jRuleNumberItem.historicState(now.plusHours(1), PERSISTENCE_SERVICE_ID)
                .map(JRuleHistoricState::getValue).orElseThrow());
        logInfo("now stateAt +2: {}", jRuleNumberItem.historicState(now.plusHours(2), PERSISTENCE_SERVICE_ID)
                .map(JRuleHistoricState::getValue).orElseThrow());
    }

    @JRuleName(NAME_PERSIST_ALL_TYPES)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_RULE, condition = @JRuleCondition(eq = COMMMAND_PERISTENCE))
    public void persistenceAllTypes() {
        Function<JRuleItem, Object> switchStateFunction = i -> ((JRuleSwitchItem) i).getStateAsOnOff();
        historicState(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        previousState(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        sumSince(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        averageSince(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        minimumSince(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        maximumSince(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        deviationSince(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        varianceSince(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);
        changedSince(ITEM_SWITCH_TO_PERSIST, JRuleSwitchItem::forName, switchStateFunction);

        Function<JRuleItem, Object> numberStateFunction = i -> ((JRuleNumberItem) i).getStateAsDecimal();
        historicState(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        previousState(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        sumSince(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        averageSince(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        minimumSince(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        maximumSince(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        deviationSince(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        varianceSince(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);
        changedSince(ITEM_NUMBER_TO_PERSIST, JRuleNumberItem::forName, numberStateFunction);

        Function<JRuleItem, Object> quantityStateFunction = i -> ((JRuleQuantityItem) i).getStateAsQuantity();
        historicState(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        previousState(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        sumSince(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        averageSince(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        minimumSince(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        maximumSince(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        deviationSince(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        varianceSince(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);
        changedSince(ITEM_QUANTITY_TO_PERSIST, JRuleQuantityItem::forName, quantityStateFunction);

        historicState(ITEM_STRING_TO_PERSIST, JRuleStringItem::forName, JRuleItem::getStateAsString);
        previousState(ITEM_STRING_TO_PERSIST, JRuleStringItem::forName, JRuleItem::getStateAsString);
        changedSince(ITEM_STRING_TO_PERSIST, JRuleStringItem::forName, JRuleItem::getStateAsString);

        historicState(ITEM_DATETIME_TO_PERSIST, JRuleDateTimeItem::forName,
                i -> ((JRuleDateTimeItem) i).getStateAsDateTime());
        previousState(ITEM_DATETIME_TO_PERSIST, JRuleDateTimeItem::forName,
                i -> ((JRuleDateTimeItem) i).getStateAsDateTime());
        changedSince(ITEM_DATETIME_TO_PERSIST, JRuleDateTimeItem::forName,
                i -> ((JRuleDateTimeItem) i).getStateAsDateTime());

        historicState(ITEM_PLAYER_TO_PERSIST, JRulePlayerItem::forName,
                i -> ((JRulePlayerItem) i).getStateAsPlayPause());
        previousState(ITEM_PLAYER_TO_PERSIST, JRulePlayerItem::forName,
                i -> ((JRulePlayerItem) i).getStateAsPlayPause());
        changedSince(ITEM_PLAYER_TO_PERSIST, JRulePlayerItem::forName,
                i -> ((JRulePlayerItem) i).getStateAsPlayPause());

        Function<JRuleItem, Object> contactStateFunction = i -> ((JRuleContactItem) i).getStateAsOpenClose();
        historicState(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        previousState(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        sumSince(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        averageSince(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        minimumSince(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        maximumSince(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        deviationSince(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        varianceSince(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);
        changedSince(ITEM_CONTACT_TO_PERSIST, JRuleContactItem::forName, contactStateFunction);

        historicState(ITEM_IMAGE_TO_PERSIST, JRuleImageItem::forName, i -> ((JRuleImageItem) i).getStateAsRaw());
        changedSince(ITEM_IMAGE_TO_PERSIST, JRuleImageItem::forName, i -> ((JRuleImageItem) i).getStateAsRaw());

        Function<JRuleItem, Object> rollershutterStateFunction = i -> ((JRuleRollershutterItem) i).getStateAsPercent();
        historicState(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        previousState(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        sumSince(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        averageSince(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        minimumSince(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        maximumSince(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        deviationSince(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        varianceSince(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);
        changedSince(ITEM_ROLLERSHUTTER_TO_PERSIST, JRuleRollershutterItem::forName, rollershutterStateFunction);

        Function<JRuleItem, Object> dimmerStateFunction = i -> ((JRuleDimmerItem) i).getStateAsPercent();
        historicState(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        previousState(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        sumSince(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        averageSince(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        minimumSince(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        maximumSince(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        deviationSince(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        varianceSince(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);
        changedSince(ITEM_DIMMER_TO_PERSIST, JRuleDimmerItem::forName, dimmerStateFunction);

        historicState(ITEM_COLOR_TO_PERSIST, JRuleColorItem::forName, i -> ((JRuleColorItem) i).getStateAsHsb());
        previousState(ITEM_COLOR_TO_PERSIST, JRuleColorItem::forName, i -> ((JRuleColorItem) i).getStateAsHsb());
        changedSince(ITEM_COLOR_TO_PERSIST, JRuleColorItem::forName, i -> ((JRuleColorItem) i).getStateAsHsb());

        historicState(ITEM_LOCATION_TO_PERSIST, JRuleLocationItem::forName,
                i -> ((JRuleLocationItem) i).getStateAsPoint());
        previousState(ITEM_LOCATION_TO_PERSIST, JRuleLocationItem::forName,
                i -> ((JRuleLocationItem) i).getStateAsPoint());
        changedSince(ITEM_LOCATION_TO_PERSIST, JRuleLocationItem::forName,
                i -> ((JRuleLocationItem) i).getStateAsPoint());
    }

    private void changedSince(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "changedSince";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> item.changedSince(ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID));
    }

    private void averageSince(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "averageSince";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> averageSince(item, ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID)
                        .map(v -> v.as(JRuleDecimalValue.class)).map(JRuleDecimalValue::floatValue).orElse(null));
    }

    private void sumSince(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "sumSince";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> sumSince(item, ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID)
                        .map(v -> v.as(JRuleDecimalValue.class)).map(JRuleDecimalValue::floatValue).orElse(null));
    }

    private void minimumSince(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "minimumSince";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> minimumSince(item, ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID)
                        .map((JRuleHistoricState t) -> t.getValue().stringValue()).orElse(null));
    }

    private void maximumSince(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "maximumSince";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> maximumSince(item, ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID)
                        .map((JRuleHistoricState t) -> (t.getValue()).stringValue()).orElse(null));
    }

    private void varianceSince(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "varianceSince";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> varianceSince(item, ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID)
                        .map(v -> v.as(JRuleDecimalValue.class)).map(JRuleDecimalValue::floatValue).orElse(null));
    }

    private void deviationSince(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "deviationSince";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> deviationSince(item, ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID)
                        .map(v -> v.as(JRuleDecimalValue.class)).map(JRuleDecimalValue::floatValue).orElse(null));
    }

    private void historicState(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "historicState";
        printInfos(itemName, getItem, currentValue, function, (i, item) -> item
                .historicState(ZonedDateTime.now().minusSeconds(i), PERSISTENCE_SERVICE_ID).orElse(null));
    }

    private void previousState(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue) {
        final String function = "previousState";
        printInfos(itemName, getItem, currentValue, function,
                (i, item) -> item.previousState(true, PERSISTENCE_SERVICE_ID).orElse(null));
    }

    private void printInfos(String itemName, Function<String, JRuleItem> getItem,
            Function<JRuleItem, Object> currentValue, String functionName,
            BiFunction<Integer, JRuleItem, Object> persistenceFunction) {
        JRuleItem jRuleItem = getItem.apply(itemName);

        for (int i = 7; i > 0; i -= 2) {
            var historicValue = persistenceFunction.apply(i, jRuleItem);
            logInfo("{}: {} since/before {}s: '{}'", functionName, itemName, i, historicValue);
        }

        logInfo("{}: {} now: {}", functionName, itemName, currentValue.apply(jRuleItem));
    }

    private Optional<JRuleHistoricState> minimumSince(JRuleItem jRuleItem, ZonedDateTime time,
            String persistenceServiceId) {
        try {
            Method method = jRuleItem.getClass().getMethod("minimumSince", ZonedDateTime.class, String.class);
            return (Optional<JRuleHistoricState>) method.invoke(jRuleItem, time, persistenceServiceId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error executing minimumSince", e);
        }
    }

    private Optional<JRuleHistoricState> maximumSince(JRuleItem jRuleItem, ZonedDateTime time,
            String persistenceServiceId) {
        try {
            Method method = jRuleItem.getClass().getMethod("maximumSince", ZonedDateTime.class, String.class);
            return (Optional<JRuleHistoricState>) method.invoke(jRuleItem, time, persistenceServiceId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error executing maximumSince", e);
        }
    }

    private Optional<JRuleValue> varianceSince(JRuleItem jRuleItem, ZonedDateTime time, String persistenceServiceId) {
        try {
            Method method = jRuleItem.getClass().getMethod("varianceSince", ZonedDateTime.class, String.class);
            return (Optional<JRuleValue>) method.invoke(jRuleItem, time, persistenceServiceId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error executing varianceSince", e);
        }
    }

    private Optional<JRuleValue> deviationSince(JRuleItem jRuleItem, ZonedDateTime time, String persistenceServiceId) {
        try {
            Method method = jRuleItem.getClass().getMethod("deviationSince", ZonedDateTime.class, String.class);
            return (Optional<JRuleValue>) method.invoke(jRuleItem, time, persistenceServiceId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error executing deviationSince", e);
        }
    }

    private Optional<JRuleValue> averageSince(JRuleItem jRuleItem, ZonedDateTime time, String persistenceServiceId) {
        try {
            Method method = jRuleItem.getClass().getMethod("averageSince", ZonedDateTime.class, String.class);
            return (Optional<JRuleValue>) method.invoke(jRuleItem, time, persistenceServiceId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error executing averageSince", e);
        }
    }

    private Optional<JRuleValue> sumSince(JRuleItem jRuleItem, ZonedDateTime time, String persistenceServiceId) {
        try {
            Method method = jRuleItem.getClass().getMethod("sumSince", ZonedDateTime.class, String.class);
            return (Optional<JRuleValue>) method.invoke(jRuleItem, time, persistenceServiceId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error executing sumSince", e);
        }
    }
}
