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
package org.openhab.binding.jrule.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SIUnits;

/**
 * The {@link QuantityTypeParsingTest}
 *
 * @author Arne Seime - Initial contribution
 */
public class QuantityTypeParsingTest {

    @Test
    public void testParseQuantityType() {
        assertEquals(10, new QuantityType<>(10, SIUnits.CELSIUS).doubleValue());
        assertEquals(10, QuantityType.valueOf("10").doubleValue());

        assertThrows(NumberFormatException.class, () -> QuantityType.valueOf("UNDEF"));
        assertThrows(NumberFormatException.class, () -> Double.parseDouble("UNDEF"));
    }
}
