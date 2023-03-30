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
package org.openhab.automation.jrule.internal.triggers.itemchange;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleMemberOf;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.event.JRuleEvent;

/**
 * The {@link JRuleGroupItemChangeRules} contains rules for testing @JRuleWhenItemChange with memberOf=true trigger
 *
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleGroupItemChangeRules extends JRule {

    public static final String GROUP_ITEM = "group_item";
    public static final String SUB_GROUP_ITEM = "sub_group_item";
    public static final String GROUP_ITEM_JUST_ITEMS = "group_item_just_items";
    public static final String GROUP_ITEM_JUST_GROUPS = "group_item_just_groups";
    public static final String GROUP_ITEM_FROM = "group_item_from";
    public static final String GROUP_ITEM_TO = "group_item_to";
    public static final String GROUP_ITEM_FROM_TO = "group_item_from_to";

    @JRuleName("Test JRuleWhenGroupItemChange")
    @JRuleWhenItemChange(item = GROUP_ITEM, memberOf = JRuleMemberOf.All)
    public void groupItemChange(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemChange/JustItems")
    @JRuleWhenItemChange(item = GROUP_ITEM_JUST_ITEMS, memberOf = JRuleMemberOf.Items)
    public void groupItemChangeJustItems(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemChange/JustGroups")
    @JRuleWhenItemChange(item = GROUP_ITEM_JUST_GROUPS, memberOf = JRuleMemberOf.Groups)
    public void groupItemChangeJustGroups(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemChange/from")
    @JRuleWhenItemChange(item = GROUP_ITEM_FROM, from = "1", memberOf = JRuleMemberOf.All)
    public void groupItemChangeFrom(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemReceivedUpdate/JustItems")
    @JRuleWhenItemReceivedUpdate(item = GROUP_ITEM_JUST_ITEMS, memberOf = JRuleMemberOf.Items)
    public void groupItemReceivedUpdateJustItems(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemReceivedUpdate/JustGroups")
    @JRuleWhenItemReceivedUpdate(item = GROUP_ITEM_JUST_GROUPS, memberOf = JRuleMemberOf.Groups)
    public void groupItemReceivedUpdateJustGroups(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemReceivedUpdate/from")
    @JRuleWhenItemReceivedUpdate(item = GROUP_ITEM_FROM, memberOf = JRuleMemberOf.All)
    public void groupItemReceivedUpdateTo(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemReceivedCommand/JustItems")
    @JRuleWhenItemReceivedCommand(item = GROUP_ITEM_JUST_ITEMS, memberOf = JRuleMemberOf.Items)
    public void groupItemReceivedCommandJustItems(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemReceivedCommand/JustGroups")
    @JRuleWhenItemReceivedCommand(item = GROUP_ITEM_JUST_GROUPS, memberOf = JRuleMemberOf.Groups)
    public void groupItemReceivedCommandJustGroups(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemReceivedCommand/from")
    @JRuleWhenItemReceivedCommand(item = GROUP_ITEM_FROM, memberOf = JRuleMemberOf.All)
    public void groupItemReceivedCommandFrom(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemChange/to")
    @JRuleWhenItemChange(item = GROUP_ITEM_TO, to = "1", memberOf = JRuleMemberOf.All)
    public void groupItemChangeTo(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenGroupItemChange/from/to")
    @JRuleWhenItemChange(item = GROUP_ITEM_FROM_TO, from = "1", to = "2", memberOf = JRuleMemberOf.All)
    public void groupItemChangeFromTo(JRuleEvent event) {
    }
}
