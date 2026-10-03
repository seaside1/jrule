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
package org.openhab.automation.jrule.internal.rules.triggers.itemreceivedcommand;

import org.openhab.automation.jrule.items.JRuleDimmerItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

/**
 * The {@link JRuleItemReceivedCommandRules} contains rules for testing @JRuleWhenItemReceivedCommand trigger
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemReceivedCommandRules extends JRule {

    public static final String DIMMER_ITEM = "dimmer_item";
    public static final String DIMMER_ITEM_INCREASE_ONLY = "dimmer_item_increase_only";

    @JRuleName("Test JRuleWhenItemReceivedCommand/dimmer")
    @JRuleWhenItemReceivedCommand(item = DIMMER_ITEM)
    public void receivedCommand(JRuleItemEvent event) {
    }

    @JRuleName("Test JRuleWhenItemReceivedCommand/dimmer/INCREASE")
    @JRuleWhenItemReceivedCommand(item = DIMMER_ITEM_INCREASE_ONLY, command = JRuleDimmerItem.INCREASE)
    public void receivedIncreaseCommand(JRuleItemEvent event) {
    }
}
