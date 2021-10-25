/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleMachineThingConfig} encapsulates all the configuration options for an instance of the
 * {@link JRuleClientThingHandler}.
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
    public static final String ITEMS_DIR_START = "items";

    public static final String ITEMS_DIR = ITEMS_DIR_START + File.separator + "org" + File.separator + "openhab"
            + File.separator + "automation" + File.separator + "jrule" + File.separator + "items" + File.separator
            + "generated" + File.separator;

    public static final String RULES_DIR_START = "rules";
    public static final String RULES_DIR = RULES_DIR_START + File.separator + "org" + File.separator + "openhab"
            + File.separator + "automation" + File.separator + "jrule" + File.separator + "rules" + File.separator
            + "user" + File.separator;
    private static final String DEFAULT_WORKING_DIR = "/etc/openhab/automation/jrule";

    private static final String CLASS_DIR = "class";

    private static final String EXT_LIB_DIR = "ext-lib";

    private final Map<String, Object> properties;

    public JRuleConfig(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getWorkingDirectory() {
        String workingDir = (String) properties.get(WORKING_DIR_PROPERTY);
        if (workingDir == null) {
            String openhabConf = System.getProperty(OPENHAB_CONF_PROPERTY);
            logger.debug("Openhab Conf Property: {}", openhabConf);
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
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(ITEMS_DIR).toString();
    }

    public String getJarDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(JAR_DIR).toString();
    }

    public String getRulesDirectory() {
        return new StringBuilder().append(getWorkingDirectory()).append(File.separator).append(RULES_DIR).toString();
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
}
