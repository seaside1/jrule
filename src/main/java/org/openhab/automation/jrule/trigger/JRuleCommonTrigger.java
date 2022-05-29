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
package org.openhab.automation.jrule.trigger;

/**
 * The {@link JRuleCommonTrigger} Items
 *
 * @author Gerhard Riegler - Initial contribution
 */
public interface JRuleCommonTrigger {
    String TRIGGER_CHANGED = "Changed";
    String TRIGGER_RECEIVED_COMMAND = "received command";
    String TRIGGER_RECEIVED_UPDATE = "received update";
}
