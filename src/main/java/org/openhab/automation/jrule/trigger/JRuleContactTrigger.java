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
 * The {@link JRuleSwitchTrigger} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public interface JRuleContactTrigger extends JRuleCommonTrigger {
    String TRIGGER_RECEIVED_UPDATE_OPEN = "received update OPEN";
    String TRIGGER_RECEIVED_UPDATE_CLOSED = "received update CLOSED";
    String TRIGGER_CHANGED_FROM_OPEN_TO_CLOSED = "Changed from OPEN to CLOSED";
    String TRIGGER_CHANGED_FROM_OPEN = "Changed from OPEN";
    String TRIGGER_CHANGED_FROM_CLOSED = "Changed from CLOSED";
    String TRIGGER_CHANGED_TO_CLOSED = "Changed to CLOSED";
    String TRIGGER_CHANGED_TO_OPEN = "Changed to OPEN";
    String TRIGGER_CHANGED_FROM_CLOSED_TO_OPEN = "Changed from CLOSED to OPEN";
}
