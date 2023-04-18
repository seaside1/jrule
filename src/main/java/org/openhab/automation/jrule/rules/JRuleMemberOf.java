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
package org.openhab.automation.jrule.rules;

/**
 * The {@link JRuleMemberOf}
 *
 * @author Robert Delbrück
 */
public enum JRuleMemberOf {
    /**
     * Not using memberOf
     */
    None,
    /**
     * Listen on all child items
     */
    All,
    /**
     * Just listen on child groups
     */
    Groups,
    /**
     * Just listen on concrete Items (without groups)
     */
    Items
}
