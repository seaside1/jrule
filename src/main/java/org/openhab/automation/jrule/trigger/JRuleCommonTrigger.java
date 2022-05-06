/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.trigger;

/**
 * The {@link JRuleCommonTrigger} Items
 *
 * @author Gerhard Riegler - Initial contribution
 */

public interface JRuleCommonTrigger {
    public static final String TRIGGER_CHANGED = "Changed";
    public static final String TRIGGER_RECEIVED_COMMAND = "received command";
    public static final String TRIGGER_RECEIVED_UPDATE = "received update";
}
