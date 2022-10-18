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

import org.openhab.automation.jrule.internal.handler.JRuleThingHandler;
import org.openhab.automation.jrule.trigger.JRuleThingStatusTrigger;

/**
 * The {@link JRuleAbstractThing} represents a thing that is either a bridge, a bridged (sub thing of a bridge) or a
 * standalone thing not in need of a bridge
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleAbstractThing implements JRuleThingStatusTrigger {
    private String thingUID;

    protected JRuleAbstractThing(String thingUID) {
        this.thingUID = thingUID;
    }

    public String getThingUID() {
        return thingUID;
    }

    public abstract String getLabel();

    public JRuleThingStatus getStatus() {
        return JRuleThingHandler.get().getStatus(thingUID);
    }

    public void disable() {
        JRuleThingHandler.get().disable(thingUID);
    }

    public void enable() {
        JRuleThingHandler.get().enable(thingUID);
    }

    public void restart() {
        disable();
        enable();
    }
}
