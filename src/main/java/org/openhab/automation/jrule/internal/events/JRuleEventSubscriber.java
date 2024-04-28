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
package org.openhab.automation.jrule.internal.events;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFilter;
import org.openhab.core.events.EventSubscriber;
import org.openhab.core.events.system.StartlevelEvent;
import org.openhab.core.items.events.GroupItemStateChangedEvent;
import org.openhab.core.items.events.ItemAddedEvent;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemRemovedEvent;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.openhab.core.items.events.ItemUpdatedEvent;
import org.openhab.core.thing.events.ChannelTriggeredEvent;
import org.openhab.core.thing.events.ThingAddedEvent;
import org.openhab.core.thing.events.ThingRemovedEvent;
import org.openhab.core.thing.events.ThingStatusInfoChangedEvent;
import org.openhab.core.thing.events.ThingUpdatedEvent;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEventSubscriber}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@Component(service = { EventSubscriber.class,
        JRuleEventSubscriber.class }, configurationPid = "automation.jrule.eventsubscriber")
@NonNullByDefault
public class JRuleEventSubscriber implements EventSubscriber {

    public static final String PROPERTY_ITEM_EVENT = "PROPERTY_ITEM_EVENT";

    public static final String PROPERTY_ITEM_REGISTRY_EVENT = "PROPERTY_ITEM_REGISTRY_EVENT";
    public static final String PROPERTY_THING_REGISTRY_EVENT = "PROPERTY_THING_REGISTRY_EVENT";

    public static final String PROPERTY_CHANNEL_EVENT = "CHANNEL_EVENT";

    public static final String PROPERTY_THING_STATUS_EVENT = "THING_STATUS_EVENT";

    private static final String LOG_NAME_SUBSCRIBER = "JRuleSubscriber";
    public static final String PROPERTY_STARTUP_EVENT = "STARTUP_EVENT";
    // status changes

    private final Logger logger = LoggerFactory.getLogger(JRuleEventSubscriber.class);

    private final Set<String> subscribedEventTypes = new HashSet<>();

    private final PropertyChangeSupport propertyChangeSupport;

    private final Queue<Event> eventQueue = new ConcurrentLinkedQueue<>();

    private volatile boolean queueEvents = false;
    private JRuleEngine jRuleEngine = JRuleEngine.get();

