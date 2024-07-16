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
package org.openhab.automation.jrule.internal.handler;

import java.util.*;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.automation.Action;
import org.openhab.core.automation.handler.ActionHandler;
import org.openhab.core.automation.handler.ModuleHandlerFactory;
import org.openhab.core.automation.type.ActionType;
import org.openhab.core.automation.type.ModuleTypeRegistry;
import org.openhab.core.config.core.Configuration;

/**
 * The {@link JRuleEventHandler} is responsible for handling commands and status
 * updates for JRule
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleModuleActionHandler {

    private static volatile JRuleModuleActionHandler instance = null;

    private ModuleTypeRegistry moduleTypeRegistry;

    private Set<ModuleHandlerFactory> moduleHandlerFactories;

    public void setModuleTypeRegistry(ModuleTypeRegistry moduleTypeRegistry) {
        this.moduleTypeRegistry = moduleTypeRegistry;
    }

    public void setModuleHandlerFactories(Set<ModuleHandlerFactory> moduleHandlerFactories) {
        this.moduleHandlerFactories = moduleHandlerFactories;
    }

    private JRuleModuleActionHandler() {
    }

    public Map<String, Object> execute(String uid, Map<String, Object> inputs) {

        // Find module
        ActionType actionType = moduleTypeRegistry.getActions().stream().filter(type -> type.getUID().equals(uid))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Action not found"));

        Action module = new Action() {

            @Override
            public String getId() {
                return actionType.getUID();
            }

            @Override
            public String getTypeUID() {
                return actionType.getUID();
            }

            @Override
            public @Nullable String getLabel() {
                return actionType.getLabel();
            }

            @Override
            public @Nullable String getDescription() {
                return actionType.getDescription();
            }

            @Override
            public Configuration getConfiguration() {
                Configuration configuration = new Configuration();
                inputs.forEach(configuration::put);
                return configuration;
            }

            @Override
            public Map<String, String> getInputs() {
                return Map.of();
            }
        };

        // Find handler for this action
        ModuleHandlerFactory handlerFactory = moduleHandlerFactories.stream()
                .filter(factory -> factory.getTypes().contains(uid)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Handler not found"));

        ActionHandler moduleHandler = null;

        try {
            moduleHandler = (ActionHandler) handlerFactory.getHandler(module, "jrule");
            // Execute on handler
            Map<String, Object> execute = moduleHandler.execute(inputs);

            return execute;
        } finally {
            // Clean up
            if (moduleHandler != null) {
                handlerFactory.ungetHandler(module, "jrule", moduleHandler);
            }
        }
    }

    public static JRuleModuleActionHandler get() {
        if (instance == null) {
            synchronized (JRuleModuleActionHandler.class) {
                if (instance == null) {
                    instance = new JRuleModuleActionHandler();
                }
            }
        }
        return instance;
    }
}
