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
package org.openhab.automation.jrule.rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link JRuleWhenItemReceivedCommand}
 *
 * @author Robert Delbrück
 */
@Repeatable(JRuleWhenItemReceivedCommands.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface JRuleWhenItemReceivedCommand {
    String item() default "";

    String command() default "";

    JRuleMemberOf memberOf() default JRuleMemberOf.None;

    JRuleCondition condition() default @JRuleCondition;
}
