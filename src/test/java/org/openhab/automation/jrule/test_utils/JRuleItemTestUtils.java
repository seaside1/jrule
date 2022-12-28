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
package org.openhab.automation.jrule.test_utils;

import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.openhab.automation.jrule.items.*;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.*;
import org.openhab.core.library.types.*;
import org.openhab.core.types.State;

/**
 * The {@link JRuleItemTestUtils}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemTestUtils {
    @NotNull
    public static Map<Item, Class<? extends JRuleItem>> getAllDummyItems()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<Item, Class<? extends JRuleItem>> items = new HashMap<>();

        items.put(createItem(StringItem.class, new StringType("abc")), JRuleStringItem.class);
        items.put(createItem(ColorItem.class, new HSBType(new DecimalType(1), new PercentType(2), new PercentType(3))),
                JRuleColorItem.class);
        items.put(createItem(ContactItem.class, OpenClosedType.OPEN), JRuleContactItem.class);
        items.put(createItem(DateTimeItem.class, new DateTimeType(ZonedDateTime.now())), JRuleDateTimeItem.class);
        items.put(createItem(DimmerItem.class, new PercentType(50)), JRuleDimmerItem.class);
        items.put(createItem(PlayerItem.class, PlayPauseType.PAUSE), JRulePlayerItem.class);
        items.put(createItem(SwitchItem.class, OnOffType.OFF), JRuleSwitchItem.class);
        items.put(createItem(NumberItem.class, new DecimalType(340)), JRuleNumberItem.class);
        items.put(createItem(RollershutterItem.class, new PercentType(22)), JRuleRollershutterItem.class);
        items.put(createItem(LocationItem.class, new PointType(new DecimalType(22.22), new DecimalType(54.12))),
                JRuleLocationItem.class);
        items.put(createItem(CallItem.class, new StringListType(List.of("+4930123456"))), JRuleCallItem.class);
        items.put(createItem(ImageItem.class, new RawType(new byte[0], "jpeg")), JRuleImageItem.class);

        items.put(createGroupItem(StringItem.class, new StringType("abc")), JRuleStringGroupItem.class);
        items.put(
                createGroupItem(ColorItem.class,
                        new HSBType(new DecimalType(1), new PercentType(2), new PercentType(3))),
                JRuleColorGroupItem.class);
        items.put(createGroupItem(ContactItem.class, OpenClosedType.OPEN), JRuleContactGroupItem.class);
        items.put(createGroupItem(DateTimeItem.class, new DateTimeType(ZonedDateTime.now())), JRuleDateTimeItem.class);
        items.put(createGroupItem(DimmerItem.class, new PercentType(50)), JRuleDimmerGroupItem.class);
        items.put(createGroupItem(PlayerItem.class, PlayPauseType.PAUSE), JRulePlayerGroupItem.class);
        items.put(createGroupItem(SwitchItem.class, OnOffType.OFF), JRuleSwitchGroupItem.class);
        items.put(createGroupItem(NumberItem.class, new DecimalType(340)), JRuleNumberGroupItem.class);
        items.put(createGroupItem(RollershutterItem.class, new PercentType(22)), JRuleRollershutterGroupItem.class);
        items.put(createGroupItem(LocationItem.class, new PointType(new DecimalType(22.22), new DecimalType(54.12))),
                JRuleLocationGroupItem.class);
        items.put(createGroupItem(CallItem.class, new StringListType(List.of("+4930123456"))),
                JRuleCallGroupItem.class);
        items.put(createGroupItem(ImageItem.class, new RawType(new byte[0], "jpeg")), JRuleImageGroupItem.class);
        return items;
    }

    private static GroupItem createGroupItem(Class<? extends GenericItem> clazz, State initialState)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GenericItem baseItem = createItem(clazz, initialState);
        GroupItem groupItem = new GroupItem(clazz.getSimpleName() + "Group", baseItem);
        groupItem.setState(initialState);
        return groupItem;
    }

    private static GenericItem createItem(Class<? extends GenericItem> clazz, State initialState)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GenericItem item = clazz.getConstructor(String.class).newInstance(clazz.getSimpleName());
        item.setLabel(clazz.getSimpleName() + "Label");
        item.setState(initialState);
        return item;
    }
}
