/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.jrule.items;

/**
 * The {@link JRuleItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleItem {
    public static final String TRIGGER_CHANGED = "Changed";
    public static final String TRIGGER_RECEIVED_COMMAND = "received command";
    public static final String TRIGGER_RECEIVED_UPDATE = "received update";

    //
    private String type;
    private String label;
    private String icon;
    private String[] groups;
    private String binding;
}
