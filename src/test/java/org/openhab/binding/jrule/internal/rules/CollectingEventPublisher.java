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
package org.openhab.binding.jrule.internal.rules;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemStateEvent;

/**
 * CollectingEventPublisher collects all item events sent from rules to the event bus (sendCommand/postUpdate).
 * It further has methods to check that events to specific items has been sent.
 *
 * @author Arne Seime - Initial contribution
 */
public class CollectingEventPublisher implements EventPublisher {

    private final List<Container> events = new ArrayList<>();

    @Override
    public void post(Event event) throws IllegalArgumentException, IllegalStateException {
        events.add(new Container(ZonedDateTime.now(), event));
    }

    public boolean hasCommandEvent(String itemName, Object command) {
        return events.stream().filter(e -> e.event instanceof ItemCommandEvent).map(e -> (ItemCommandEvent) e.event)
                .anyMatch(e -> e.getTopic().equals(createTopic(itemName, "command"))
                        && e.getItemCommand().toString().equals(command.toString()));
    }

    public boolean hasUpdateEvent(String itemName, Object update) {
        return events.stream().filter(e -> e.event instanceof ItemStateEvent).map(e -> (ItemStateEvent) e.event)
                .anyMatch(e -> e.getTopic().equals(createTopic(itemName, "state"))
                        && e.getItemState().toString().equals(update.toString()));
    }

    private String createTopic(String itemName, String type) {
        return "openhab/items/" + itemName + "/" + type;
    }

    public boolean isLastCommandEvent(String itemName, Object command) {
        return events.stream().filter(e -> e.event instanceof ItemCommandEvent)
                .filter(container -> container.event.getTopic().equals(createTopic(itemName, "command")))
                .reduce((container, container2) -> {
                    if (container.time.isAfter(container2.time)) {
                        return container;
                    } else {
                        return container2;
                    }
                }).filter(container -> ((ItemCommandEvent) container.event).getItemCommand().toString()
                        .equals(command.toString()))
                .isPresent();
    }

    final class Container {
        private ZonedDateTime time;
        private Event event;

        public Container(ZonedDateTime time, Event event) {
            this.time = time;
            this.event = event;
        }
    }
}
