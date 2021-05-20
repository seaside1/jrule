/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.internal.handler.JRuleHandler;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.voice.VoiceManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link JRuleFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@Component(configurationPid = "automation.jrule")
@NonNullByDefault
public class JRuleFactory {

    private final ItemRegistry itemRegistry;
    private final JRuleEventSubscriber eventSubscriber;
    private final EventPublisher eventPublisher;
    private final VoiceManager voiceManager;
    private final JRuleHandler jRuleHandler;

    @Activate
    public JRuleFactory(Map<String, Object> properties, final @Reference JRuleEventSubscriber eventSubscriber,
            final @Reference ItemRegistry itemRegistry, final @Reference EventPublisher eventPublisher,
            final @Reference VoiceManager voiceManager) {
        this.itemRegistry = itemRegistry;
        this.eventSubscriber = eventSubscriber;
        this.eventPublisher = eventPublisher;
        this.voiceManager = voiceManager;
        jRuleHandler = new JRuleHandler(properties, itemRegistry, eventPublisher, eventSubscriber, voiceManager);
        jRuleHandler.initialize();
    }

    @Deactivate
    public void dispose() {
        jRuleHandler.dispose();
    }
}
