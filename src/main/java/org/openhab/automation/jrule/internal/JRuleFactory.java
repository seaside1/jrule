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
package org.openhab.automation.jrule.internal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.internal.handler.JRuleHandler;
import org.openhab.automation.jrule.internal.module.JRuleRuleProvider;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.core.audio.AudioHTTPServer;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.net.NetworkAddressService;
import org.openhab.core.persistence.PersistenceServiceRegistry;
import org.openhab.core.scheduler.CronScheduler;
import org.openhab.core.thing.ThingManager;
import org.openhab.core.thing.ThingRegistry;
import org.openhab.core.thing.link.ItemChannelLinkRegistry;
import org.openhab.core.voice.VoiceManager;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@Component(configurationPid = "automation.jrule")
@NonNullByDefault
public class JRuleFactory {

    private final JRuleHandler jRuleHandler;
    private final JRuleEngine jRuleEngine;

    private static final Logger logger = LoggerFactory.getLogger(JRuleFactory.class);

    private static final String LOG_NAME_FACTORY = "JRuleFactory";

    private final JRuleDelayedDebouncingExecutor delayedInit = new JRuleDelayedDebouncingExecutor(5, TimeUnit.SECONDS);

    @Activate
    public JRuleFactory(Map<String, Object> properties, final @Reference JRuleEventSubscriber eventSubscriber,
            final @Reference ItemRegistry itemRegistry,
            final @Reference ItemChannelLinkRegistry itemChannelLinkRegistry,
            final @Reference ThingRegistry thingRegistry, final @Reference ThingManager thingManager,
            final @Reference EventPublisher eventPublisher, final @Reference VoiceManager voiceManager,
            final @Reference AudioHTTPServer audioHTTPServer,
            final @Reference NetworkAddressService networkAddressService, final ComponentContext componentContext,
            final @Reference CronScheduler cronScheduler, final @Reference MetadataRegistry metadataRegistry,
            final @Reference JRuleRuleProvider ruleProvider,
            @Reference final PersistenceServiceRegistry persistenceServiceRegistry) {
        JRuleConfig config = new JRuleConfig(properties);
        config.initConfig();
        jRuleEngine = JRuleEngine.get();
        jRuleEngine.setPersistenceServiceRegistry(persistenceServiceRegistry);
        jRuleEngine.setConfig(config);
        jRuleEngine.setItemRegistry(itemRegistry);
        jRuleEngine.setCronScheduler(cronScheduler);
        jRuleEngine.setRuleProvider(ruleProvider);

        jRuleEngine.initialize();
        JRuleItemRegistry.setMetadataRegistry(metadataRegistry);
        jRuleHandler = new JRuleHandler(config, itemRegistry, itemChannelLinkRegistry, thingRegistry, thingManager,
                eventPublisher, eventSubscriber, voiceManager, audioHTTPServer, networkAddressService, cronScheduler,
                componentContext.getBundleContext(), metadataRegistry);
        delayedInit.call(this::init);
    }

    @Nullable
    private Boolean init() {
        JRuleLog.info(logger, LOG_NAME_FACTORY, "Initializing Java Rules Engine v{}", getBundleVersion());
        jRuleHandler.initialize();
        return Boolean.TRUE;
    }

    private String getBundleVersion() {
        return FrameworkUtil.getBundle(JRuleHandler.class).getVersion().toString();
    }

    @Deactivate
    public synchronized void dispose() {
        delayedInit.cancel();
        jRuleHandler.dispose();
    }
}
