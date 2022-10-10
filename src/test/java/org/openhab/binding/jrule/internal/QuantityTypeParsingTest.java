package org.openhab.binding.jrule.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SIUnits;

public class QuantityTypeParsingTest {

    @Test
    public void testParseQuantityType() {
        assertEquals(10, new QuantityType<>(10, SIUnits.CELSIUS).doubleValue());
        assertEquals(10, QuantityType.valueOf("10").doubleValue());
    }
}
