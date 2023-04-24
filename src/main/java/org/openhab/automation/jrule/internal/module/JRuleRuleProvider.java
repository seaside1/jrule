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
package org.openhab.automation.jrule.internal.module;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.core.automation.Rule;
import org.openhab.core.automation.RuleProvider;
import org.openhab.core.automation.RuleStatus;
import org.openhab.core.automation.RuleStatusDetail;
import org.openhab.core.automation.RuleStatusInfo;
import org.openhab.core.automation.events.RuleStatusInfoEvent;
import org.openhab.core.common.registry.ProviderChangeListener;
import org.openhab.core.events.EventPublisher;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link JRuleRuleProvider} provides rules into openhab ecosystem
 *
 * @author Arne Seime - Initial contribution
 */
@NonNullByDefault
@Component(immediate = true, service = { JRuleRuleProvider.class, RuleProvider.class })
public class JRuleRuleProvider implements RuleProvider {

    public static final String MODULE_PREFIX = "jrule.";

    private final Collection<ProviderChangeListener<Rule>> listeners = new ArrayList<>();

    private final Map<String, JRuleModuleEntry> rules = new ConcurrentHashMap<>();

    @Nullable
    private EventPublisher eventPublisher;

    @Override
    public Collection<Rule> getAll() {
        return rules.values().stream().map(e -> (Rule) e).collect(Collectors.toList());
    }

    @Override
    public void addProviderChangeListener(ProviderChangeListener<Rule> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeProviderChangeListener(ProviderChangeListener<Rule> listener) {
        listeners.remove(listener);
    }

    @Reference
    public void setEventPublisher(final EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Deactivate
    protected void deactivate() {
        rules.clear();
    }

    public void reset() {
        for (JRuleModuleEntry rule : rules.values()) {
            listeners.stream().forEach(e -> e.removed(this, rule));
            rule.dispose();
        }
        rules.clear();
    }

    public void add(JRuleModuleEntry entry) {
        rules.put(entry.getUID(), entry);
        listeners.stream().forEach(e -> e.added(this, entry));
    }

    @Nullable
    public JRuleModuleEntry getRule(String ruleUid) {
        return rules.get(ruleUid);
    }

    public void runRule(JRule rule, Method method) {
        RuleStatusInfoEvent ruleStatusInfoEvent = JRuleEventFactory.createRuleStatusInfoEvent(
                new RuleStatusInfo(RuleStatus.RUNNING, RuleStatusDetail.NONE, null),
                JRuleModuleEntry.createUid(rule, method), "jRule");
        eventPublisher.post(ruleStatusInfoEvent);
    }

    public void stopRule(JRule rule, Method method) {
        RuleStatusInfoEvent ruleStatusInfoEvent = JRuleEventFactory.createRuleStatusInfoEvent(
                new RuleStatusInfo(RuleStatus.IDLE, RuleStatusDetail.NONE, null),
                JRuleModuleEntry.createUid(rule, method), "jRule");
        eventPublisher.post(ruleStatusInfoEvent);
    }
}
