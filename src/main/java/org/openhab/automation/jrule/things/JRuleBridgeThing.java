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
package org.openhab.automation.jrule.things;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@link JRuleBridgeThing} thing represents a thing that is connected to a bridge
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleBridgeThing extends AbstractJRuleThing {

    public JRuleBridgeThing(String thingUID) {
        super(thingUID);
    }

    public abstract List<String> getSubThingUIDs();

    public List<JRuleSubThing> getChildThings() {
        return getSubThingUIDs().stream().map(e -> JRuleThingRegistry.get(e, JRuleSubThing.class))
                .collect(Collectors.toList());
    }

    public static JRuleBridgeThing forName(String thingUID) {
        return JRuleThingRegistry.get(thingUID, JRuleBridgeThing.class);
    }
}
