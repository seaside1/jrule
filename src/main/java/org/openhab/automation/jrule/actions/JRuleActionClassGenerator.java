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
package org.openhab.automation.jrule.actions;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.generator.JRuleAbstractClassGenerator;
import org.openhab.core.automation.annotation.ActionInput;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingActionsScope;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;

/**
 * The {@link JRuleActionClassGenerator} Class Generator for actions
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleActionClassGenerator extends JRuleAbstractClassGenerator {

    private static final String TEMPLATE_SUFFIX = ".ftlh";

    protected static final String LOG_NAME_CLASS_GENERATOR = "JRuleActionClassGen";

    private final Logger logger = LoggerFactory.getLogger(JRuleActionClassGenerator.class);

    public JRuleActionClassGenerator(JRuleConfig jRuleConfig) {
        super(jRuleConfig);
    }

    public boolean generateActionSource(Thing thing) {
        try {
            Map<String, Object> processingModel = new HashMap<>();

            Map<String, Object> actionModel = createActionModel(thing);
            processingModel.put("action", actionModel);

            File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getActionsDirectory())
                    .append(File.separator).append(jRuleConfig.getGeneratedItemPrefix())
                    .append(getActionFriendlyName(thing.getUID().toString())).append(JRuleConstants.JAVA_FILE_TYPE)
                    .toString());

            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration
                        .getTemplate("actions/" + actionModel.get("templateName") + TEMPLATE_SUFFIX);
                template.process(processingModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for action {}: {}", thing.getUID().toString(),
                    ExceptionUtils.getStackTrace(e));

        }

        return false;
    }

    public boolean generateActionsSource(Collection<Thing> things) {
        try {
            List<Map<String, Object>> model = things.stream()
                    .sorted(Comparator.comparing(e -> getActionFriendlyName(e.getUID().toString())))
                    .map(this::createActionsModel).collect(Collectors.toList());
            Map<String, Object> processingModel = new HashMap<>();
            processingModel.put("actions", model);
            processingModel.put("packageName", jRuleConfig.getGeneratedActionPackage());

            File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getActionsDirectory())
                    .append(File.separator).append("JRuleActions.java").toString());

            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration.getTemplate("actions/Actions" + TEMPLATE_SUFFIX);
                template.process(processingModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for JRuleActions.java: {}",
                    ExceptionUtils.getStackTrace(e));

        }
        return false;
    }

    String getActionsScope(Thing thing) {
        if (thing.getHandler() != null) {
            // Check if the ThingHandlerService has a ThingActionsScope annotation
            Class<? extends ThingHandlerService> thingActionsClass = thing.getHandler().getServices().stream()
                    .filter(ThingActions.class::isAssignableFrom).findFirst()
                    .orElseThrow(() -> new IllegalStateException("should not occur here"));
            if (thingActionsClass.getAnnotation(ThingActionsScope.class) != null) {
                return thingActionsClass.getAnnotation(ThingActionsScope.class).name();
            }
        }
        // Else default to the binding id
        return thing.getUID().getBindingId();
    }

    private Map<String, Object> createActionsModel(Thing thing) {
        Map<String, Object> freemarkerModel = new HashMap<>();
        freemarkerModel.put("id", thing.getUID().toString());
        freemarkerModel.put("scope", getActionsScope(thing));
        freemarkerModel.put("name", StringUtils.uncapitalize(getActionFriendlyName(thing.getUID().toString())));
        freemarkerModel.put("package", jRuleConfig.getGeneratedActionPackage());
        freemarkerModel.put("class",
                jRuleConfig.getGeneratedItemPrefix() + getActionFriendlyName(thing.getUID().toString()));
        freemarkerModel.put("label", thing.getLabel());
        freemarkerModel.put("templateName", "Actions");
        freemarkerModel.put("parentClass", "JRuleAbstractAction");
        return freemarkerModel;
    }

    private Map<String, Object> createActionModel(Thing thing) {
        Map<String, Object> freemarkerModel = new HashMap<>();
        freemarkerModel.put("id", thing.getUID().toString());
        freemarkerModel.put("name", getActionFriendlyName(thing.getUID().toString()));
        freemarkerModel.put("package", jRuleConfig.getGeneratedActionPackage());
        freemarkerModel.put("class",
                jRuleConfig.getGeneratedItemPrefix() + getActionFriendlyName(thing.getUID().toString()));
        freemarkerModel.put("label", thing.getLabel());
        freemarkerModel.put("templateName", "Action");
        freemarkerModel.put("parentClass", "JRuleAbstractAction");

        List<Object> methodList = new ArrayList<>();
        freemarkerModel.put("methods", methodList);

        if (thing.getHandler() != null) {
            Class<? extends ThingHandlerService> thingActionsClass = thing.getHandler().getServices().stream()
                    .filter(ThingActions.class::isAssignableFrom).findFirst()
                    .orElseThrow(() -> new IllegalStateException("should not occur here"));

            freemarkerModel.put("type", thingActionsClass.getTypeName());
            Set<String> imports = new TreeSet<>();
            freemarkerModel.put("imports", imports);

            Arrays.stream(thingActionsClass.getDeclaredMethods())
                    .filter(method -> method.getAnnotation(RuleAction.class) != null).collect(Collectors.toSet())

                    .forEach(method -> {
                        Map<Object, Object> methodMap = new HashMap<>();
                        methodMap.put("name", method.getName());

                        Class<?> returnType = replaceTypeIfNecessary(method.getReturnType());

                        methodMap.put("returnType", returnType.getTypeName());
                        methodMap.put("import", !returnType.isPrimitive());
                        methodMap.put("hasReturnType", !returnType.getTypeName().equalsIgnoreCase("void"));
                        if (!returnType.isPrimitive() && !returnType.getTypeName().equalsIgnoreCase("void")) {
                            imports.add(returnType.getTypeName());
                        }

                        List<Object> args = new ArrayList<>();
                        methodMap.put("args", args);
                        Arrays.stream(method.getParameters()).forEach(parameter -> {
                            if (parameter.getAnnotation(ActionInput.class) != null) {
                                Map<Object, Object> arg = new HashMap<>();
                                Class<?> parameterType = replaceTypeIfNecessary(parameter.getType());
                                arg.put("type", parameterType.getTypeName());
                                arg.put("reflectionType", ClassUtils.primitiveToWrapper(parameter.getType())
                                        .getTypeName().replaceFirst("java.lang.", ""));
                                arg.put("name", parameter.getAnnotation(ActionInput.class).name());
                                args.add(arg);
                            }
                        });
                        methodList.add(methodMap);
                    });
        }
        return freemarkerModel;
    }

    private Class<?> replaceTypeIfNecessary(Class<?> type) {
        if (type.isPrimitive() || "org.openhab.core.library.types".equals(type.getPackageName())
                || "org.openhab.core.types".equals(type.getPackageName())
                || type.getPackageName().startsWith("java.")) {
            return type;
        } else {
            return Object.class;
        }
    }

    public static String getActionFriendlyName(String thingUid) {
        return Arrays.stream(thingUid.split("[:\\-]")).map(StringUtils::capitalize).collect(Collectors.joining(""));
    }
}
