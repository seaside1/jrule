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
package org.openhab.binding.jrule.internal.events;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.jrule.internal.handler.JRuleEngine;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFilter;
import org.openhab.core.events.EventSubscriber;
import org.openhab.core.items.events.ItemAddedEvent;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemRemovedEvent;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.openhab.core.items.events.ItemUpdatedEvent;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleEventSubscriber}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@Component(service = { EventSubscriber.class,
        JRuleEventSubscriber.class }, configurationPid = "binding.jrule.eventsubscriber")
@NonNullByDefault
public class JRuleEventSubscriber implements EventSubscriber {

    public static final String PROPERTY_ITEM_EVENT = "PROPERTY_ITEM_EVENT";

    public static final String PROPERTY_ITEM_REGISTRY_EVENT = "PROPERTY_ITEM_REGESTRY_EVENT";

    private final Logger logger = LoggerFactory.getLogger(JRuleEventSubscriber.class);

    private final Set<String> subscribedEventTypes = new HashSet<String>();

    private final Set<String> jRuleMonitoredItems = new HashSet<>();

    private final PropertyChangeSupport propertyChangeSupport;

    public JRuleEventSubscriber() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        subscribedEventTypes.add(ItemStateEvent.TYPE);
        subscribedEventTypes.add(ItemCommandEvent.TYPE);
        subscribedEventTypes.add(ItemStateChangedEvent.TYPE);
        subscribedEventTypes.add(ItemUpdatedEvent.TYPE);
        subscribedEventTypes.add(ItemAddedEvent.TYPE);
        subscribedEventTypes.add(ItemRemovedEvent.TYPE);
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
        logger.debug("Adding subscripted item: {}", name);
        jRuleMonitoredItems.add(name);
    }

    public void startSubscriper() {
        logger.debug("starting subscriper");
        JRuleEngine.get().getItemNames().stream().forEach(itemName -> addItemItemName(itemName));
        propertyChangeSupport.addPropertyChangeListener(JRuleEngine.get());
    }

    public void stopSubscriber() {
        jRuleMonitoredItems.clear();
        propertyChangeSupport.removePropertyChangeListener(JRuleEngine.get());
    }

    @Override
    public void receive(Event event) {
        final String itemFromTopic = getItemFromTopic(event.getTopic());
        if (event.getType().equals(ItemAddedEvent.TYPE) //
                || event.getType().equals(ItemRemovedEvent.TYPE) //
                || event.getType().equals(ItemUpdatedEvent.TYPE)) {
            propertyChangeSupport.firePropertyChange(PROPERTY_ITEM_REGISTRY_EVENT, null, event);
            return;
        }
        if (!jRuleMonitoredItems.contains(itemFromTopic)) {
            return;
        }
        propertyChangeSupport.firePropertyChange(PROPERTY_ITEM_EVENT, null, event);
        logger.debug("Got Event event: topic {} payload: {}", event.getTopic(), event.getPayload());
    }

    private String getItemFromTopic(String topic) {

        if (topic.isEmpty()) {
            // TODO: VAR
            return "";
        }
        final int start = topic.indexOf("items/") + "items/".length();
        final int end = topic.length();// topic.lastIndexOf("/");
        return end > 0 && end > start ? topic.substring(start, end) : "";
        // final String[] fragments = topic.split("//");
        // return (fragments.length > 0) ? fragments[fragments.length - 1] : "";
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        logger.debug("Adding listener for EventSubscriber");
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }
}
