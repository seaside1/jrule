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
package org.openhab.automation.jrule.internal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRuleItemChangeExecutionContext;
import org.openhab.automation.jrule.internal.engine.excutioncontext.JRulePreconditionContext;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 * The {@link JRuleEngineTest}
 *
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleEngineTest {

    public static final String ITEMNAME = "item";

    private static Stream<Arguments> lessThanStates() {
        return Stream.of(Arguments.of(QuantityType.ZERO, true), Arguments.of(QuantityType.ONE, false),
                Arguments.of(DecimalType.ZERO, true), Arguments.of(DecimalType.valueOf("1"), false),
                Arguments.of(PercentType.valueOf("10"), false), Arguments.of(PercentType.valueOf("0.1"), true),
                Arguments.of(QuantityType.valueOf("0.1 W"), true), Arguments.of(StringType.valueOf("CAKE"), false),
                Arguments.of(UnDefType.UNDEF, false));
    }

    @ParameterizedTest
    @MethodSource("lessThanStates")
    void testMatchLessThanPreCondition(State state, boolean shouldMatch) throws ItemNotFoundException {

        NumberItem item = new NumberItem(ITEMNAME);
        item.setState(state);

        ItemRegistry itemRegistry = Mockito.mock(ItemRegistry.class);
        when(itemRegistry.getItem(ITEMNAME)).thenReturn(item);

        JRuleEngine engine = JRuleEngine.get();
        engine.setItemRegistry(itemRegistry);

        JRulePreconditionContext lessThanCondition = new JRulePreconditionContext(ITEMNAME, Optional.of(1d),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        JRuleExecutionContext ex = new JRuleItemChangeExecutionContext("uid", "logname", null, null, ITEMNAME, null,
                Optional.empty(), Optional.empty(), List.of(lessThanCondition), Optional.empty(), Optional.empty(),
                null, null);

        assertEquals(shouldMatch, engine.matchPrecondition(ex));
    }
}
