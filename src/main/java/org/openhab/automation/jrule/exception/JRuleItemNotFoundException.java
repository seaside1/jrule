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
package org.openhab.automation.jrule.exception;

/**
 * The {@link JRuleItemNotFoundException} rethrows internal ItemNotFoundException's
 *
 * @author Robert Delbrück
 */
public class JRuleItemNotFoundException extends JRuleRuntimeException {
    public JRuleItemNotFoundException(String message) {
        super(message);
    }

    public JRuleItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
