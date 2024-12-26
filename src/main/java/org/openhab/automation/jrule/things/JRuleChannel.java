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
package org.openhab.automation.jrule.things;

/**
 * The {@link JRuleChannel} represents a Thing channel, possibly linked to an Item.
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleChannel {
    private String channelUID;

    public JRuleChannel(String channelUID) {
        this.channelUID = channelUID;
    }

    public String getChannelUID() {
        return channelUID;
    }
}
