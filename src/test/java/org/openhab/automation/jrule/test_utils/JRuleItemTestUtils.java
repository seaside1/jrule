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
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.openhab.automation.jrule.items.JRuleColorGroupItem;
import org.openhab.automation.jrule.items.JRuleColorItem;
import org.openhab.automation.jrule.items.JRuleContactGroupItem;
import org.openhab.automation.jrule.items.JRuleContactItem;
import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleDimmerGroupItem;
import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.items.JRuleImageGroupItem;
import org.openhab.automation.jrule.items.JRuleImageItem;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.JRuleLocationGroupItem;
import org.openhab.automation.jrule.items.JRuleLocationItem;
import org.openhab.automation.jrule.items.JRuleNumberGroupItem;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRulePlayerGroupItem;
import org.openhab.automation.jrule.items.JRulePlayerItem;
import org.openhab.automation.jrule.items.JRuleRollershutterGroupItem;
import org.openhab.automation.jrule.items.JRuleRollershutterItem;
import org.openhab.automation.jrule.items.JRuleStringGroupItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchGroupItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.DateTimeItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.ImageItem;
import org.openhab.core.library.items.LocationItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.PlayerItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.PlayPauseType;
import org.openhab.core.library.types.PointType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;

/**
 * The {@link JRuleItemTestUtils}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemTestUtils {
    @NotNull
    public static Map<Item, Class<? extends JRuleItem<?>>> getAllDummyItems()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<Item, Class<? extends JRuleItem<?>>> items = new HashMap<>();

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
        // items.add(createItem(CallItem.class, new StringType("+4930123456")));
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
        // items.add(createGroupItem(CallItem.class, new StringType("+4930123456")));
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
