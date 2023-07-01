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
 * The {@link JRuleChannelEvent}
 *
 * @author Robert Delbrück
 */
public class JRuleChannelEvent extends JRuleEvent {
    private String channel;
    private String event;

    public String getChannel() {
        return channel;
    }

    public String getEvent() {
        return event;
    }

    public JRuleChannelEvent(String channel, String event) {
        this.channel = channel;
        this.event = event;
    }

    @Override
    public String toString() {
        return String.format("JRuleEvent [channel=%s]", channel);
    }
}
