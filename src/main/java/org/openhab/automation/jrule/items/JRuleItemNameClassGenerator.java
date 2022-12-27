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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.generator.JRuleAbstractClassGenerator;
import org.openhab.core.items.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;

/**
 * The {@link JRuleItemNameClassGenerator} Class Generator
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleItemNameClassGenerator extends JRuleAbstractClassGenerator {

    private static final String TEMPLATE_SUFFIX = ".ftlh";

    protected static final String LOG_NAME_CLASS_GENERATOR = "JRuleItemNameEnumGen";

    private final Logger logger = LoggerFactory.getLogger(JRuleItemNameClassGenerator.class);

    public JRuleItemNameClassGenerator(JRuleConfig jRuleConfig) {
        super(jRuleConfig);
    }

    public boolean generateItemNamesSource(Collection<Item> items) {
        List<Map<String, Object>> model = items.stream().sorted(Comparator.comparing(Item::getName))
                .map(this::createItemModel).collect(Collectors.toList());
        Map<String, Object> processingModel = new HashMap<>();
        processingModel.put("itemNames", model);
        processingModel.put("packageName", jRuleConfig.getGeneratedItemPackage());

        File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getItemsDirectory())
                .append(File.separator).append("JRuleItemNames.java").toString());

        try {
            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration.getTemplate("items/ItemNames" + TEMPLATE_SUFFIX);
                template.process(processingModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for JRuleItemNames.java: {}",
                    ExceptionUtils.getStackTrace(e));

        }
        return false;
    }

    private Map<String, Object> createItemModel(Item item) {
        Map<String, Object> itemModel = new HashMap<>();
        itemModel.put("name", item.getName());
        return itemModel;
    }
}
