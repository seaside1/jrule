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
import java.time.temporal.ChronoUnit;

/**
 * The {@link JRuleDebounce} type.
 * Default value unit is Seconds.
 *
 * @author Robert Delbrück - Initial contribution
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface JRuleDebounce {
    /**
     * Default is seconds. Can be changed via unit().
     * 
     * @return value as long
     */
    long value() default 0;

    /**
     * Time unit. Default is seconds.
     * 
     * @return time unit.
     */
    ChronoUnit unit() default ChronoUnit.SECONDS;
}
