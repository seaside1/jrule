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
package org.openhab.automation.jrule.items.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link JRuleItemMetadata} containing all metadata information.
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemMetadata {
    private final String value;
    private final Map<String, Object> configuration;

    public JRuleItemMetadata(String value, Map<String, Object> configuration) {
        this.value = value;
        this.configuration = configuration;
    }

    public JRuleItemMetadata(String value) {
        this(value, new HashMap<>());
    }

    public String getValue() {
        return value;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return String.format("%s, configuration=%s", value, configuration);
    }
}
