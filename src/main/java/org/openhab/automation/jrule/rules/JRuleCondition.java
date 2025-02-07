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

import java.lang.annotation.Inherited;

/**
 * The {@link JRuleCondition}
 *
 * @author Robert Delbrück
 */
@Inherited
public @interface JRuleCondition {
    double gt() default Double.MIN_VALUE;

    double lt() default Double.MIN_VALUE;

    double gte() default Double.MIN_VALUE;

    double lte() default Double.MIN_VALUE;

    String eq() default "";

    String neq() default "";
}
