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
package org.openhab.automation.jrule.test_utils;

import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.openhab.automation.jrule.items.JRuleCallGroupItem;
import org.openhab.automation.jrule.items.JRuleCallItem;
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
import org.openhab.automation.jrule.items.JRuleQuantityGroupItem;
import org.openhab.automation.jrule.items.JRuleQuantityItem;
import org.openhab.automation.jrule.items.JRuleRollershutterGroupItem;
import org.openhab.automation.jrule.items.JRuleRollershutterItem;
import org.openhab.automation.jrule.items.JRuleStringGroupItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchGroupItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.items.JRuleUnspecifiedGroupItem;
import org.openhab.core.i18n.UnitProvider;
import org.openhab.core.internal.i18n.I18nProviderImpl;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.CallItem;
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
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.library.types.StringListType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.types.State;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * The {@link JRuleItemTestUtils}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemTestUtils {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static List<Pair<? extends Item, Class<? extends JRuleItem>>> getAllDummyItems()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Pair<? extends Item, Class<? extends JRuleItem>>> items = new ArrayList<>();

        items.add(Pair.of(createItem(StringItem.class, new StringType("abc")), JRuleStringItem.class));
        items.add(Pair.of(
                createItem(ColorItem.class, new HSBType(new DecimalType(1), new PercentType(2), new PercentType(3))),
                JRuleColorItem.class));
        items.add(Pair.of(createItem(ContactItem.class, OpenClosedType.OPEN), JRuleContactItem.class));
        items.add(Pair.of(createItem(DateTimeItem.class, new DateTimeType(ZonedDateTime.now())),
                JRuleDateTimeItem.class));
        items.add(Pair.of(createItem(DimmerItem.class, new PercentType(50)), JRuleDimmerItem.class));
        items.add(Pair.of(createItem(PlayerItem.class, PlayPauseType.PAUSE), JRulePlayerItem.class));
        items.add(Pair.of(createItem(SwitchItem.class, OnOffType.OFF), JRuleSwitchItem.class));
        items.add(Pair.of(createItem(NumberItem.class, new DecimalType(340)), JRuleNumberItem.class));
        items.add(Pair.of(createItem(NumberItem.class, new QuantityType<>(340, Units.BAR)), JRuleQuantityItem.class));
        items.add(Pair.of(createItem(RollershutterItem.class, new PercentType(22)), JRuleRollershutterItem.class));
        items.add(Pair.of(createItem(LocationItem.class, new PointType(new DecimalType(22.22), new DecimalType(54.12))),
                JRuleLocationItem.class));
        items.add(Pair.of(createItem(CallItem.class, new StringListType(List.of("+4930123456"))), JRuleCallItem.class));
        items.add(Pair.of(createItem(ImageItem.class, new RawType(new byte[0], "jpeg")), JRuleImageItem.class));

        items.add(Pair.of(createGroupItem(StringItem.class, new StringType("abc")), JRuleStringGroupItem.class));
        items.add(Pair.of(
                createGroupItem(ColorItem.class,
                        new HSBType(new DecimalType(1), new PercentType(2), new PercentType(3))),
                JRuleColorGroupItem.class));
        items.add(Pair.of(createGroupItem(ContactItem.class, OpenClosedType.OPEN), JRuleContactGroupItem.class));
        items.add(Pair.of(createGroupItem(DateTimeItem.class, new DateTimeType(ZonedDateTime.now())),
                JRuleDateTimeItem.class));
        items.add(Pair.of(createGroupItem(DimmerItem.class, new PercentType(50)), JRuleDimmerGroupItem.class));
        items.add(Pair.of(createGroupItem(PlayerItem.class, PlayPauseType.PAUSE), JRulePlayerGroupItem.class));
        items.add(Pair.of(createGroupItem(SwitchItem.class, OnOffType.OFF), JRuleSwitchGroupItem.class));
        items.add(Pair.of(createGroupItem(NumberItem.class, new DecimalType(340)), JRuleNumberGroupItem.class));
        items.add(Pair.of(createGroupItem(NumberItem.class, new QuantityType<>(340, Units.BAR)),
                JRuleQuantityGroupItem.class));

        GroupItem groupItemNoBaseWithMember = createGroupItem(null, new QuantityType<>(340, Units.BAR));
        groupItemNoBaseWithMember.addMember(createItem(NumberItem.class, new QuantityType<>(340, Units.BAR)));
        items.add(Pair.of(groupItemNoBaseWithMember, JRuleUnspecifiedGroupItem.class));

        items.add(Pair.of(createGroupItem(RollershutterItem.class, new PercentType(22)),
                JRuleRollershutterGroupItem.class));
        items.add(Pair.of(
                createGroupItem(LocationItem.class, new PointType(new DecimalType(22.22), new DecimalType(54.12))),
                JRuleLocationGroupItem.class));
        items.add(Pair.of(createGroupItem(CallItem.class, new StringListType(List.of("+4930123456"))),
                JRuleCallGroupItem.class));
        items.add(
                Pair.of(createGroupItem(ImageItem.class, new RawType(new byte[0], "jpeg")), JRuleImageGroupItem.class));
        items.add(Pair.of(createGroupItem(null, null), JRuleUnspecifiedGroupItem.class));
        return items;
    }

    private static GroupItem createGroupItem(Class<? extends GenericItem> clazz, State initialState)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GenericItem baseItem = clazz != null ? createItem(clazz, initialState) : null;
        String name;
        if (initialState instanceof QuantityType) {
            name = "Quantity";
        } else {
            name = (clazz != null ? clazz.getSimpleName() : "");
        }
        GroupItem groupItem = new GroupItem(name + "Group" + counter.incrementAndGet(), baseItem);
        if (baseItem != null) {
            groupItem.addMember(baseItem);
        }
        groupItem.setState(initialState);
        return groupItem;
    }

    private static GenericItem createItem(Class<? extends GenericItem> clazz, State initialState)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GenericItem item;
        if (initialState instanceof QuantityType) {
            I18nProviderImpl i18nProvider = getI18nProvider();
            item = clazz.getConstructor(String.class, String.class, UnitProvider.class).newInstance("Number:Pressure",
                    clazz.getSimpleName() + initialState.getClass().getSimpleName(), i18nProvider);
        } else {
            item = clazz.getConstructor(String.class)
                    .newInstance(clazz.getSimpleName() + initialState.getClass().getSimpleName());
        }
        item.setLabel(clazz.getSimpleName() + "Label");
        item.setState(initialState);
        item.addTag("Tag1");
        item.addTag("Tag2");
        return item;
    }

    @NotNull
    public static I18nProviderImpl getI18nProvider() {
        ComponentContext componentContext = Mockito.mock(ComponentContext.class);
        Mockito.when(componentContext.getProperties()).thenReturn(new Hashtable<>());
        Mockito.when(componentContext.getBundleContext()).thenReturn(Mockito.mock(BundleContext.class));
        return new I18nProviderImpl(componentContext);
    }
}
