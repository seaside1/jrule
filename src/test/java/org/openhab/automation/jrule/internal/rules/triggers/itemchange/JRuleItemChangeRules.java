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
package org.openhab.automation.jrule.internal.rules.triggers.itemchange;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleEvent;

/**
 * The {@link JRuleItemChangeRules} contains rules for testing @JRuleWhenItemChange trigger
 *
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleItemChangeRules extends JRule {

    public static final String ITEM = "item";
    public static final String ITEM_FROM = "item_from";
    public static final String ITEM_TO = "item_to";
    public static final String ITEM_FROM_TO = "item_from_to";
    public static final String ITEM_DUPLICATE = "item_duplicate";

    @JRuleName("Test JRuleWhenItemChange")
    @JRuleWhenItemChange(item = ITEM)
    public void itemChange(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/from")
    @JRuleWhenItemChange(item = ITEM_FROM, from = "1")
    public void itemChangeFrom(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/to")
    @JRuleWhenItemChange(item = ITEM_TO, to = "1")
    public void itemChangeTo(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/from/to")
    @JRuleWhenItemChange(item = ITEM_FROM_TO, from = "1", to = "2")
    public void itemChangeFromTo(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/multiple")
    @JRuleWhenItemChange(item = ITEM_DUPLICATE, to = "1")
    @JRuleWhenItemChange(item = ITEM_DUPLICATE)
    public void duplicateMatchingWhen(JRuleEvent event) {
    }
}
