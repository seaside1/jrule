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
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.generator.JRuleAbstractClassGenerator;
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
public class JRuleItemClassGenerator extends JRuleAbstractClassGenerator {

    private static final String TEMPLATE_SUFFIX = ".ftlh";

    protected static final String LOG_NAME_CLASS_GENERATOR = "JRuleItemClassGen";

    private final Logger logger = LoggerFactory.getLogger(JRuleItemClassGenerator.class);

    public JRuleItemClassGenerator(JRuleConfig jRuleConfig) {

        super(jRuleConfig);
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
        String plainType = item.getType().contains(":") ? item.getType().split(":")[0] : item.getType();
        itemModel.put("class", "JRuleInternal" + plainType + "Item");
        if (isQuantityType(item.getType())) {
            itemModel.put("quantityType", getQuantityType(item.getType()));
        }
        itemModel.put("label", item.getLabel());
        itemModel.put("type", item.getType());

        // Group handling
        if (item.getType().equals(GroupItem.TYPE)) {
            Item baseItem = ((GroupItem) item).getBaseItem();
            String plainGroupType = baseItem.getType().contains(":") ? baseItem.getType().split(":")[0]
                    : baseItem.getType();
            itemModel.put("class", "JRuleInternal" + plainGroupType + "GroupItem");
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
}
