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

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleLocationGroupItem;

/**
 * The {@link JRuleInternalLocationGroupItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalLocationGroupItem extends JRuleInternalLocationItem implements JRuleLocationGroupItem {

    public JRuleInternalLocationGroupItem(String name, String label, String type, String id) {
        super(name, label, type, id);
    }

    public static JRuleInternalLocationGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalLocationGroupItem.class);
    }
}
