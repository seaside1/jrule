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
package org.openhab.automation.jrule.internal.handler;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.automation.Rule;
import org.openhab.core.automation.RuleExecution;
import org.openhab.core.automation.RuleManager;
import org.openhab.core.automation.RuleProvider;
import org.openhab.core.automation.RuleStatus;
import org.openhab.core.automation.RuleStatusInfo;
import org.openhab.core.common.registry.ProviderChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * The {@link JRuleThingHandler} provides access to thing actions
 *
 * @author Arne Seime - Initial contribution
 */
@NonNullByDefault
@Component(immediate = true, service = { JRuleRuleProvider.class, RuleProvider.class })
public class JRuleRuleProvider implements RuleProvider, RuleManager {

    private final Collection<ProviderChangeListener<Rule>> listeners = new ArrayList<>();

    private final Map<String, Rule> rules = new ConcurrentHashMap<>();

    @Override
    public Collection<Rule> getAll() {
        return rules.values();
    }

    @Override
    public void addProviderChangeListener(ProviderChangeListener<Rule> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeProviderChangeListener(ProviderChangeListener<Rule> listener) {
        listeners.remove(listener);
    }

    @Deactivate
    protected void deactivate() {
        rules.clear();
    }

    public void reset() {
        for (Rule rule : rules.values()) {
            listeners.stream().forEach(e -> e.removed(this, rule));
        }
        rules.clear();
    }

    public void add(JRuleEntry entry) {
        rules.put(entry.getUID(), entry);
        listeners.stream().forEach(e -> e.added(this, entry));
    }

    @Override
    public @Nullable Boolean isEnabled(String s) {
        return null;
    }

    @Override
    public void setEnabled(String s, boolean b) {
        System.out.println("Enable rule " + b);
    }

    @Override
    public @Nullable RuleStatusInfo getStatusInfo(String s) {
        return null;
    }

    @Override
    public @Nullable RuleStatus getStatus(String s) {
        return null;
    }

    @Override
    public Map<String, Object> runNow(String s) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Map<String, Object> runNow(String s, boolean b, @Nullable Map<String, Object> map) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Stream<RuleExecution> simulateRuleExecutions(ZonedDateTime zonedDateTime, ZonedDateTime zonedDateTime1) {
        return Stream.of();
    }
}
