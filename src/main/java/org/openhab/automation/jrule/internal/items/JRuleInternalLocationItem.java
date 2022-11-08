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
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.items.JRuleLocationItem;
import org.openhab.automation.jrule.rules.value.JRulePointValue;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * The {@link JRuleInternalLocationItem} Items
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleInternalLocationItem extends JRuleInternalItem<JRulePointValue> implements JRuleLocationItem {
    protected JRuleInternalLocationItem(String itemName) {
        super(itemName);
    }

    @Override
    public JRulePointValue getState() {
        return new JRulePointValue(JRuleEventHandler.get().getStringValue(getName()));
    }

    @Override
    public Optional<JRulePointValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
        return Optional.empty();
    }
}
