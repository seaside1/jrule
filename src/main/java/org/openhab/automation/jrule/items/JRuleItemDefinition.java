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
package org.openhab.automation.jrule.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link JRuleItemDefinition} carries an item's registry data on the matching field of the
 * generated JRuleItemNames class, so that rules and tests can read it by reflection instead of
 * parsing the generated javadoc.
 *
 * @author Robert Delbrück - Initial contribution
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface JRuleItemDefinition {
    String name();

    String type();

    String[] groupNames();
}
