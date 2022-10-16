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
package org.openhab.automation.jrule.internal.engine;

/**
 * The {@link JRuleLoadingStatistics} class holds rule loading statistics
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleLoadingStatistics {
    private int numChannelTriggers;
    private int numItemStateTriggers;
    private int numTimedTriggers;

    private int numThingTriggers;
    private int numRuleClasses;

    private int numRuleMethods;
    private final JRuleLoadingStatistics previous;

    public JRuleLoadingStatistics(JRuleLoadingStatistics previous) {
        this.previous = previous;
    }

    public void addChannelTrigger() {
        numChannelTriggers++;
    }

    public void addThingTrigger() {
        numThingTriggers++;
    }

    public void addItemStateTrigger() {
        numItemStateTriggers++;
    }

    public void addTimedTrigger() {
        numTimedTriggers++;
    }

    public void addRuleClass() {
        numRuleClasses++;
    }

    public void addRuleMethod() {
        numRuleMethods++;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("\n");
        b.append("********   ");
        b.append(String.format("Loaded %d rule classes, change %d", numRuleClasses,
                previous == null ? 0 : numRuleClasses - previous.numRuleClasses));

        b.append("\n");
        b.append("********   ");
        b.append(String.format("Loaded %d rule methods, change %d", numRuleMethods,
                previous == null ? 0 : numRuleMethods - previous.numRuleMethods));

        b.append("\n");
        b.append("********   ");
        b.append(String.format("Loaded %d item state triggers, change %d", numItemStateTriggers,
                previous == null ? 0 : numItemStateTriggers - previous.numItemStateTriggers));

        b.append("\n");
        b.append("********   ");
        b.append(String.format("Loaded %d channel triggers, change %d", numChannelTriggers,
                previous == null ? 0 : numChannelTriggers - previous.numChannelTriggers));

        b.append("\n");
        b.append("********   ");
        b.append(String.format("Loaded %d timed triggers, change %d", numTimedTriggers,
                previous == null ? 0 : numTimedTriggers - previous.numTimedTriggers));

        b.append("\n");
        b.append("********   ");
        b.append(String.format("Loaded %d thing triggers, change %d", numThingTriggers,
                previous == null ? 0 : numThingTriggers - previous.numThingTriggers));

        return b.toString();
    }
}
