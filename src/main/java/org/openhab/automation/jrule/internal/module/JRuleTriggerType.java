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
package org.openhab.automation.jrule.internal.module;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.automation.Visibility;
import org.openhab.core.automation.type.TriggerType;

/**
 *
 * @author Arne Seime - Initial Contribution
 */
@NonNullByDefault
public class JRuleTriggerType extends TriggerType {

    public JRuleTriggerType(Class annotation) {
        super(JRuleModuleUtil.toTriggerModuleUID(annotation), List.of(), "@" + annotation.getSimpleName(), null, null,
                Visibility.VISIBLE, List.of());
    }
}
