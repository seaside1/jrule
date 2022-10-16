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
package org.openhab.automation.jrule.items;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.generator.AbstractClassGenerator;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.CoreItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The {@link JRuleItemClassGenerator} Class Generator
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleItemClassGenerator extends AbstractClassGenerator {

    private static final String TEMPLATE_SUFFIX = ".ftlh";

    protected static final String LOG_NAME_CLASS_GENERATOR = "JRuleItemClassGen";

    private final Logger logger = LoggerFactory.getLogger(JRuleItemClassGenerator.class);

    public JRuleItemClassGenerator(JRuleConfig jRuleConfig) {

        super(jRuleConfig);
    }

    public boolean generateItemSource(Item item) {
        try {
            String type = item.getType();
            String templateName = getTemplateFromType(type);

            if (templateName == null) {
                JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Unsupported item type for item: {} type: {}",
                        item.getName(), item.getType());
            } else {

                Map<String, Object> processingModel = new HashMap<>();
                processingModel.put("item", createItemModel(item));

                File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getItemsDirectory())
                        .append(File.separator).append(jRuleConfig.getGeneratedItemPrefix()).append(item.getName())
                        .append(JRuleConstants.JAVA_FILE_TYPE).toString());

                try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                    Template template = freemarkerConfiguration.getTemplate("items/" + templateName);
                    template.process(processingModel, fileWriter);
                }

                JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                        targetSourceFile.getAbsolutePath());
                return true;
            }
        } catch (TemplateException | IOException e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for item {}: {}", item, e.toString());

        }

        return false;
    }

    public boolean generateItemsSource(Collection<Item> items) {
        List<Map<String, Object>> model = items.stream().sorted(Comparator.comparing(Item::getName))
                .map(this::createItemModel).collect(Collectors.toList());
        Map<String, Object> processingModel = new HashMap<>();
        processingModel.put("items", model);
        processingModel.put("packageName", jRuleConfig.getGeneratedItemPackage());

        File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getItemsDirectory())
                .append(File.separator).append("JRuleItems.java").toString());

        try {
            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration.getTemplate("items/Items" + TEMPLATE_SUFFIX);
                template.process(processingModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;
        } catch (TemplateException | IOException e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for JRuleItems.java: {}", e.toString());

        }
        return false;
    }

    private Map<String, Object> createItemModel(Item item) {
        Map<String, Object> itemModel = new HashMap<>();
        itemModel.put("id", item.getUID());
        itemModel.put("name", item.getName());
        itemModel.put("package", jRuleConfig.getGeneratedItemPackage());
        itemModel.put("class", jRuleConfig.getGeneratedItemPrefix() + item.getName());
        if (isQuantityType(item.getType())) {
            itemModel.put("quantityType", getQuantityType(item.getType()));
        }
        itemModel.put("label", item.getLabel());
        itemModel.put("type", item.getType());

        // Group handling
        if (item.getType().equals(GroupItem.TYPE)) {
            Item baseItem = ((GroupItem) item).getBaseItem();

            String baseItemType = "String"; // Defaulting to a simple string value, can hold any state
            if (baseItem != null && baseItem.getType() != null) {
                baseItemType = baseItem.getType();
            }

            if (isQuantityType(baseItemType)) {
                itemModel.put("parentClass", "JRuleGroupNumberItem");
                itemModel.put("quantityType", getQuantityType(baseItemType));
            } else {
                itemModel.put("parentClass", "JRuleGroup" + baseItemType + "Item");
            }
        }

        return itemModel;
    }

    private String getQuantityType(String type) {
        return type.split(":")[1];
    }

    private boolean isQuantityType(String type) {
        String[] split = type.split(":");
        return split.length > 1 && CoreItemFactory.NUMBER.equals(split[0]);
    }

    private String getTemplateFromType(String type) {

        if (type.equals(CoreItemFactory.SWITCH)) {
            return "ItemClassSwitch" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.DIMMER)) {
            return "ItemClassDimmer" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.NUMBER) || isQuantityType(type)) {
            return "ItemClassNumber" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.STRING)) {
            return "ItemClassString" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.IMAGE)) {
            return "ItemClassImage" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.CALL)) {
            return "ItemClassCall" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.ROLLERSHUTTER)) {
            return "ItemClassRollershutter" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.LOCATION)) {
            return "ItemClassLocation" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.COLOR)) {
            return "ItemClassColor" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.CONTACT)) {
            return "ItemClassContact" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.PLAYER)) {
            return "ItemClassPlayer" + TEMPLATE_SUFFIX;
        } else if (type.equals(CoreItemFactory.DATETIME)) {
            return "ItemClassDateTime" + TEMPLATE_SUFFIX;
        } else if (type.equals(GroupItem.TYPE)) {
            return "ItemClassGroup" + TEMPLATE_SUFFIX;
        }
        return null;
    }
}
