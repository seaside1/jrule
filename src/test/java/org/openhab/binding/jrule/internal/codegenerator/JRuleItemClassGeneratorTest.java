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
package org.openhab.binding.jrule.internal.codegenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.compiler.JRuleCompiler;
import org.openhab.automation.jrule.items.JRuleItemClassGenerator;
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

/**
 * The {@link JRuleItemClassGeneratorTest}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 * @author Arne Seime - Added code generator and compilation tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleItemClassGeneratorTest {

    private JRuleItemClassGenerator sourceFileGenerator;
    private File targetFolder;
    private JRuleCompiler compiler;

    @BeforeAll
    public void setup() {
        targetFolder = new File("target/gen/org/openhab/automation/jrule/generated/items/");
        targetFolder.mkdirs();

        Map<String, Object> map = new HashMap<>();
        map.put("org.openhab.automation.jrule.directory", "target");
        JRuleConfig config = new JRuleConfig(map);
        sourceFileGenerator = new JRuleItemClassGenerator(config);
        compiler = new JRuleCompiler(config);
    }

    @BeforeEach
    public void wipeFiles() {
        // Wipe any existing files
        Arrays.stream(targetFolder.listFiles()).forEach(File::delete);
    }

    @Test
    public void testGenerateAndCompileGroupItem() {
        generateAndCompile(decorate(new GroupItem("ColorGroup", new ColorItem("ColorItem"))));
        generateAndCompile(decorate(new GroupItem("ContactGroup", new ContactItem("ContactItem"))));
        generateAndCompile(decorate(new GroupItem("DateTimeGroup", new DateTimeItem("DateTimeItem"))));
        generateAndCompile(decorate(new GroupItem("DimmerGroup", new DimmerItem("DimmerItem"))));
        generateAndCompile(decorate(new GroupItem("NumberGroup", new NumberItem("NumberItem"))));
        generateAndCompile(
                decorate(new GroupItem("QuantityGroup", new NumberItem("Number:Temperature", "QuantityItem"))));
        generateAndCompile(decorate(new GroupItem("PlayerGroup", new PlayerItem("PlayerItem"))));
        generateAndCompile(decorate(new GroupItem("RollershutterGroup", new RollershutterItem("RollershutterItem"))));
        generateAndCompile(decorate(new GroupItem("StringGroup", new StringItem("StringItem"))));
        generateAndCompile(decorate(new GroupItem("SwitchGroup", new SwitchItem("SwitchItem"))));
        generateAndCompile(decorate(new GroupItem("LocationGroup", new LocationItem("LocationItem"))));
        generateAndCompile(decorate(new GroupItem("CallGroup", new CallItem("CallItem"))));
        generateAndCompile(decorate(new GroupItem("ImageGroup", new ImageItem("ImageItem"))));
    }

    private Item decorate(GenericItem item) {
        item.setLabel(item.getName() + "Label");
        return item;
    }

    @Test
    public void testGenerateItemsFile() {
        List<Item> items = new ArrayList<>();

        ColorItem colorItem = new ColorItem("ColorItem");
        colorItem.setLabel("ColorLabel");
        items.add(colorItem);

        ContactItem contactItem = new ContactItem("ContactItem");
        contactItem.setLabel("ContactLabel");
        items.add(contactItem);

        DateTimeItem dateTimeItem = new DateTimeItem("DateTimeItem");
        dateTimeItem.setLabel("DateTimeLabel");
        items.add(dateTimeItem);

        DimmerItem dimmerItem = new DimmerItem("DimmerItem");
        dimmerItem.setLabel("DimmerLabel");
        items.add(dimmerItem);

        PlayerItem playerItem = new PlayerItem("PlayerItem");
        playerItem.setLabel("PlayerLabel");
        items.add(playerItem);

        SwitchItem switchItem = new SwitchItem("SwitchItem");
        switchItem.setLabel("SwitchLabel");
        items.add(switchItem);

        StringItem stringItem = new StringItem("StringItem");
        stringItem.setLabel("StringLabel");
        items.add(stringItem);

        NumberItem numberItem = new NumberItem("NumberItem");
        numberItem.setLabel("NumberLabel");
        items.add(numberItem);

        RollershutterItem rollershutterItem = new RollershutterItem("RollershutterItem");
        rollershutterItem.setLabel("RollershutterLabel");
        items.add(rollershutterItem);

        LocationItem locationItem = new LocationItem("LocationItem");
        locationItem.setLabel("LocationLabel");
        items.add(locationItem);

        CallItem callItem = new CallItem("CallItem");
        callItem.setLabel("CallLabel");
        items.add(callItem);

        ImageItem imageItem = new ImageItem("ImageItem");
        imageItem.setLabel("ImageLabel");
        items.add(imageItem);

        NumberItem quantityItem = new NumberItem("Number:Temperature", "QuantityItem");
        quantityItem.setLabel("QuantityLabel");
        items.add(quantityItem);

        GroupItem groupItem = new GroupItem("RollershutterGroup", new RollershutterItem("RollershutterItem"));
        groupItem.setLabel("RollerShutterGroupLabel");
        items.add(groupItem);

        boolean success = sourceFileGenerator.generateItemsSource(items);
        assertTrue(success, "Failed to generate source file for items");

        compiler.compile(List.of(new File(targetFolder, "JRuleItems.java")), "target/classes:target/gen");

        File compiledClass = new File(targetFolder, "JRuleItems.class");
        assertTrue(compiledClass.exists());
    }

    private void generateAndCompile(Item item) {
        // boolean success = sourceFileGenerator.generateItemSource(item);
        // assertTrue(success, "Failed to generate source file for " + item);
        //
        // compiler.compile(List.of(new File(targetFolder, "_" + item.getName() + ".java")), "target/classes");
        //
        // File compiledClass = new File(targetFolder, "_" + item.getName() + ".class");
        // assertTrue(compiledClass.exists());
    }
}
