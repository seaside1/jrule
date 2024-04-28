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
package org.openhab.automation.jrule.rules.event;

/**
 * The {@link JRuleStartupEvent}
 *
 * @author Robert Delbrück
 */
public class JRuleStartupEvent extends JRuleEvent {
    private final int startupLevel;

    public JRuleStartupEvent(int startupLevel) {
        this.startupLevel = startupLevel;
    }

    public int getStartupLevel() {
        return startupLevel;
    }

    @Override
    public String toString() {
        return "JRuleStartupEvent{" + "startupLevel=" + startupLevel + '}';
    }
}
