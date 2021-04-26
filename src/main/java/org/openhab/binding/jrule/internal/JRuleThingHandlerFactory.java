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
package org.openhab.binding.jrule.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.binding.jrule.internal.handler.JRuleThingHandler;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link JRuleThingHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.jrule")
@NonNullByDefault
public class JRuleThingHandlerFactory extends BaseThingHandlerFactory {

    private final ItemRegistry itemRegistry;
    private final JRuleEventSubscriber eventSubscriber;
    private final EventPublisher eventPublisher;

    @Activate
    public JRuleThingHandlerFactory(final @Reference JRuleEventSubscriber eventSubscriber,
            final @Reference ItemRegistry itemRegistry, final @Reference EventPublisher eventPublisher) {
        this.itemRegistry = itemRegistry;
        this.eventSubscriber = eventSubscriber;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return JRuleThingHandler.supportsThingType(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        if (JRuleThingHandler.supportsThingType(thingTypeUID)) {
            return new JRuleThingHandler(thing, itemRegistry, eventPublisher, eventSubscriber);
        }
        return null;
    }
}
