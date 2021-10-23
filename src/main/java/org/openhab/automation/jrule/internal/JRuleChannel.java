/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.automation.jrule.internal;

import java.util.stream.Stream;

/**
 * The {@link JRuleChannel} class defines channel enum.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public enum JRuleChannel {

    GENERATE_ITEMS;

    private static final String DASH = "-";
    private static final String UNDERSCORE = "_";

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    @SuppressWarnings("null")
    public static Stream<JRuleChannel> stream() {
        return Stream.of(JRuleChannel.values());
    }

    public static JRuleChannel fromString(String str) {
        return JRuleChannel.stream()
                .filter(channelList -> str.replaceAll(DASH, UNDERSCORE).equalsIgnoreCase(channelList.name()))
                .findFirst().orElse(null);
    }
}
