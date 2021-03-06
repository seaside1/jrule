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
 * The {@link JRulePlayerTrigger} Items
 *
 * @author Gerhard Riegler - Initial contribution
 */
public interface JRulePlayerTrigger extends JRuleCommonTrigger {
    String TRIGGER_RECEIVED_UPDATE_ON = "received update PLAY";
    String TRIGGER_RECEIVED_UPDATE_OFF = "received update PAUSE";
    String TRIGGER_RECEIVED_COMMAND_ON = "received command PLAY";
    String TRIGGER_RECEIVED_COMMAND_OFF = "received command PAUSE";
    String TRIGGER_CHANGED_FROM_ON_TO_OFF = "Changed from PLAY to PAUSE";
    String TRIGGER_CHANGED_FROM_OFF_TO_ON = "Changed from PAUSE to PLAY";
    String TRIGGER_CHANGED_FROM_ON = "Changed from PLAY";
    String TRIGGER_CHANGED_FROM_OFF = "Changed from PAUSE";
    String TRIGGER_CHANGED_TO_OFF = "Changed to PAUSE";
    String TRIGGER_CHANGED_TO_ON = "Changed to PLAY";
}
