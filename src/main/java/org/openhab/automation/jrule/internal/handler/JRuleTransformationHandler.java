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
package org.openhab.automation.jrule.internal.handler;

import org.openhab.automation.jrule.JRuleExecutionException;
import org.openhab.core.transform.TransformationException;
import org.openhab.core.transform.TransformationHelper;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleTransformationHandler} is responsible for handling transformation requests
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleTransformationHandler {

    private static volatile JRuleTransformationHandler instance;

    private BundleContext bundleContext;

    private final Logger logger = LoggerFactory.getLogger(JRuleTransformationHandler.class);

    private JRuleTransformationHandler() {
    }

    public static JRuleTransformationHandler get() {
        if (instance == null) {
            synchronized (JRuleTransformationHandler.class) {
                if (instance == null) {
                    instance = new JRuleTransformationHandler();
                }
            }
        }
        return instance;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public String transform(String stateDescPattern, String state) throws JRuleExecutionException {
        try {
            return TransformationHelper.transform(bundleContext, stateDescPattern, state);
        } catch (TransformationException e) {
            throw new JRuleExecutionException(
                    String.format("Transformation of %s using %s failed: %s", state, stateDescPattern, e));
        }
    }
}
