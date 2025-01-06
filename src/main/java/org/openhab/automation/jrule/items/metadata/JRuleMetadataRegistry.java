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

import java.util.Map;
import java.util.stream.Collectors;

import org.openhab.core.items.Metadata;
import org.openhab.core.items.MetadataKey;
import org.openhab.core.items.MetadataRegistry;

/**
 * Used to handle metadata from openhab
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleMetadataRegistry {
    private final MetadataRegistry metadataRegistry;

    public JRuleMetadataRegistry(MetadataRegistry metadataRegistry) {
        this.metadataRegistry = metadataRegistry;
    }

    public Map<String, JRuleItemMetadata> getAllMetadata(String itemName) {
        return getAllMetadata(itemName, metadataRegistry);
    }

    public static Map<String, JRuleItemMetadata> getAllMetadata(String itemName, MetadataRegistry metadataRegistry) {
        return metadataRegistry.stream().filter(metadata -> metadata.getUID().getItemName().equals(itemName))
                .collect(Collectors.toMap(metadata -> metadata.getUID().getNamespace(),
                        metadata -> new JRuleItemMetadata(metadata.getValue(), metadata.getConfiguration())));
    }

    public void addMetadata(String namespace, String itemName, JRuleItemMetadata metadata, boolean override) {
        MetadataKey key = new MetadataKey(namespace, itemName);
        Metadata foundMetadata = metadataRegistry.get(key);
        if (foundMetadata != null && override) {
            metadataRegistry.remove(key);
        }

        metadataRegistry.add(new Metadata(key, metadata.getValue(), metadata.getConfiguration()));
    }
}
