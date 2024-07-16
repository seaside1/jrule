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
import java.util.*;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openhab.automation.jrule.actions.JRuleModuleActionClassGenerator;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.compiler.JRuleCompiler;
import org.openhab.core.automation.Visibility;
import org.openhab.core.automation.type.ActionType;
import org.openhab.core.automation.type.Input;
import org.openhab.core.automation.type.Output;
import org.openhab.core.config.core.ConfigDescriptionParameter;
import org.openhab.core.config.core.ConfigDescriptionParameterBuilder;

/**
 * The {@link JRuleModuleActionClassGeneratorTest}
 *
 *
 * @author Robert Delbrück - Initial contribution
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleModuleActionClassGeneratorTest {

    private JRuleModuleActionClassGenerator sourceFileGenerator;
    private File targetFolder;
    private JRuleCompiler compiler;

    @BeforeAll
    public void setup() {
        targetFolder = new File("target/gen/org/openhab/automation/jrule/generated/moduleactions/");
        targetFolder.mkdirs();

        Map<String, Object> map = new HashMap<>();
        map.put("org.openhab.automation.jrule.directory", "target");
        JRuleConfig config = new JRuleConfig(map);
        sourceFileGenerator = new JRuleModuleActionClassGenerator(config, null);
        compiler = new JRuleCompiler(config);
    }

    @BeforeEach
    public void wipeFiles() {
        // Wipe any existing files
        Arrays.stream(targetFolder.listFiles()).forEach(File::delete);
    }

    @Test
    public void testGenerateAndCompileActionFile() {

        @Nullable
        List<ConfigDescriptionParameter> configDescriptions = new ArrayList<>();
        configDescriptions.add(ConfigDescriptionParameterBuilder.create("param1", ConfigDescriptionParameter.Type.TEXT)
                .withDescription("Description").withLabel("Label").build());

        @Nullable
        Set<String> tags = Set.of("TAG1", "TAG2");
        @Nullable
        List<Input> inputs = new ArrayList<>();
        @Nullable
        List<Output> outputs = new ArrayList<>();
        ActionType actionType = new ActionType("script.ScriptAction", configDescriptions, "Label", "Description", tags,
                Visibility.VISIBLE, inputs, outputs);

        generateAndCompile(actionType);
    }

    private void generateAndCompile(ActionType action) {
        boolean success = sourceFileGenerator.generateActionSource(List.of(action));
        assertTrue(success, "Failed to generate source file for " + action);

        compiler.compile(List.of(new File(targetFolder, "JRuleModuleActions.java")), "target/classes");

        File compiledClass = new File(targetFolder, "JRuleModuleActions.class");
        assertTrue(compiledClass.exists());
    }
}
