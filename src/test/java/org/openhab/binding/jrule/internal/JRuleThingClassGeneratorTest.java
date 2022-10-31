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
import org.openhab.automation.jrule.things.JRuleThingClassGenerator;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.internal.BridgeImpl;
import org.openhab.core.thing.internal.ThingImpl;
import org.openhab.core.thing.type.ChannelKind;
import org.vesalainen.util.Lists;

/**
 * The {@link JRuleThingClassGeneratorTest}
 *
 * 
 * @author Arne Seime - Initial contribution
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleThingClassGeneratorTest {

    private JRuleThingClassGenerator sourceFileGenerator;
    private File targetFolder;
    private JRuleCompiler compiler;

    @BeforeAll
    public void setup() {
        targetFolder = new File("target/gen/org/openhab/automation/jrule/generated/things/");
        targetFolder.mkdirs();

        Map<String, Object> map = new HashMap<>();
        map.put("org.openhab.automation.jrule.directory", "target");
        JRuleConfig config = new JRuleConfig(map);
        sourceFileGenerator = new JRuleThingClassGenerator(config);
        compiler = new JRuleCompiler(config);
    }

    @BeforeEach
    public void wipeFiles() {
        // Wipe any existing files
        Arrays.stream(targetFolder.listFiles()).forEach(File::delete);
    }

    @Test
    public void testGenerateAndCompileThingFile() {
        ThingImpl thing = new ThingImpl(new ThingTypeUID("mybinding", "thingtype"),
                new ThingUID("mybinding", "thingtype", "id"));
        Channel triggerChannel = ChannelBuilder.create(new ChannelUID(thing.getUID(), "triggerChannel"))
                .withKind(ChannelKind.TRIGGER).build();
        Channel triggerChannelWithType = ChannelBuilder.create(new ChannelUID(thing.getUID(), "triggerChannel#start"))
                .withKind(ChannelKind.TRIGGER).build();
        Channel stateChannel = ChannelBuilder.create(new ChannelUID(thing.getUID(), "stateChannel"))
                .withKind(ChannelKind.STATE).build();
        thing.addChannel(triggerChannel);
        thing.addChannel(triggerChannelWithType);
        thing.addChannel(stateChannel);

        generateAndCompile(thing);
    }

    @Test
    public void testGenerateThingsFile() {

        BridgeImpl bridgeThing = new BridgeImpl(new ThingTypeUID("mybinding", "bridgetype"),
                new ThingUID("mybinding", "bridgetype", "id"));

        Thing subThing1 = new ThingImpl(new ThingTypeUID("mybinding", "thingtype"),
                new ThingUID("mybinding", "thingtype", "id1"));
        subThing1.setBridgeUID(bridgeThing.getBridgeUID());

        generateAndCompile(subThing1);
        Thing subThing2 = new ThingImpl(new ThingTypeUID("mybinding", "thingtype"),
                new ThingUID("mybinding", "thingtype", "id2"));
        subThing2.setBridgeUID(bridgeThing.getBridgeUID());
        generateAndCompile(subThing2);

        bridgeThing.addThing(subThing1);
        bridgeThing.addThing(subThing2);
        generateAndCompile(bridgeThing);

        Thing standaloneThing = new ThingImpl(new ThingTypeUID("mybinding", "thingtype"),
                new ThingUID("mybinding", "thingtype", "id3"));
        generateAndCompile(standaloneThing);

        List<Thing> things = Lists.create(bridgeThing, subThing1, subThing2, standaloneThing);

        boolean success = sourceFileGenerator.generateThingsSource(things);
        assertTrue(success, "Failed to generate source file for things");

        compiler.compile(List.of(new File(targetFolder, "JRuleThings.java")), "target/classes:target/gen");

        File compiledClass = new File(targetFolder, "JRuleThings.class");
        assertTrue(compiledClass.exists());
    }

    private void generateAndCompile(Thing thing) {
        boolean success = sourceFileGenerator.generateThingSource(thing);
        assertTrue(success, "Failed to generate source file for " + thing);

        compiler.compile(List.of(new File(targetFolder, "_" + thing.getUID().toString().replace(':', '_') + ".java")),
                "target/classes");

        File compiledClass = new File(targetFolder, "_" + thing.getUID().toString().replace(':', '_') + ".class");
        assertTrue(compiledClass.exists());
    }
}
