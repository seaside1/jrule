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

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.rules.JRuleWhenChannelTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedUpdate;
import org.openhab.automation.jrule.rules.JRuleWhenStartup;
import org.openhab.automation.jrule.rules.JRuleWhenThingTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenTimeTrigger;
import org.openhab.core.automation.type.ModuleType;
import org.openhab.core.automation.type.ModuleTypeProvider;
import org.openhab.core.common.registry.ProviderChangeListener;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author Arne Seime - Initial Contribution
 */
@Component(immediate = true, service = { ModuleTypeProvider.class })
@NonNullByDefault
public class JRuleModuleTypeProvider implements ModuleTypeProvider {
    private static final Map<String, ModuleType> PROVIDED_MODULE_TYPES = Map.of(
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedCommand.class),
            new JRuleTriggerType(JRuleWhenItemReceivedCommand.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemChange.class),
            new JRuleTriggerType(JRuleWhenItemChange.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedUpdate.class),
            new JRuleTriggerType(JRuleWhenItemReceivedUpdate.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenChannelTrigger.class),
            new JRuleTriggerType(JRuleWhenChannelTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenCronTrigger.class),
            new JRuleTriggerType(JRuleWhenCronTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenTimeTrigger.class),
            new JRuleTriggerType(JRuleWhenTimeTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenThingTrigger.class),
            new JRuleTriggerType(JRuleWhenThingTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenStartup.class), new JRuleTriggerType(JRuleWhenStartup.class));

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ModuleType> T getModuleType(@Nullable String UID, @Nullable Locale locale) {
        return (T) PROVIDED_MODULE_TYPES.get(UID);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ModuleType> Collection<T> getModuleTypes(@Nullable Locale locale) {
        return (Collection<T>) PROVIDED_MODULE_TYPES.values();
    }

    @Override
    public void addProviderChangeListener(ProviderChangeListener<ModuleType> listener) {
        // does nothing because this provider does not change
    }

    @Override
    public Collection<ModuleType> getAll() {
        return Collections.unmodifiableCollection(PROVIDED_MODULE_TYPES.values());
    }

    @Override
    public void removeProviderChangeListener(ProviderChangeListener<ModuleType> listener) {
        // does nothing because this provider does not change
    }
}
