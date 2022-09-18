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
package org.openhab.automation.jrule.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleConfig} encapsulates all the configuration options for an instance of the
 * {@link org.openhab.automation.jrule.internal.handler.JRuleHandler}.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class JRuleConfig {
    private static final Logger logger = LoggerFactory.getLogger(JRuleConfig.class);
    private static final String AUTOMATION_JRULE = "/automation/jrule";
    private static final String OPENHAB_CONF_PROPERTY = "openhab.conf";

    private static final String JAR_DIR = "jar";

    public static final String ITEMS_PACKAGE = "org.openhab.automation.jrule.items.generated.";
    public static final String RULES_PACKAGE = "org.openhab.automation.jrule.rules.user.";
    private static final String WORKING_DIR_PROPERTY = "org.openhab.automation.jrule.directory";
    private static final String GENERATED_ITEM_PREFIX_PROPERTY = "org.openhab.automation.jrule.itemprefix";
    private static final String GENERATED_ITEM_PACKAGE_PROPERTY = "org.openhab.automation.jrule.itempackage";
    private static final String EXECUTORS_MIN_PROPERTY = "org.openhab.automation.jrule.engine.executors.min";
    private static final String EXECUTORS_MAX_PROPERTY = "org.openhab.automation.jrule.engine.executors.max";
    private static final String EXECUTORS_ENABLE_PROPERTY = "org.openhab.automation.jrule.engine.executors.enable";
    private static final String EXECUTORS_THREAD_KEEPALIVE_PROPERTY = "org.openhab.automation.jrule.engine.executors.keepalive";
    public static final String ITEMS_DIR_START = "items";

    private static final int DEFAULT_MIN_EXECUTORS = 2;
    private static final int DEFAULT_MAX_EXECUTORS = 10;
    private static final long DEFAULT_KEEPALIVE_EXECUTORS = 2L;
    private static final boolean DEFAULT_ENABLE_EXECUTORS = true;

    public static final String RULES_DIR_START = "rules";
    public static final String RULES_DIR = RULES_DIR_START + File.separator + "org" + File.separator + "openhab"
            + File.separator + "automation" + File.separator + "jrule" + File.separator + "rules" + File.separator
            + "user" + File.separator;

    private static final String DEFAULT_WORKING_DIR = File.separator + "etc" + File.separator + "openhab"
            + File.separator + "automation" + File.separator + "jrule";
    private static final String DEFAULT_GENERATED_ITEM_PREFIX = "_";
    private static final String DEFAULT_GENERATED_ITEM_PACKAGE = "org.openhab.automation.jrule.items.generated";

    private static final String CLASS_DIR = "class";

    private static final String EXT_LIB_DIR = "ext-lib";
    public static final String JAR_RULES_DIR = "rules-jar";

    private static final String LOG_NAME_CONF = "JRuleConf";
    private static final String JRULE_CONFIG_NAME = "jrule.conf";

    private static final String INIT_DELAY_PROPERTY = "org.openhab.automation.jrule.engine.initdelay";
    private static final int DEFAULT_INIT_DELAY = 5;
    private static final String INIT_RULES_DELAY_PROPERTY = "org.openhab.automation.jrule.engine.rulesdelay";
    private static final int DEFAULT_RULES_INIT_DELAY = 2;
    private static final String DEFAULT_ITEMS_RECOMPILATION_DELAY_PROPERTY = "org.openhab.automation.jrule.engine.itemsrecompilationdelay";
    private static final int DEFAULT_ITEMS_RECOMPILATION_DELAY = 5;
    private final Map<String, Object> properties;

    private final Properties jRuleProperties;

    public JRuleConfig(Map<String, Object> properties) {
        this.properties = properties;
        jRuleProperties = new Properties();
    }

    public void initConfig() {
        final String workingDirectory = getWorkingDirectory();
        final String configFileName = workingDirectory.concat(File.separator).concat(JRULE_CONFIG_NAME);
        try (InputStream is = new FileInputStream(configFileName)) {
            jRuleProperties.load(is);
        } catch (IOException e) {
            logger.debug("Failed to load properties {}", configFileName);
        }
        jRuleProperties.putAll(properties);
    }

    public int getInitDelaySeconds() {
        final String initDelay = (String) properties.get(INIT_DELAY_PROPERTY);
        int delay = DEFAULT_INIT_DELAY;
        if (initDelay != null) {
            try {
                delay = Integer.parseInt(initDelay);
            } catch (Exception x) {
                // Best effort
            }
        }
        return delay;
    }

    public String getWorkingDirectory() {
        String workingDir = (String) properties.get(WORKING_DIR_PROPERTY);
        if (workingDir == null) {
            String openhabConf = System.getProperty(OPENHAB_CONF_PROPERTY);
            JRuleLog.debug(logger, LOG_NAME_CONF, "Openhab Conf Property: {}", openhabConf);
            if (openhabConf != null) {
                workingDir = openhabConf.concat(AUTOMATION_JRULE);
            }
        }
        return workingDir == null ? DEFAULT_WORKING_DIR : workingDir;
    }

    public String getClassDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(CLASS_DIR).toString();
    }

    public String getItemsDirectory() {
        final StringBuilder sb = new StringBuilder(getWorkingDirectory());
        sb.append(File.separator).append(ITEMS_DIR_START).append(File.separator);
        final String p = JRuleUtil.packageNameToPath(getGeneratedItemPackage());
        sb.append(p);
        return sb.toString();
    }

    public String getJarDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(JAR_DIR).toString();
    }

    public String getRulesDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(RULES_DIR).toString();
    }

    public String getConfigFile() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(JRULE_CONFIG_NAME)
                .toString();
    }

    public String getItemsRootDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(ITEMS_DIR_START)
                .toString();
    }

    public String getRulesRootDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(RULES_DIR_START)
                .toString();
    }

    public String getExtlibDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(EXT_LIB_DIR).toString();
    }

    public String getJarRulesDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(JAR_RULES_DIR)
                .toString();
    }

    private String getConfigPropertyOrDefaultValue(String property, String defaultValue) {
        final String propertyValue = (String) jRuleProperties.get(property);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    public long getKeepAliveExecutors() {
        final String value = getConfigPropertyOrDefaultValue(EXECUTORS_THREAD_KEEPALIVE_PROPERTY,
                "" + DEFAULT_KEEPALIVE_EXECUTORS);
        try {
            return Long.parseLong(value);
        } catch (Exception x) {
            logger.error("Failed to load Min Executors {}", value);
        }
        return DEFAULT_KEEPALIVE_EXECUTORS;
    }

    public int getMinExecutors() {
        final String value = getConfigPropertyOrDefaultValue(EXECUTORS_MIN_PROPERTY, "" + DEFAULT_MIN_EXECUTORS);
        try {
            return Integer.parseInt(value);
        } catch (Exception x) {
            logger.error("Failed to load Min Executors {}", value);
        }
        return DEFAULT_MIN_EXECUTORS;
    }

    public boolean isExecutorsEnabled() {
        final String value = getConfigPropertyOrDefaultValue(EXECUTORS_ENABLE_PROPERTY, "" + DEFAULT_ENABLE_EXECUTORS);
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception x) {
            logger.error("Failed to load enable Executors {}", value);
        }
        return DEFAULT_ENABLE_EXECUTORS;
    }

    public int getMaxExecutors() {
        final String value = getConfigPropertyOrDefaultValue(EXECUTORS_MAX_PROPERTY, "" + DEFAULT_MAX_EXECUTORS);
        try {
            return Integer.parseInt(value);
        } catch (Exception x) {
            logger.error("Failed to load Max Executors {}", value);
        }
        return DEFAULT_MAX_EXECUTORS;
    }

    public String getGeneratedItemPrefix() {
        return getConfigPropertyOrDefaultValue(GENERATED_ITEM_PREFIX_PROPERTY, DEFAULT_GENERATED_ITEM_PREFIX);
    }

    public String getGeneratedItemPackage() {
        return getConfigPropertyOrDefaultValue(GENERATED_ITEM_PACKAGE_PROPERTY, DEFAULT_GENERATED_ITEM_PACKAGE);
    }

    public int getRulesInitDelaySeconds() {
        return getIntConfigProperty(INIT_RULES_DELAY_PROPERTY, DEFAULT_RULES_INIT_DELAY);
    }

    public int getItemsRecompilationDelaySeconds() {
        return getIntConfigProperty(DEFAULT_ITEMS_RECOMPILATION_DELAY_PROPERTY, DEFAULT_ITEMS_RECOMPILATION_DELAY);
    }

    private int getIntConfigProperty(String propertyName, int defaultValue) {
        final String configuredValue = (String) properties.get(propertyName);
        if (configuredValue != null) {
            try {
                return Integer.parseInt(configuredValue);
            } catch (Exception x) {
                // Best effort
            }
        }
        return defaultValue;
    }
}
