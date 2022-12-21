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
package org.openhab.automation.jrule.internal.items;

import org.openhab.automation.jrule.items.JRuleStringItem;

import java.util.Map;

/**
 * The {@link JRuleInternalStringItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleInternalStringItem extends JRuleInternalItem implements JRuleStringItem {

    public JRuleInternalStringItem(String name, String label, String type, String id, Map<String, String> metadata) {
        super(name, label, type, id, metadata, tags);
    }
}
