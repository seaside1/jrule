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
package org.openhab.automation.jrule.internal.engine.excutioncontext;

import java.util.Optional;

/**
 * The {@link JRulePreconditionContext}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class JRulePreconditionContext {

    private final String item;
    private final Optional<Double> lt;
    private final Optional<Double> lte;
    private final Optional<Double> gt;
    private final Optional<Double> gte;
    private final Optional<String> eq;
    private final Optional<String> neq;

    public JRulePreconditionContext(String item, Optional<Double> lt, Optional<Double> lte, Optional<Double> gt,
            Optional<Double> gte, Optional<String> eq, Optional<String> neq) {
        this.item = item;
        this.lt = lt;
        this.lte = lte;
        this.gt = gt;
        this.gte = gte;
        this.eq = eq;
        this.neq = neq;
    }

    public String getItem() {
        return item;
    }

    public Optional<Double> getLt() {
        return lt;
    }

    public Optional<Double> getLte() {
        return lte;
    }

    public Optional<Double> getGt() {
        return gt;
    }

    public Optional<Double> getGte() {
        return gte;
    }

    public Optional<String> getEq() {
        return eq;
    }

    public Optional<String> getNeq() {
        return neq;
    }

    @Override
    public String toString() {
        return "JRulePreconditionContext{" + "item='" + item + '\'' + ", lt=" + lt + ", lte=" + lte + ", gt=" + gt
                + ", gte=" + gte + ", eq=" + eq + ", neq=" + neq + '}';
    }
}
