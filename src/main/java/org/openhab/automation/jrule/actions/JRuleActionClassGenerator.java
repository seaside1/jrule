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
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.generator.JRuleAbstractClassGenerator;
import org.openhab.core.model.script.engine.action.ActionDoc;
import org.openhab.core.model.script.engine.action.ActionService;
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

    public boolean generateActionSource(ActionService actionService) {
        try {
            Map<String, Object> processingModel = new HashMap<>();

            Map<String, Object> actionModel = createActionModel(actionService);
            processingModel.put("action", actionModel);

            File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getActionsDirectory())
                    .append(File.separator).append(jRuleConfig.getGeneratedItemPrefix())
                    .append(actionService.getActionClass().getSimpleName()).append(JRuleConstants.JAVA_FILE_TYPE)
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
                    "Internal error when generating java source for action {}: {}", actionService.getActionClass(),
                    ExceptionUtils.getStackTrace(e));

        }

        return false;
    }

    public boolean generateActionsSource(Collection<ActionService> actionServices) {
        try {
            List<Map<String, Object>> model = actionServices.stream()
                    .sorted(Comparator.comparing(e -> e.getActionClass().getSimpleName())).map(this::createActionsModel)
                    .collect(Collectors.toList());
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

    private Map<String, Object> createActionsModel(ActionService actionService) {
        Map<String, Object> freemarkerModel = new HashMap<>();
        freemarkerModel.put("name",
                StringUtils.uncapitalize(StringUtils.uncapitalize(actionService.getActionClass().getSimpleName())));
        freemarkerModel.put("package", jRuleConfig.getGeneratedActionPackage());
        freemarkerModel.put("class",
                jRuleConfig.getGeneratedItemPrefix() + actionService.getActionClass().getSimpleName());
        freemarkerModel.put("actionClass", actionService.getActionClass().getSimpleName());
        freemarkerModel.put("label", actionService.getActionClass());
        freemarkerModel.put("templateName", "Actions");
        return freemarkerModel;
    }

    private Map<String, Object> createActionModel(ActionService actionService) {
        Map<String, Object> freemarkerModel = new HashMap<>();
        freemarkerModel.put("id", actionService.getActionClass());
        freemarkerModel.put("name", actionService.getActionClass().getSimpleName());
        freemarkerModel.put("package", jRuleConfig.getGeneratedActionPackage());
        freemarkerModel.put("class",
                jRuleConfig.getGeneratedItemPrefix() + actionService.getActionClass().getSimpleName());
        freemarkerModel.put("actionClass", actionService.getActionClass().getSimpleName());
        freemarkerModel.put("actionClassFqn", actionService.getActionClass().getName());
        freemarkerModel.put("label", actionService.getActionClass());
        freemarkerModel.put("templateName", "Action");

        List<Object> methodList = new ArrayList<>();
        freemarkerModel.put("methods", methodList);

        Class<?> actionsClass = actionService.getActionClass();

        freemarkerModel.put("type", actionsClass.getTypeName());
        Set<String> imports = new TreeSet<>();
        // imports.add(actionService.getActionClass().getName());
        freemarkerModel.put("imports", imports);

        Arrays.stream(actionsClass.getDeclaredMethods()).filter(method -> method.getAnnotation(ActionDoc.class) != null)
                .collect(Collectors.toSet())

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
                        Map<Object, Object> arg = new HashMap<>();
                        Class<?> parameterType = replaceTypeIfNecessary(parameter.getType());
                        arg.put("type", parameterType.getTypeName());
                        arg.put("reflectionType", ClassUtils.primitiveToWrapper(parameter.getType()).getTypeName()
                                .replaceFirst("java.lang.", ""));
                        arg.put("name", parameter.getName());
                        args.add(arg);
                    });
                    methodList.add(methodMap);
                });
        return freemarkerModel;
    }

    private Class<?> replaceTypeIfNecessary(Class<?> type) {
        if (type.isPrimitive() || "org.openhab.core.library.types".equals(type.getPackageName())
                || "org.openhab.core.items".equals(type.getPackageName())
                || "org.openhab.core.types".equals(type.getPackageName())
                || type.getPackageName().startsWith("java.")) {
            return type;
        } else {
            return Object.class;
        }
    }
}
