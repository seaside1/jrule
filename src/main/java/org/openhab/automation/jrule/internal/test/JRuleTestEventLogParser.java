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
package org.openhab.automation.jrule.internal.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.handler.JRuleHandler;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.openhab.core.types.TypeParser;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleTestEventLogParser}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleTestEventLogParser {

    private final URL url;
    private final Logger logger = LoggerFactory.getLogger(JRuleTestEventLogParser.class);

    public JRuleTestEventLogParser(String eventLogResourceName) {
        url = getResourceUrl(eventLogResourceName);
    }

    private List<JRuleMockedItemStateChangedEvent> readFromUrl(URL url) {
        final List<JRuleMockedItemStateChangedEvent> changeEventList = new ArrayList<>();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while (reader.ready()) {
                final String line = reader.readLine();
                final JRuleMockedItemStateChangedEvent parseItemFromLine = parseItemFromLine(line);
                if (parseItemFromLine != null) {
                    changeEventList.add(parseItemFromLine);
                }
            }
        } catch (IOException x) {
            JRuleLog.error(logger, "JRuleTestEventLogParser", "Failed to parse url: {}", url, x);
        }
        return changeEventList;
    }

    private JRuleMockedItemStateChangedEvent parseItemFromLine(String line) {
        if (!line.contains("ItemStateChangedEvent")) {
            return null;
        }
        String stripped = line.substring(line.lastIndexOf("]"));

        String[] fragments = stripped.split("\\s+");
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i].contains("Item")) {
                String itemName = fragments[++i].replaceAll("'", "");
                String trigger = fragments[++i];
                i++;
                String from = fragments[++i];
                i++;
                String to = fragments[++i];

                Type toType = TypeParser.parseType("DecimalType", to);
                Type fromType = TypeParser.parseType("DecimalType", from);
                if (toType == null) {
                    toType = TypeParser.parseType("DateTimeType", to);
                    fromType = TypeParser.parseType("DateTimeType", from);

                }
                String topic = "openhab/items/" + itemName + "/statechanged";
                return new JRuleMockedItemStateChangedEvent(topic, "", itemName, (State) toType, (State) fromType);
            }
        }
        return null;
    }

    public List<JRuleMockedItemStateChangedEvent> parse() {
        return readFromUrl(url);
    }

    public static URL getResourceUrl(String resource) {
        URL resourceUrl = null;
        try {
            resourceUrl = FrameworkUtil.getBundle(JRuleHandler.class).getResource(resource);
        } catch (Exception x) {
            try {
                resourceUrl = JRuleUtil.class.getClassLoader().getResource(resource);
            } catch (Exception x2) {

            }
        }
        return resourceUrl;
    }
}
