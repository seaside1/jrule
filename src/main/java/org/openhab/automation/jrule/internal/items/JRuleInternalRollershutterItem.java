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

import org.openhab.automation.jrule.items.JRuleRollershutterItem;

import java.util.List;
import java.util.Map;

/**
 * The {@link JRuleInternalRollershutterItem} Items
 *
 * @author Timo Litzius - Initial contribution
 */
public class JRuleInternalRollershutterItem extends JRuleInternalItem implements JRuleRollershutterItem {

    public JRuleInternalRollershutterItem(String name, String label, String type, String id, Map<String, String> metadata, List<String> tags) {
        super(name, label, type, id, metadata, tags);
    }
}
