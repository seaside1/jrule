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
import org.openhab.core.items.events.GroupItemStateChangedEvent;
import org.openhab.core.items.events.ItemAddedEvent;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemRemovedEvent;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.openhab.core.items.events.ItemUpdatedEvent;
import org.openhab.core.thing.events.ChannelTriggeredEvent;
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

    public static final String PROPERTY_CHANNEL_EVENT = "CHANNEL_EVENT";

    private static final String LOG_NAME_SUBSCRIBER = "JRuleSubscriber";

    private final Logger logger = LoggerFactory.getLogger(JRuleEventSubscriber.class);

    private final Set<String> subscribedEventTypes = new HashSet<>();

    private final Set<String> jRuleMonitoredItems = new HashSet<>();

    private final Set<String> jRuleMonitoredChannels = new HashSet<>();

    private final PropertyChangeSupport propertyChangeSupport;

    private final Queue<Event> eventQueue = new ConcurrentLinkedQueue<>();

    private volatile boolean queueEvents = false;

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
    }

    @Override
    public Set<String> getSubscribedEventTypes() {
        return subscribedEventTypes;
    }

    @Override
    public @Nullable EventFilter getEventFilter() {
        return null;
    }

    public void addItemItemName(String name) {
        JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Adding subscripted item: {}", name);
        jRuleMonitoredItems.add(name);
    }

    public void startSubscriber() {
        JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "starting subscriber");
        JRuleEngine.get().getItemNames().forEach(this::addItemItemName);
        jRuleMonitoredChannels.addAll(JRuleEngine.get().getChannelNames());
        propertyChangeSupport.addPropertyChangeListener(JRuleEngine.get());
    }

    public void stopSubscriber() {
        jRuleMonitoredItems.clear();
        jRuleMonitoredChannels.clear();
        propertyChangeSupport.removePropertyChangeListener(JRuleEngine.get());
    }

    public void pauseEventSubscriber() {
        queueEvents = true;
    }

    public void resumeEvents() {
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
        final String itemFromTopic = JRuleUtil.getItemNameFromTopic(event.getTopic());
        if (event.getType().equals(ItemAddedEvent.TYPE) //
                || event.getType().equals(ItemRemovedEvent.TYPE) //
                || event.getType().equals(ItemUpdatedEvent.TYPE)) {
            JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "event processed as {}: topic {} payload: {}",
                    PROPERTY_ITEM_REGISTRY_EVENT, event.getTopic(), event.getPayload());
            propertyChangeSupport.firePropertyChange(PROPERTY_ITEM_REGISTRY_EVENT, null, event);
            return;
        }
        if (jRuleMonitoredItems.contains(itemFromTopic)) {
            JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Event processed as {}: topic {} payload: {}",
                    PROPERTY_ITEM_EVENT, event.getTopic(), event.getPayload());
            propertyChangeSupport.firePropertyChange(PROPERTY_ITEM_EVENT, null, event);
        }
        if (event.getType().equals(ChannelTriggeredEvent.TYPE)) {

            ChannelTriggeredEvent channelTriggeredEvent = (ChannelTriggeredEvent) event;
            String channel = channelTriggeredEvent.getChannel().toString();

            if (jRuleMonitoredChannels.contains(channel)) {
                JRuleLog.debug(logger, LOG_NAME_SUBSCRIBER, "Event processed as {}: topic {} payload: {}",
                        PROPERTY_CHANNEL_EVENT, event.getTopic(), event.getPayload());
                propertyChangeSupport.firePropertyChange(PROPERTY_CHANNEL_EVENT, null, event);
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
