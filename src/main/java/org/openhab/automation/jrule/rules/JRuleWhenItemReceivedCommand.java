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

/**
 * The {@link JRuleWhenItemReceivedCommand}
 *
 * @author Robert Delbrück
 */
@Inherited
@Repeatable(JRuleWhenItemReceivedCommands.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface JRuleWhenItemReceivedCommand {
    String item() default "";

    String command() default "";

    JRuleMemberOf memberOf() default JRuleMemberOf.None;

    JRuleCondition condition() default @JRuleCondition;
}
