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

import java.util.List;
import java.util.Map;

import org.openhab.automation.jrule.internal.items.JRuleInternalNumberGroupItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;

/**
 * The {@link JRuleNumberGroupItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRuleNumberGroupItemTest extends JRuleNumberItemTest {
    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalNumberGroupItem("Group", "Label", "Type", "Id",
                Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom"))),
                List.of("Lighting", "Inside"));
    }
}
