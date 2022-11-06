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
package org.openhab.automation.jrule.rules.event;

/**
 * The {@link JRuleThingEvent}
 *
 * @author Robert Delbrück
 */
public class JRuleThingEvent extends JRuleEvent {
    private String thing;
    private String status;

    private String oldStatus;

    public String getThing() {
        return thing;
    }

    public String getStatus() {
        return status;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public JRuleThingEvent(String thing, String status, String oldStatus) {
        this.thing = thing;
        this.status = status;
        this.oldStatus = oldStatus;
    }

    @Override
    public String toString() {
        return "JRuleThingEvent{" + "thing='" + thing + '\'' + ", status='" + status + '\'' + ", oldStatus='"
                + oldStatus + '\'' + '}';
    }
}
