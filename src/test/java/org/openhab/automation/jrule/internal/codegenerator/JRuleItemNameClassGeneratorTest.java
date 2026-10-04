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
package org.openhab.automation.jrule.internal.codegenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.compiler.JRuleCompiler;
import org.openhab.automation.jrule.items.JRuleItemDefinition;
import org.openhab.automation.jrule.items.JRuleItemNameClassGenerator;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.test_utils.JRuleItemTestUtils;
import org.openhab.core.items.*;
import org.openhab.core.library.items.*;
import org.openhab.core.library.types.*;
import org.openhab.core.types.State;

/**
 * The {@link JRuleItemNameClassGeneratorTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleItemNameClassGeneratorTest {

    private JRuleItemNameClassGenerator sourceFileGenerator;
    private File targetFolder;
    private JRuleCompiler compiler;

    @BeforeAll
    public void setup() {
        targetFolder = new File("target/gen/org/openhab/automation/jrule/generated/items/");
        targetFolder.mkdirs();

        Map<String, Object> map = new HashMap<>();
        map.put("org.openhab.automation.jrule.directory", "target");
        JRuleConfig config = new JRuleConfig(map);
        sourceFileGenerator = new JRuleItemNameClassGenerator(config);
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
        generateAndCompile(decorate(new GroupItem("QuantityGroup",
                new NumberItem("Number:Temperature", "QuantityItem", JRuleItemTestUtils.getI18nProvider()))));
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
    public void testGenerateItemsFile() throws Exception {
        List<Item> items = new ArrayList<>();

        GenericItem stringItem = createItem(StringItem.class, new StringType("abc"));
        stringItem.addGroupNames("StringItemGroup");
        items.add(stringItem);
        items.add(createItem(ColorItem.class, new HSBType(new DecimalType(1), new PercentType(2), new PercentType(3))));
        items.add(createItem(ContactItem.class, OpenClosedType.OPEN));
        items.add(createItem(DateTimeItem.class, new DateTimeType(ZonedDateTime.now())));
        items.add(createItem(DimmerItem.class, new PercentType(50)));
        items.add(createItem(PlayerItem.class, PlayPauseType.PAUSE));
        items.add(createItem(SwitchItem.class, OnOffType.OFF));
        GenericItem numberItem = createItem(NumberItem.class, new DecimalType(340));
        // deliberately out of order, the annotation has to list them sorted
        numberItem.addGroupNames("NumberItemGroup", "DimmerItemGroup");
        items.add(numberItem);
        items.add(createItem(RollershutterItem.class, new PercentType(22)));
        items.add(createItem(LocationItem.class, new PointType(new DecimalType(22.22), new DecimalType(54.12))));
        // items.add(createItem(CallItem.class, new StringType("+4930123456")));
        items.add(createItem(ImageItem.class, new RawType(new byte[0], "jpeg")));

        items.add(createGroupItem(StringItem.class, new StringType("abc")));
        items.add(createGroupItem(ColorItem.class,
                new HSBType(new DecimalType(1), new PercentType(2), new PercentType(3))));
        items.add(createGroupItem(ContactItem.class, OpenClosedType.OPEN));
        items.add(createGroupItem(DateTimeItem.class, new DateTimeType(ZonedDateTime.now())));
        items.add(createGroupItem(DimmerItem.class, new PercentType(50)));
        items.add(createGroupItem(PlayerItem.class, PlayPauseType.PAUSE));
        items.add(createGroupItem(SwitchItem.class, OnOffType.OFF));
        items.add(createGroupItem(NumberItem.class, new DecimalType(340)));
        items.add(createGroupItem(RollershutterItem.class, new PercentType(22)));
        items.add(createGroupItem(LocationItem.class, new PointType(new DecimalType(22.22), new DecimalType(54.12))));
        // items.add(createGroupItem(CallItem.class, new StringType("+4930123456")));
        items.add(createGroupItem(ImageItem.class, new RawType(new byte[0], "jpeg")));

        MetadataRegistry metadataRegistry = Mockito.mock(MetadataRegistry.class);
        JRuleItemRegistry.setMetadataRegistry(metadataRegistry);
        Mockito.when(metadataRegistry.stream()).thenAnswer(
                invocationOnMock -> Stream.of(new Metadata(new MetadataKey("Speech", "CallItemStringListType"),
                        "some data", Map.of("location", "Livingroom"))));

        boolean success = sourceFileGenerator.generateItemNamesSource(items, metadataRegistry);
        assertTrue(success, "Failed to generate source file for items");

        Assertions.assertTrue(
                compiler.compile(List.of(new File(targetFolder, "JRuleItemNames.java")), "target/classes:target/gen"),
                "Failed to compile generated source file for item names");
        assertTrue(new File(targetFolder, "JRuleItemNames.class").exists());

        try (URLClassLoader classLoader = new URLClassLoader(new URL[] { new File("target/gen").toURI().toURL() },
                JRuleItemNameClassGeneratorTest.class.getClassLoader())) {
            Class<?> aClass = classLoader.loadClass("org.openhab.automation.jrule.generated.items.JRuleItemNames");

            for (Item item : items) {
                assertItemDefinition(aClass, item);
            }
        }
    }

    private static void assertItemDefinition(Class<?> aClass, Item item)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = aClass.getDeclaredField(item.getName());
        Assertions.assertEquals(item.getName(), field.get(null), "field value");

        JRuleItemDefinition definition = field.getAnnotation(JRuleItemDefinition.class);
        Assertions.assertNotNull(definition, "no JRuleItemDefinition on " + item.getName());
        Assertions.assertEquals(item.getName(), definition.name(), "name of " + item.getName());
        Assertions.assertEquals(item.getType(), definition.type(), "type of " + item.getName());
        Assertions.assertEquals(item.getGroupNames().stream().sorted().toList(), List.of(definition.groupNames()),
                "groupNames of " + item.getName());
    }

    private GroupItem createGroupItem(Class<? extends GenericItem> clazz, State initialState)
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
