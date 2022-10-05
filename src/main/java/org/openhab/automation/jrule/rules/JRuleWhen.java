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
 * The {@link JRuleWhen}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@Repeatable(JRuleWhens.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface JRuleWhen {
    String cron() default "";

    int hours() default -1;

    int minutes() default -1;

    int seconds() default -1;

    String item() default "";

    String channel() default "";

    String event() default "";

    String trigger() default "";

    String update() default "";

    String from() default "";

    String to() default "";

    double gt() default Double.MIN_VALUE;

    double lt() default Double.MIN_VALUE;

    double gte() default Double.MIN_VALUE;

    double lte() default Double.MIN_VALUE;

    String eq() default "";

    String neq() default "";
}
