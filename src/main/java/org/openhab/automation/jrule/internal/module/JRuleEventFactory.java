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
package org.openhab.automation.jrule.internal.module;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.automation.RuleStatusInfo;
import org.openhab.core.automation.events.RuleStatusInfoEvent;
import org.openhab.core.events.AbstractEventFactory;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cloned from openhab Core
 *
 * @author Robert Delbrück - Initial contribution
 * @see org.openhab.core.automation.internal.RuleEventFactory
 */
@NonNullByDefault
@Component(service = EventFactory.class, immediate = true)
public class JRuleEventFactory extends AbstractEventFactory {

    private final Logger logger = LoggerFactory.getLogger(JRuleEventFactory.class);

    private static final String RULE_STATE_EVENT_TOPIC = "openhab/rules/{ruleID}/state";

    private static final Set<String> SUPPORTED_TYPES = new HashSet<>();

    static {
        SUPPORTED_TYPES.add(RuleStatusInfoEvent.TYPE);
    }

    public JRuleEventFactory() {
        super(SUPPORTED_TYPES);
    }

    @Override
    protected Event createEventByType(String eventType, String topic, String payload, @Nullable String source) {
        logger.trace("creating ruleEvent of type: {}", eventType);
        if (RuleStatusInfoEvent.TYPE.equals(eventType)) {
            return createRuleStatusInfoEvent(topic, payload, source);
        }
        throw new IllegalArgumentException("The event type '" + eventType + "' is not supported by this factory.");
    }

    @SuppressWarnings("null")
    private Event createRuleStatusInfoEvent(String topic, String payload, @Nullable String source) {
        RuleStatusInfo statusInfo = deserializePayload(payload, RuleStatusInfo.class);
        return new RuleStatusInfoEvent(topic, payload, source, statusInfo, getRuleId(topic));
    }

    private String getRuleId(String topic) {
        String[] topicElements = getTopicElements(topic);
        if (topicElements.length != 4) {
            throw new IllegalArgumentException("Event creation failed, invalid topic: " + topic);
        }
        return topicElements[2];
    }

    /**
     * Creates a rule status info event.
     *
     * @param statusInfo the status info of the event.
     * @param ruleUID the UID of the rule for which the event is created.
     * @param source the source of the event.
     * @return {@link RuleStatusInfoEvent} instance.
     */
    public static RuleStatusInfoEvent createRuleStatusInfoEvent(RuleStatusInfo statusInfo, String ruleUID,
            String source) {
        String topic = buildTopic(RULE_STATE_EVENT_TOPIC, ruleUID);
        String payload = serializePayload(statusInfo);
        return new RuleStatusInfoEvent(topic, payload, source, statusInfo, ruleUID);
    }

    private static String buildTopic(String topic, String ruleUID) {
        return topic.replace("{ruleID}", ruleUID);
    }
}
