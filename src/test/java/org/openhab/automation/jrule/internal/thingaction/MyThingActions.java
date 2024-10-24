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
package org.openhab.automation.jrule.internal.thingaction;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.core.automation.annotation.ActionInput;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;

/**
 * The {@link MyThingActions}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class MyThingActions implements ThingActions {
    @RuleAction(label = "doWhatWithoutParams")
    public void doWhatWithoutParams() {
    }

    @RuleAction(label = "sendData")
    public QuantityType sendData(@ActionInput(name = "value") String value) {
        return QuantityType.ONE;
    }

    @RuleAction(label = "doSomething")
    public int doSomething(@ActionInput(name = "value") String value, @ActionInput(name = "blub") int blub,
            @ActionInput(name = "blabla") Float blabla) {
        return 0;
    }

    @RuleAction(label = "noActionInput")
    public int noActionInput(String value) {
        return 0;
    }

    @RuleAction(label = "doSomethingAbstract")
    public int doSomethingAbstract(@ActionInput(name = "value") Command value) {
        return 10;
    }

    @RuleAction(label = "doWithVarArgs")
    public int doWithVarArgs(@ActionInput(name = "value") String value, String... args) {
        return 15;
    }

    @Override
    public void setThingHandler(ThingHandler handler) {
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return null;
    }

    @RuleAction(label = "returnObjectOnUnsupportedClass")
    public JRule returnObjectOnUnsupportedClass(@ActionInput(name = "value") JRule value) {
        return value;
    }
}
