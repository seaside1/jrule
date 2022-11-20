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
package org.openhab.automation.jrule.rules.value;

import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRulePercentValue}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRulePercentValue extends JRuleDecimalValue {
    public JRulePercentValue(int value) {
        super(value);
    }

    public JRulePercentValue(double value) {
        super((int) Math.round(value + 0.5));
    }

    public JRulePercentValue(String value) {
        super(value);
    }

    @Override
    public Command toOhCommand() {
        return new PercentType(this.getValue());
    }

    @Override
    public State toOhState() {
        return new PercentType(this.getValue());
    }
}
