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

package org.openhab.automation.jrule.persistence;

import java.time.ZonedDateTime;

import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleHistoricState} class, which wraps {@link org.openhab.core.persistence.HistoricItem} from openHAB
 * core.
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRuleHistoricState {
    private final JRuleValue value;
    private final ZonedDateTime timestamp;

    public JRuleHistoricState(JRuleValue value, ZonedDateTime timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public JRuleValue getValue() {
        return value;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
