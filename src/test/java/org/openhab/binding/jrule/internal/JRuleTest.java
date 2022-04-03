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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.model.core.ModelRepository;
import org.openhab.core.thing.ThingRegistry;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * The {@link JRuleTest}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class JRuleTest {

    private @NonNullByDefault({}) ModelRepository modelRepository;
    private @NonNullByDefault({}) ThingRegistry thingRegistry;
    private @NonNullByDefault({}) ItemRegistry itemRegistry;
    private static final String ITEMS_TESTMODEL_NAME = "test.items";
    private @Nullable BundleContext bundleContext;

    @BeforeEach
    public void setUp() throws Exception {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ALL);

        // bundleContext = getBundleContext();
        // modelRepository = getService(ModelRepository.class);
        // itemRegistry = getService(ItemRegistry.class);
        // // final ServiceReference serviceReference =
        // bundleContext.getServiceReference(ModelRepository.class.getName());
        // bundleContext.getService(serviceReference);

        // GenericItemProvider provider = new GenericItemProvider(modelRepository, genericMetadataProvider, properties);
        // = getService(ModelRepository.class);
    }
}
// Log info started completed
// Write readme
// Write tutorial
