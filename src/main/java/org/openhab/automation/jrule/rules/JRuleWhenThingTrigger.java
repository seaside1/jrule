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

import java.lang.annotation.*;

import org.openhab.automation.jrule.things.JRuleThingStatus;

/**
 * The {@link JRuleWhenThingTrigger}
 *
 * @author Robert Delbrück
 */
@Inherited
@Repeatable(JRuleWhenThingTriggers.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface JRuleWhenThingTrigger {
    String thing() default "";

    JRuleThingStatus from() default JRuleThingStatus.THING_UNKNOWN;

    JRuleThingStatus to() default JRuleThingStatus.THING_UNKNOWN;
}
