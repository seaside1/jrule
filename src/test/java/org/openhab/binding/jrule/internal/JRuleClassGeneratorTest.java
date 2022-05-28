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
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.compiler.JRuleCompiler;
import org.openhab.automation.jrule.items.JRuleItemClassGenerator;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.DateTimeItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.PlayerItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;

/**
 * The {@link JRuleClassGeneratorTest}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
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
        generateAndCompile(new ColorItem("ColorItem"));
        generateAndCompile(new ContactItem("ContactItem"));
        generateAndCompile(new DateTimeItem("DateTimeItem"));
        generateAndCompile(new DimmerItem("DimmerItem"));
        generateAndCompile(new GroupItem("GroupItem"));
        generateAndCompile(new NumberItem("NumberItem"));
        generateAndCompile(new NumberItem("Number:Temperature", "QuantityItem"));
        generateAndCompile(new PlayerItem("PlayerItem"));
        generateAndCompile(new RollershutterItem("RollershutterItem"));
        generateAndCompile(new StringItem("StringItem"));
        generateAndCompile(new SwitchItem("SwitchItem"));
    }

    private void generateAndCompile(Item item) {
        boolean success = sourceFileGenerator.generateItemSource(item);
        assertTrue(success, "Failed to generate source file for " + item);

        compiler.compile(new File(targetFolder, "_" + item.getName() + ".java"), "target/classes");

        File compiledClass = new File(targetFolder, "_" + item.getName() + ".class");
        assertTrue(compiledClass.exists());
    }
}
