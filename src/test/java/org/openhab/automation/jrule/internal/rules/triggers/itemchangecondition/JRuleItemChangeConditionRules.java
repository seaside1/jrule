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
package org.openhab.automation.jrule.internal.rules.triggers.itemchangecondition;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleCondition;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleEvent;

/**
 * The {@link JRuleItemChangeConditionRules} contains rules for testing @JRuleWhenItemChange#previousCondition
 *
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleItemChangeConditionRules extends JRule {
    public static final String ITEM_FROM_TO = "item_from_to";
    public static final String ITEM_FROM_TO_2 = "item_from_to_2";
    public static final String ITEM_FROM_TO_3 = "item_from_to_3";

    @JRuleName("Test JRuleWhenItemChange/from/to")
    @JRuleWhenItemChange(item = ITEM_FROM_TO, previousCondition = @JRuleCondition(lt = 12), condition = @JRuleCondition(gte = 12))
    public void itemChangeFromTo(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/from/to")
    @JRuleWhenItemChange(item = ITEM_FROM_TO_2, previousCondition = @JRuleCondition(lt = 10), condition = @JRuleCondition(gt = 20))
    public void itemChangeFromTo2(JRuleEvent event) {
    }

    @JRuleName("Test JRuleWhenItemChange/from/to Quantity")
    @JRuleWhenItemChange(item = ITEM_FROM_TO_3, previousCondition = @JRuleCondition(lt = 40), condition = @JRuleCondition(gt = 40))
    public void itemChangeFromTo3(JRuleEvent event) {
    }
}
