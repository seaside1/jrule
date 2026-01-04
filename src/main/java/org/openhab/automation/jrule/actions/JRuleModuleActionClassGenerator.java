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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.generator.JRuleAbstractClassGenerator;
import org.openhab.core.automation.type.ActionType;
import org.openhab.core.automation.type.ModuleTypeRegistry;
import org.openhab.core.config.core.ConfigDescriptionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;

/**
 * The {@link JRuleModuleActionClassGenerator} Class Generator for Module actions
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleModuleActionClassGenerator extends JRuleAbstractClassGenerator {

    private static final String TEMPLATE_SUFFIX = ".ftlh";

    protected static final String LOG_NAME_CLASS_GENERATOR = "JRuleModuleActionClassGen";

    private final Logger logger = LoggerFactory.getLogger(JRuleModuleActionClassGenerator.class);

    private ModuleTypeRegistry moduleTypeRegistry;

    public JRuleModuleActionClassGenerator(JRuleConfig jRuleConfig, ModuleTypeRegistry moduleTypeRegistry) {
        super(jRuleConfig);
        this.moduleTypeRegistry = moduleTypeRegistry;
    }

    public boolean generateActionSources() {
        Collection<ActionType> actions = moduleTypeRegistry.getActions().stream()
                .filter(actionType -> actionType.getUID().lastIndexOf("#") == -1).collect(Collectors.toList());
        return generateActionSource(actions);
    }

    public boolean generateActionSource(Collection<ActionType> action) {
        try {

            Map<String, Object> freemarkerModel = new HashMap<>();
            freemarkerModel.put("packageName", jRuleConfig.getGeneratedModuleActionPackage());

            List<Object> methods = new ArrayList<>();
            freemarkerModel.put("methods", methods);

            action.forEach(act -> {
                Map<String, Object> actionModel = createActionModel(act);
                methods.add(actionModel);
            });

            File targetSourceFile = new File(
                    new StringBuilder().append(jRuleConfig.getModuleActionsDirectory()).append(File.separator)
                            .append("JRuleModuleActions").append(JRuleConstants.JAVA_FILE_TYPE).toString());

            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration.getTemplate("module/ModuleActions" + TEMPLATE_SUFFIX);
                template.process(freemarkerModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for module actions: {}",
                    ExceptionUtils.getStackTrace(e));

        }

        return false;
    }

    private Map<String, Object> createActionModel(ActionType actionType) {

        Map<String, Object> actionModel = new HashMap<>();
        actionModel.put("name", getActionFriendlyName(actionType.getUID()));
        actionModel.put("uid", actionType.getUID());
        actionModel.put("description", actionType.getDescription());
        actionModel.put("label", actionType.getLabel());

        List<Map<String, Object>> args = new ArrayList<>();
        actionModel.put("args", args);

        actionType.getConfigurationDescriptions().forEach(config -> {
            Map<String, Object> configMap = new HashMap<>();
            configMap.put("name", config.getName());
            configMap.put("type", toJavaType(config.getType()));
            configMap.put("default", config.getDefault());
            configMap.put("label", StringUtils.trimToEmpty(config.getLabel()));
            configMap.put("description", StringUtils.trimToEmpty(config.getDescription()));
            args.add(configMap);
        });

        return actionModel;
    }

    private Object toJavaType(ConfigDescriptionParameter.Type type) {
        switch (type) {
            case BOOLEAN:
                return "Boolean";
            case DECIMAL:
                return "Double";
            case INTEGER:
                return "Integer";
            case TEXT:
                return "String";

            default:
                return "Object";
        }
    }

    public static String getActionFriendlyName(String moduleUID) {
        String[] split = moduleUID.split("[\\.:\\-]");
        return StringUtils.uncapitalize(split[0]) + StringUtils.capitalize(split[1]);
    }
}
