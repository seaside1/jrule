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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.generator.JRuleAbstractClassGenerator;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.type.ChannelKind;
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
        try {
            List<Map<String, Object>> model = things.stream().sorted(Comparator.comparing(e -> getThingFriendlyName(e)))
                    .map(this::createThingModel).collect(Collectors.toList());
            Map<String, Object> processingModel = new HashMap<>();
            processingModel.put("things", model);
            processingModel.put("packageName", jRuleConfig.getGeneratedThingPackage());

            File targetSourceFile = new File(new StringBuilder().append(jRuleConfig.getThingsDirectory())
                    .append(File.separator).append("JRuleThings.java").toString());

            try (FileWriter fileWriter = new FileWriter(targetSourceFile)) {
                Template template = freemarkerConfiguration.getTemplate("things/Things" + TEMPLATE_SUFFIX);
                template.process(processingModel, fileWriter);
            }

            JRuleLog.debug(logger, LOG_NAME_CLASS_GENERATOR, "Wrote Generated class: {}",
                    targetSourceFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            JRuleLog.error(logger, LOG_NAME_CLASS_GENERATOR,
                    "Internal error when generating java source for JRuleThings.java: {}",
                    ExceptionUtils.getStackTrace(e));

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
        freemarkerModel.put("triggerChannels", extractTriggerChannels(thing));

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

    private List<JRuleTriggerChannel> extractTriggerChannels(Thing thing) {
        return thing.getChannels().stream().filter(channel -> channel.getKind() == ChannelKind.TRIGGER)
                .map(channel -> channel.getUID().getId())
                .map(channelName -> new JRuleTriggerChannel(channelName, createFieldName(thing, channelName)))
                .collect(Collectors.toList());
    }

    public static String createFieldName(Thing thing, String channelName) {
        String fieldName = channelName.replaceAll("[#\\-:\\.]", "_");

        if (!isValidJavaIdentifier(fieldName)) {
            // Try prefix with '_'
            fieldName = "_" + fieldName;
        }

        // Check again, throw error
        if (!isValidJavaIdentifier(fieldName)) {
            throw new IllegalArgumentException(
                    String.format("Unable to create a valid Java field name for channel name '%s' in thing '%s'",
                            channelName, thing.getUID()));
        }

        return fieldName;
    }

    public static boolean isValidJavaIdentifier(String s) {
        if (s.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String getThingFriendlyName(Thing thing) {
        return thing.getUID().toString().replace(':', '_').replace('-', '_');
    }

    public static class JRuleTriggerChannel {
        public String channelName;
        public String fieldName;

        public JRuleTriggerChannel(String channelName, String fieldName) {
            this.channelName = channelName;
            this.fieldName = fieldName;
        }

        public String getChannelName() {
            return channelName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }
}
