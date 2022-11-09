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

import org.openhab.automation.jrule.items.JRuleContactItem;
import org.openhab.automation.jrule.rules.value.JRuleOpenClosedValue;

/**
 * The {@link JRuleInternalContactItem} Items
 *
 * @author Timo Litzius - Initial contribution
 */
public class JRuleInternalContactItem extends JRuleInternalItem<JRuleOpenClosedValue> implements JRuleContactItem {

    protected JRuleInternalContactItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }
}
