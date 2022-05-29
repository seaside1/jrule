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
package org.openhab.binding.jrule.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.DateTimeItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.LocationItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.PlayerItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.vesalainen.util.Lists;

/**
 * The {@link JRuleClassGeneratorTest}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 * @author Arne Seime - Added code generator and compilation tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleClassGeneratorTest {

    private JRuleItemClassGenerator sourceFileGenerator;
    private File targetFolder;
    private JRuleCompiler compiler;

    @BeforeAll
    public void setup() {
        targetFolder = new File("target/items/org/openhab/automation/jrule/items/generated/");
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
        Arrays.stream(targetFolder.listFiles()).forEach(e -> e.delete());
    }

    @Test
    public void testGenerateAndCompile() {
        generateAndCompile(decorate(new ColorItem("ColorItem")));
        generateAndCompile(decorate(new ContactItem("ContactItem")));
        generateAndCompile(decorate(new DateTimeItem("DateTimeItem")));
        generateAndCompile(decorate(new DimmerItem("DimmerItem")));
        generateAndCompile(decorate(new GroupItem("GroupItem")));
        generateAndCompile(decorate(new NumberItem("NumberItem")));
        generateAndCompile(decorate(new NumberItem("Number:Temperature", "QuantityItem")));
        generateAndCompile(decorate(new PlayerItem("PlayerItem")));
        generateAndCompile(decorate(new RollershutterItem("RollershutterItem")));
        generateAndCompile(decorate(new StringItem("StringItem")));
        generateAndCompile(decorate(new SwitchItem("SwitchItem")));
        generateAndCompile(decorate(new LocationItem("LocationItem")));
    }

    private Item decorate(GenericItem item) {
        item.setLabel(item.getName() + "Label");
        return item;
    }

    @Test
    public void testGenerateItemsFile() {

        SwitchItem switchItem = new SwitchItem("SwitchItem");
        switchItem.setLabel("SwitchLabel");
        generateAndCompile(switchItem);

        StringItem stringItem = new StringItem("StringItem");
        stringItem.setLabel("StringLabel");
        generateAndCompile(stringItem);

        NumberItem numberItem = new NumberItem("NumberItem");
        numberItem.setLabel("NumberLabel");
        generateAndCompile(numberItem);

        NumberItem quantityItem = new NumberItem("Number:Temperature", "NumberItem");
        quantityItem.setLabel("QuantityLabel");
        generateAndCompile(quantityItem);

        List<Item> items = Lists.create(switchItem, stringItem);

        boolean success = sourceFileGenerator.generateItemsSource(items);
        assertTrue(success, "Failed to generate source file for items");

        compiler.compile(new File(targetFolder, "Items.java"), "target/classes:target/items");

        File compiledClass = new File(targetFolder, "Items.class");
        assertTrue(compiledClass.exists());
    }

    private void generateAndCompile(Item item) {
        boolean success = sourceFileGenerator.generateItemSource(item);
        assertTrue(success, "Failed to generate source file for " + item);

        compiler.compile(new File(targetFolder, "_" + item.getName() + ".java"), "target/classes");

        File compiledClass = new File(targetFolder, "_" + item.getName() + ".class");
        assertTrue(compiledClass.exists());
    }
}
