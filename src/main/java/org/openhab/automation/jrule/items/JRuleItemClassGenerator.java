/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.CoreItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleItemClassGenerator} Class Generator
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleItemClassGenerator {

    private final Logger logger = LoggerFactory.getLogger(JRuleItemClassGenerator.class);

    private final URL itemClassTemplateUrl;
    private final URL switchItemClassTemplateUrl;
    private final URL dimmerItemClassTemplateUrl;
    private final URL numberItemClassTemplateUrl;
    private final URL stringItemClassTemplateUrl;
    private final URL dateTimeItemClassTemplateUrl;
    private final URL groupItemClassTemplateUrl;

    private final String itemClassTemplate;
    private final String switchItemClassTemplate;
    private final String dimmerItemClassTemplate;
    private final String numberItemClassTemplate;
    private final String stringItemClassTemplate;
    private final String dateTimeItemClassTemplate;
    private final String groupItemClassTemplate;

    private final JRuleConfig jRuleConfig;

    public JRuleItemClassGenerator(JRuleConfig jRuleConfig) {
        itemClassTemplateUrl = JRuleUtil.getResourceUrl("ItemClass.template");
        itemClassTemplate = JRuleUtil.getResourceAsString(itemClassTemplateUrl);
        switchItemClassTemplateUrl = JRuleUtil.getResourceUrl("ItemClassSwitch.template");
        switchItemClassTemplate = JRuleUtil.getResourceAsString(switchItemClassTemplateUrl);
        dimmerItemClassTemplateUrl = JRuleUtil.getResourceUrl("ItemClassDimmer.template");
        dimmerItemClassTemplate = JRuleUtil.getResourceAsString(dimmerItemClassTemplateUrl);
        numberItemClassTemplateUrl = JRuleUtil.getResourceUrl("ItemClassNumber.template");
        numberItemClassTemplate = JRuleUtil.getResourceAsString(numberItemClassTemplateUrl);
        stringItemClassTemplateUrl = JRuleUtil.getResourceUrl("ItemClassString.template");
        stringItemClassTemplate = JRuleUtil.getResourceAsString(stringItemClassTemplateUrl);
        dateTimeItemClassTemplateUrl = JRuleUtil.getResourceUrl("ItemClassDateTime.template");
        dateTimeItemClassTemplate = JRuleUtil.getResourceAsString(dateTimeItemClassTemplateUrl);
        groupItemClassTemplateUrl = JRuleUtil.getResourceUrl("ItemClassGroup.template");
        groupItemClassTemplate = JRuleUtil.getResourceAsString(groupItemClassTemplateUrl);
        this.jRuleConfig = jRuleConfig;
    }

    public File getJavaItemForFile(Item item) {
        return new File(new StringBuilder().append(jRuleConfig.getItemsDirectory()).append(File.separator)
                .append(JRuleConstants.JRULE_GENERATION_PREFIX).append(item.getName())
                .append(JRuleConstants.JAVA_FILE_TYPE).toString());
    }

    public boolean generateItemSource(Item item) {
        File f = getJavaItemForFile(item);
        String generatedClass = null;
        String type = item.getType();
        if (type.contains(":")) {
            String[] split = item.getType().split(":");
            type = split[0];
        }
        if (type.equals(CoreItemFactory.SWITCH)) {
            generatedClass = switchItemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.DIMMER)) {
            generatedClass = dimmerItemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.NUMBER)) {
            generatedClass = numberItemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.STRING)) {
            generatedClass = stringItemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.IMAGE)) {
            generatedClass = itemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.CALL)) {
            generatedClass = itemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.ROLLERSHUTTER)) {
            generatedClass = itemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.LOCATION)) {
            generatedClass = itemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.COLOR)) {
            generatedClass = itemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.CONTACT)) {
            generatedClass = itemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.PLAYER)) {
            generatedClass = itemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(CoreItemFactory.DATETIME)) {
            generatedClass = dateTimeItemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else if (type.equals(GroupItem.TYPE)) {
            generatedClass = groupItemClassTemplate.replaceAll("ITEMNAME", item.getName());
        } else {
            logger.debug("Unsupported item type for item: {} type: {}", item.getName(), item.getType());
            return false;
        }
        if (f.exists()) {
            String existingClass = JRuleUtil.getFileAsString(f);
            if (existingClass.equals(generatedClass)) {
                logger.debug("Item: {} already exists, ignoring", item.getName());
                return false;
            }

        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(f);
            fout.write(generatedClass.getBytes(StandardCharsets.UTF_8));
            logger.debug("Wrote Generated class: {}", f.getAbsolutePath());
            return true;
        } catch (FileNotFoundException e1) {
            logger.error("Failed to write generated class", e1);
        } catch (IOException e) {
            logger.error("Failed to write generated class", e);
        } finally {
            if (fout != null) {
                try {
                    fout.flush();
                } catch (IOException e) {
                }
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }
}
