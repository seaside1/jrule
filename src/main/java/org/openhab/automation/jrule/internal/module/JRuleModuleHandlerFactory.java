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
import java.util.Set;

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
import org.openhab.core.automation.Module;
import org.openhab.core.automation.Trigger;
import org.openhab.core.automation.handler.BaseModuleHandlerFactory;
import org.openhab.core.automation.handler.ModuleHandler;
import org.openhab.core.automation.handler.ModuleHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 *
 * @author Arne Seime - Initial Contribution
 */
@Component(service = ModuleHandlerFactory.class, configurationPid = "automation.jrule")
@NonNullByDefault
public class JRuleModuleHandlerFactory extends BaseModuleHandlerFactory {
    private static final Collection<String> TYPES = Set.of(
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedCommand.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemChange.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenItemReceivedUpdate.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenChannelTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenCronTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenTimeTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenThingTrigger.class),
            JRuleModuleUtil.toTriggerModuleUID(JRuleWhenStartup.class));
    private JRuleRuleProvider jRuleRuleProvider;

    @Activate
    public JRuleModuleHandlerFactory(@Reference JRuleRuleProvider jRuleRuleProvider) {
        this.jRuleRuleProvider = jRuleRuleProvider;
    }

    @Override
    public Collection<String> getTypes() {
        return TYPES;
    }

    @Override
    protected @Nullable ModuleHandler internalCreate(Module module, String ruleUID) {
        if (module.getTypeUID().startsWith(JRuleTriggerHandler.TRIGGER_PREFIX)) {
            return new JRuleTriggerHandler((Trigger) module, jRuleRuleProvider, ruleUID);
        }
        return null;
    }
}
