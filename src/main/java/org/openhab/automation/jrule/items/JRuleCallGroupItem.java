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
package org.openhab.automation.jrule.items;

import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.items.JRuleInternalCallGroupItem;
import org.openhab.automation.jrule.rules.value.JRuleStringListValue;

/**
 * The {@link JRuleCallGroupItem} Items
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleCallGroupItem extends JRuleCallItem, JRuleGroupItem<JRuleCallItem> {
    static JRuleCallGroupItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleInternalCallGroupItem.class);
    }

    static Optional<JRuleCallGroupItem> forNameOptional(String itemName) {
        return Optional.ofNullable(JRuleUtil.forNameWrapExceptionAsNull(() -> forName(itemName)));
    }

    default void postUpdate(JRuleStringListValue state) {
        memberItems().forEach(i -> i.postUncheckedUpdate(state));
    }
}
