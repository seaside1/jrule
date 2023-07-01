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
package org.openhab.automation.jrule.internal.handler;

import org.openhab.automation.jrule.things.JRuleThingStatus;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingManager;
import org.openhab.core.thing.ThingRegistry;
import org.openhab.core.thing.ThingUID;

/**
 * The {@link JRuleThingHandler} provides access to thing actions
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleThingHandler {

    private static volatile JRuleThingHandler instance = null;

    private JRuleThingHandler() {
    }

    private ThingRegistry thingRegistry;

    private ThingManager thingManager;

    public void setThingManager(ThingManager thingManager) {
        this.thingManager = thingManager;
    }

    public void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    public static JRuleThingHandler get() {
        if (instance == null) {
            synchronized (JRuleThingHandler.class) {
                if (instance == null) {
                    instance = new JRuleThingHandler();
                }
            }
        }
        return instance;
    }

    public void disable(String thingUID) {
        setEnabled(thingUID, false);
    }

    public void enable(String thingUID) {
        setEnabled(thingUID, true);
    }

    private void setEnabled(String thingUID, boolean enabled) {
        Thing thing = thingRegistry.get(new ThingUID(thingUID));
        if (thing != null) {
            thingManager.setEnabled(new ThingUID(thingUID), enabled);
        }
    }

    public JRuleThingStatus getStatus(String thingUID) {
        Thing thing = thingRegistry.get(new ThingUID(thingUID));
        if (thing != null) {
            return JRuleThingStatus.valueOf(thing.getStatus().name());
        } else {
            return JRuleThingStatus.THING_UNKNOWN;
        }
    }
}
