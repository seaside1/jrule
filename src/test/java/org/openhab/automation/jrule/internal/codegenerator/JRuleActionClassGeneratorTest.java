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

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.openhab.automation.jrule.actions.JRuleActionClassGenerator;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.compiler.JRuleCompiler;
import org.openhab.core.audio.AudioManager;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.model.script.engine.action.ActionService;
import org.openhab.core.model.script.internal.engine.action.AudioActionService;
import org.openhab.core.model.script.internal.engine.action.SemanticsActionService;

/**
 * The {@link JRuleActionClassGeneratorTest}
 *
 *
 * @author Robert Delbrück - Initial contribution
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JRuleActionClassGeneratorTest {

    private JRuleActionClassGenerator sourceFileGenerator;
    private File targetFolder;
    private JRuleCompiler compiler;

    @BeforeAll
    public void setup() {
        targetFolder = new File("target/gen/org/openhab/automation/jrule/generated/actions/");
        targetFolder.mkdirs();

        Map<String, Object> map = new HashMap<>();
        map.put("org.openhab.automation.jrule.directory", "target");
        JRuleConfig config = new JRuleConfig(map);
        sourceFileGenerator = new JRuleActionClassGenerator(config);
        compiler = new JRuleCompiler(config);
    }

    @BeforeEach
    public void wipeFiles() {
        // Wipe any existing files
        Arrays.stream(targetFolder.listFiles()).forEach(File::delete);
    }

    @Test
    public void testGenerateAndCompileActionFile() {
        ActionService actionService = new SemanticsActionService(Mockito.mock(ItemRegistry.class));

        generateAndCompile(actionService);
    }

    @Test
    public void testGenerateActionsFile() throws ClassNotFoundException, IOException, NoSuchFieldException,
            NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        ActionService actionService = new SemanticsActionService(Mockito.mock(ItemRegistry.class));
        generateAndCompile(actionService);

        AudioManager mock = Mockito.mock(AudioManager.class);
        Mockito.when(mock.getVolume(Mockito.any())).thenReturn(PercentType.HUNDRED);
        ActionService actionService2 = new AudioActionService(mock);
        generateAndCompile(actionService2);

        List<ActionService> actionServices = List.of(actionService, actionService2);

        boolean success = sourceFileGenerator.generateActionsSource(actionServices);
        assertTrue(success, "Failed to generate source file for actions");

        compiler.compile(List.of(new File(targetFolder, "JRuleActions.java")),
                "target/classes" + File.pathSeparator + "target/gen");

        File compiledClass = new File(targetFolder, "JRuleActions.class");
        assertTrue(compiledClass.exists());

        URLClassLoader classLoader = new URLClassLoader(new URL[] { new File("target/gen").toURI().toURL() },
                JRuleActionClassGeneratorTest.class.getClassLoader());
        final String className = "org.openhab.automation.jrule.generated.actions.JRuleActions";
        // compiler.loadClass(classLoader, className, true);
        Class<?> aClass = classLoader.loadClass(className);
        Object jRuleActions = aClass.getConstructor().newInstance();
        Field audioActionField = aClass.getDeclaredField("audio");
        Object action = audioActionField.get(jRuleActions);

        // Verify methods exists
        Method getMasterVolume = action.getClass().getDeclaredMethod("getMasterVolume");
        Object res = getMasterVolume.invoke(action);
        assertEquals(1F, res);

        // Verify methods exists
        Method playSound = action.getClass().getDeclaredMethod("playSound", String.class, String.class,
                PercentType.class);
        playSound.invoke(action, "param1", "param2", PercentType.ZERO);
    }

    private void generateAndCompile(ActionService actionService) {
        boolean success = sourceFileGenerator.generateActionSource(actionService);
        assertTrue(success, "Failed to generate source file for " + actionService);

        compiler.compile(
                List.of(new File(targetFolder, "_" + actionService.getActionClass().getSimpleName() + ".java")),
                "target/classes");

        File compiledClass = new File(targetFolder, "_" + actionService.getActionClass().getSimpleName() + ".class");
        assertTrue(compiledClass.exists());
    }

    public static String getActionFriendlyName(Class<?> actionClass) {
        return StringUtils.uncapitalize(actionClass.getSimpleName());
    }
}
