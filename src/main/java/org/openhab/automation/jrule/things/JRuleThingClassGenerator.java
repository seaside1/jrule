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
package org.openhab.automation.jrule.things;

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
import org.openhab.automation.jrule.internal.generator.JRuleAbstractClassGenerator;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The {@link JRuleThingClassGenerator} Class Generator for things
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleThingClassGenerator extends JRuleAbstractClassGenerator {

    private static final String TEMPLATE_SUFFIX = ".ftlh";

    protected static final String LOG_NAME_CLASS_GENERATOR = "JRuleThingClassGen";

    private final Logger logger = LoggerFactory.getLogger(JRuleThingClassGenerator.class);

    public JRuleThingClassGenerator(JRuleConfig jRuleConfig) {
        super(jRuleConfig);
    }

    public boolean generateThingSource(Thing thing) {
        try {

            Map<String, Object> processingModel = new HashMap<>();

            Map<String, Object> thingModel = createThingModel(thing);
            processingModel.put("thing", thingModel);

            File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getThingsDirectory())
                    .append(File.separator).append(jRuleConfig.getGeneratedItemPrefix())
                    .append(getThingFriendlyName(thing)).append(JRuleConstants.JAVA_FILE_TYPE).toString());

            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration
                        .getTemplate("things/" + thingModel.get("templateName") + TEMPLATE_SUFFIX);
                template.process(processingModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;

        } catch (TemplateException | IOException e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for thing {}: {}", thing.getUID().toString(),
                    e.toString());

        }

        return false;
    }

    public boolean generateThingsSource(Collection<Thing> things) {
        List<Map<String, Object>> model = things.stream().sorted(Comparator.comparing(e -> getThingFriendlyName(e)))
                .map(this::createThingModel).collect(Collectors.toList());
        Map<String, Object> processingModel = new HashMap<>();
        processingModel.put("things", model);
        processingModel.put("packageName", jRuleConfig.getGeneratedThingPackage());

        File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getThingsDirectory())
                .append(File.separator).append("JRuleThings.java").toString());

        try {
            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration.getTemplate("things/Things" + TEMPLATE_SUFFIX);
                template.process(processingModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;
        } catch (TemplateException | IOException e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for JRuleThings.java: {}", e.toString());

        }
        return false;
    }

    private Map<String, Object> createThingModel(Thing thing) {
        Map<String, Object> freemarkerModel = new HashMap<>();
        freemarkerModel.put("id", thing.getUID().toString());
        freemarkerModel.put("name", getThingFriendlyName(thing));
        freemarkerModel.put("package", jRuleConfig.getGeneratedThingPackage());
        freemarkerModel.put("class", jRuleConfig.getGeneratedItemPrefix() + getThingFriendlyName(thing));
        freemarkerModel.put("label", thing.getLabel());

        if (thing.getBridgeUID() != null) {
            freemarkerModel.put("parentClass", "JRuleSubThing");
            freemarkerModel.put("templateName", "SubThing");
            freemarkerModel.put("bridgeUID", thing.getBridgeUID().toString());
        } else if (thing instanceof Bridge) {
            freemarkerModel.put("parentClass", "JRuleBridgeThing");
            freemarkerModel.put("templateName", "BridgeThing");
            Bridge bridge = (Bridge) thing;
            freemarkerModel.put("subThingUIDs",
                    bridge.getThings().stream().map(e -> e.getUID().toString()).collect(Collectors.toList()));
        } else {
            freemarkerModel.put("parentClass", "JRuleStandaloneThing");
            freemarkerModel.put("templateName", "Standalone");
        }

        return freemarkerModel;
    }

    public static String getThingFriendlyName(Thing thing) {
        return thing.getUID().toString().replace(':', '_').replace('-', '_');
    }
}
