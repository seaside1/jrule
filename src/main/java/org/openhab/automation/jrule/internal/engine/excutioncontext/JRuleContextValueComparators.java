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
package org.openhab.automation.jrule.internal.engine.excutioncontext;

/**
 * The {@link JRuleContextValueComparators} interface defines the common comparison methods (less than, greater than
 * etc)
 *
 * @author Arne Seime - Initial contribution
 */
public interface JRuleContextValueComparators {

    public Double getGt();

    public Double getGte();

    public Double getLt();

    public Double getLte();

    public String getEq();

    public String getNeq();

    default boolean hasCompartorsSet() {
        return getGt() != null || getGte() != null || getLt() != null || getLte() != null || getEq() != null
                || getNeq() != null;
    }
}
