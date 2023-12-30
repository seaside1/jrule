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
package org.openhab.automation.jrule.internal.items;

import java.util.List;

import org.openhab.automation.jrule.items.JRuleLocationItem;
import org.openhab.automation.jrule.items.metadata.JRuleMetadataRegistry;

/**
 * The {@link JRuleInternalLocationItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalLocationItem extends JRuleInternalItem implements JRuleLocationItem {
    public JRuleInternalLocationItem(String name, String label, String type, String id,
            JRuleMetadataRegistry metadataRegistry, List<String> tags) {
        super(name, label, type, id, metadataRegistry, tags);
    }
}