    public JRuleEventSubscriber() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        subscribedEventTypes.add(GroupItemStateChangedEvent.TYPE);
        subscribedEventTypes.add(ItemStateEvent.TYPE);
        subscribedEventTypes.add(ItemCommandEvent.TYPE);
        subscribedEventTypes.add(ItemStateChangedEvent.TYPE);
        subscribedEventTypes.add(ItemUpdatedEvent.TYPE);
        subscribedEventTypes.add(ItemAddedEvent.TYPE);
        subscribedEventTypes.add(ItemRemovedEvent.TYPE);
        subscribedEventTypes.add(ChannelTriggeredEvent.TYPE);
        subscribedEventTypes.add(ThingAddedEvent.TYPE);
        // TODO disabled for now. ThingDTO is missing hashCode+equals, so it is not possible (easily) to determine if
        // thing is really changed of if a binding just has called updateThing() with no new data
        // subscribedEventTypes.add(ThingUpdatedEvent.TYPE);
        subscribedEventTypes.add(ThingRemovedEvent.TYPE);
        subscribedEventTypes.add(ThingStatusInfoChangedEvent.TYPE);
        subscribedEventTypes.add(StartlevelEvent.TYPE);
    }

    @Override
    public Set<String> getSubscribedEventTypes() {
        return subscribedEventTypes;
    }

    @Override
    public @Nullable EventFilter getEventFilter() {
        return null;
    }

    public void startSubscriber() {
        JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Starting subscriber");
        propertyChangeSupport.addPropertyChangeListener(JRuleEngine.get());
    }

    public void stopSubscriber() {
        propertyChangeSupport.removePropertyChangeListener(JRuleEngine.get());
        propertyChangeSupport.removePropertyChangeListener(jRuleEngine);
    }

    /**
     * Queue events until JRule is ready to process them. See {resumeEventDelivery}
     */
    public void pauseEventDelivery() {
        queueEvents = true;
    }

    public void resumeEventDelivery() {
        while (true) {
            Event event = eventQueue.poll();
            if (event == null) {
                JRuleLog.info(logger, LOG_NAME_SUBSCRIBER, "Event queue empty");
                queueEvents = false;
                break;
            }
            try {
                JRuleLog.info(logger, LOG_NAME_SUBSCRIBER, "Delivering previously queued event {}", event);
                processEvent(event);
            } catch (Exception e) {
                JRuleLog.warn(logger, LOG_NAME_SUBSCRIBER, "Error processing queued event, discarding: {}", event);
            }
        }
    }

    @Override
    public void receive(Event event) {
        if (queueEvents) {
            JRuleLog.info(logger, LOG_NAME_SUBSCRIBER, "Event processing paused, queueing event {}", event);
            eventQueue.add(event);
        } else {
            processEvent(event);
        }
    }

    private void processEvent(Event event) {
        JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Received event '{}' with topic '{}' and payload '{}'",
                event.getType(), event.getTopic(), event.getPayload());
        if (event.getType().equals(ItemAddedEvent.TYPE) //
                || event.getType().equals(ItemRemovedEvent.TYPE) //
                || event.getType().equals(ItemUpdatedEvent.TYPE)) {
            JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "event processed as {}: topic {} payload: {}",
                    PROPERTY_ITEM_REGISTRY_EVENT, event.getTopic(), event.getPayload());
            propertyChangeSupport.firePropertyChange(PROPERTY_ITEM_REGISTRY_EVENT, null, event);
        } else if (event.getType().equals(ThingAddedEvent.TYPE) || event.getType().equals(ThingRemovedEvent.TYPE)
                || event.getType().equals(ThingUpdatedEvent.TYPE)) {
            JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "event processed as {}: topic {} payload: {}",
                    PROPERTY_THING_REGISTRY_EVENT, event.getTopic(), event.getPayload());
            propertyChangeSupport.firePropertyChange(PROPERTY_THING_REGISTRY_EVENT, null, event);
        } else if (event.getType().equals(ItemStateEvent.TYPE) || event.getType().equals(ItemCommandEvent.TYPE)
                || event.getType().equals(ItemStateChangedEvent.TYPE)
                || event.getType().equals(GroupItemStateChangedEvent.TYPE)) {
            final String itemFromTopic = JRuleUtil.getItemNameFromTopic(event.getTopic());
            if (jRuleEngine.watchingForItem(itemFromTopic)) {
                JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Event processed as {}: topic {} payload: {}",
                        PROPERTY_ITEM_EVENT, event.getTopic(), event.getPayload());
                propertyChangeSupport.firePropertyChange(PROPERTY_ITEM_EVENT, null, event);
            }
        } else if (event.getType().equals(ChannelTriggeredEvent.TYPE)) {
            ChannelTriggeredEvent channelTriggeredEvent = (ChannelTriggeredEvent) event;
            String channel = channelTriggeredEvent.getChannel().toString();
            if (jRuleEngine.watchingForChannel(channel)) {
                JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Event processed as {}: topic {} payload: {}",
                        PROPERTY_CHANNEL_EVENT, event.getTopic(), event.getPayload());
                propertyChangeSupport.firePropertyChange(PROPERTY_CHANNEL_EVENT, null, event);
            }
        } else if (event.getType().equals(ThingStatusInfoChangedEvent.TYPE)) {
            ThingStatusInfoChangedEvent thingStatusChangedEvent = (ThingStatusInfoChangedEvent) event;
            String thingUID = thingStatusChangedEvent.getThingUID().toString();

            if (jRuleEngine.watchingForThing(thingUID)) {
                JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Event processed as {}: topic {} payload: {}",
                        PROPERTY_THING_STATUS_EVENT, event.getTopic(), event.getPayload());
                propertyChangeSupport.firePropertyChange(PROPERTY_THING_STATUS_EVENT, null, event);
            }
        } else if (event.getType().equals(StartlevelEvent.TYPE)) {
            StartlevelEvent startlevelEvent = (StartlevelEvent) event;
            if (jRuleEngine.watchingForStartlevel(startlevelEvent.getStartlevel())) {
                JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Event processed as {}: topic {} payload: {}",
                        PROPERTY_STARTUP_EVENT, event.getTopic(), event.getPayload());
                propertyChangeSupport.firePropertyChange(PROPERTY_STARTUP_EVENT, null, event);
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Adding listener for EventSubscriber");
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }
}
