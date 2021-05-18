/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.jrule.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link JRuleBindingConstants} class defines common constants, which are
 * used across the Java Rule binding.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class JRuleBindingConstants {
    public static final String JRULE_GENERATION_PREFIX = "_";

    public static final String JAVA_FILE_TYPE = ".java";
    public static final String CLASS_FILE_TYPE = ".class";

    public static final String BINDING_ID = "jrule";
    public static final ThingTypeUID THING_TYPE_JRULE = new ThingTypeUID(BINDING_ID, "jruleengine");

    public static final String EMPTY = "";
}
