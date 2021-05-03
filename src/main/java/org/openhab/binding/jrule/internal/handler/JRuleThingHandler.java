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
package org.openhab.binding.jrule.internal.handler;

import static org.openhab.core.thing.ThingStatus.ONLINE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.jrule.internal.JRuleBindingConstants;
import org.openhab.binding.jrule.internal.JRuleChannel;
import org.openhab.binding.jrule.internal.JRuleConfig;
import org.openhab.binding.jrule.internal.JRuleUtil;
import org.openhab.binding.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.binding.jrule.internal.watch.JRuleRulesWatcher;
import org.openhab.binding.jrule.items.JRuleItemClassGenerator;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.voice.VoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleClientThingHandler} is responsible for handling commands and status
 * updates for JRule State Machines.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class JRuleThingHandler extends BaseThingHandler implements PropertyChangeListener {

    @NonNullByDefault({})
    private ItemRegistry itemRegistry;

    @NonNullByDefault({})
    private JRuleEventSubscriber eventSubscriber;

    @NonNullByDefault({})
    private EventPublisher eventPublisher;

    @NonNullByDefault({})
    private VoiceManager voiceManager;

    private static final String PREFIX_DEBUG_LOG = "[{}] [{}] {}";

    private static final String PREFIX_INFO_LOG = "[{}] {}";

    private final Logger logger = LoggerFactory.getLogger(JRuleThingHandler.class);

    @Nullable
    private JRuleRulesWatcher directoryWatcher;

    @Nullable
    private JRuleItemClassGenerator itemGenerator;

    @Nullable
    private JRuleCompiler compiler;

    @Nullable
    private JRuleConfig config;

    @Nullable
    private Thread rulesDirWatcherThread;

    public JRuleThingHandler(Thing thing, ItemRegistry itemRegistry, EventPublisher eventPublisher,
            JRuleEventSubscriber eventSubscriber, VoiceManager voiceManager) {
        super(thing);
        this.itemRegistry = itemRegistry;
        this.eventSubscriber = eventSubscriber;
        this.eventPublisher = eventPublisher;
        this.voiceManager = voiceManager;
        JRuleEventHandler jRuleEventHandler = JRuleEventHandler.get();
        jRuleEventHandler.setEventPublisher(eventPublisher);
        jRuleEventHandler.setItemRegistry(itemRegistry);
        JRuleVoiceHandler jRuleVoiceHandler = JRuleVoiceHandler.get();
        jRuleVoiceHandler.setVoiceManager(voiceManager);
        logger.debug("New instance JRuleThingHandler: {}", thing.getUID());
    }

    // Remove creating instances of items
    private void createRuleInstances() {
        try {
            final URL[] urls = new URL[] {
                    new File(config.getWorkingDirectory() + JRuleConfig.ITEMS_DIR_START).toURI().toURL(),
                    new File(config.getWorkingDirectory() + JRuleConfig.RULES_DIR_START).toURI().toURL() };

            ClassLoader loader = new URLClassLoader(urls, JRuleUtil.class.getClassLoader());
            compiler.loadClasses(loader, new File(config.getWorkingDirectory() + JRuleConfig.RULES_DIR),
                    JRuleConfig.RULES_PACKAGE, true);

        } catch (MalformedURLException e) {
            logger.debug("Failed to create instance", e);
        }
    }

    private void compileUserRules() {
        logger.debug("Compile User Rules");
        compiler.compileRules();
    }

    private void compileItemSources() {
        logger.debug("Compile class");
        compiler.compileItems();
    }

    private void loadRules() {
        Class[] classes = null;
        try {
            classes = JRuleUtil.getClasses("org.openhab.binding.jrule.rules");
        } catch (ClassNotFoundException e) {
            logger.debug("Failed to get classes", e);
        } catch (IOException e) {
            logger.debug("Failed to get classes", e);
        }
        if (classes != null) {
            logger.debug("Iterating classes: {}", classes.length);
            for (Class clazz : classes) {
                logger.debug("++Found class: {}", clazz.getName());// jRuleCommandHandler.se//, eventSubscriber)
            }
        }
        // new MyRule();
    }

    @SuppressWarnings("null")
    @Override
    public final void initialize() {
        config = getConfigAs(JRuleConfig.class);
        itemGenerator = new JRuleItemClassGenerator(config);
        compiler = new JRuleCompiler(config);
        logger.debug("SettingConfig name: {} config: {}", config.getClass(), config.toString());
        writeExternalJars();
        generateItemSources();
        compileItemSources();
        createItemsJar();
        compileUserRules();
        createRuleInstances();
        startDirectoryWatcher();
        eventSubscriber.startSubscriper();
        logger.info("Initializing, setting status ONLINE");
        updateStatus(ONLINE);
    }

    private void startDirectoryWatcher() {
        final Path path = new File(config.getWorkingDirectory() + File.separator + JRuleConfig.RULES_DIR).toPath();
        directoryWatcher = new JRuleRulesWatcher(path);
        directoryWatcher.addPropertyChangeListener(this);
        rulesDirWatcherThread = new Thread(directoryWatcher);
        rulesDirWatcherThread.start();
    }

    private void createItemsJar() {
        JRuleUtil.createJarFile(config.getWorkingDirectory() + File.separator + "gen",
                config.getWorkingDirectory() + File.separator + "jar" + File.separator + "jruleItems.jar");
    }

    private void writeExternalJars() {
        writeJar("jrule.jar");
        writeJar("slf4j-api-1.7.16.jar");
        writeJar("org.eclipse.jdt.annotation-2.2.100.jar");
    }

    private void writeJar(String name) {
        URL jarUrl = JRuleUtil.getResourceUrl("lib/" + name);
        final byte[] jarBytes = JRuleUtil.getResourceAsBytes(jarUrl);
        JRuleUtil.writeFile(jarBytes, config.getWorkingDirectory() + File.separator + "jar/" + name);
    }

    @SuppressWarnings("null")
    @Override
    public void dispose() {
        super.dispose();
        logger.debug("JRuleThingHandler dispose");
        JRuleEngine.get().reset();
        if (directoryWatcher != null) {
            directoryWatcher.removePropertyChangeListener(this);
        }
        if (rulesDirWatcherThread != null) {
            try {
                rulesDirWatcherThread.interrupt();
                rulesDirWatcherThread.join();
            } catch (Exception x) {
                // Best effort
            }
        }
    }

    public void generateItemSources() {
        itemRegistry.getItems().stream().forEach(item -> generateItemSource(item));
    }

    private void generateItemSource(Item item) {
        if (itemGenerator != null) {
            itemGenerator.generateItemSource(item);
        } else {
            logger.error("Failed to generate Item sources, not initialized");
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        logger.debug("Handling command = {} for channel = {}", command.getClass(), channelUID);
        String channelId = channelUID.getIdWithoutGroup();
        JRuleChannel channel = JRuleChannel.fromString(channelId);
        if (channel == null) {
            logger.info("Got channel null for channelId = {}", channelId);
        }

        if (command instanceof OnOffType) {
            if (channel != null) {
                updateOnOffChannel(channel, channelId, (OnOffType) command);
            }
            return;
        }
    }

    @SuppressWarnings("null")
    private void updateOnOffChannel(JRuleChannel channel, String channelId, OnOffType command) {
        switch (channel) {
            case GENERATE_ITEMS:
                logger.info("Generating items");
                generateItemSources();
                break;
            default:
                break;
        }
    }

    public static boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return JRuleBindingConstants.THING_TYPE_JRULE.equals(thingTypeUID);
    }

    @Override
    public void propertyChange(@Nullable PropertyChangeEvent evt) {
        if (evt != null) {
            final String property = evt.getPropertyName();
            if (property == JRuleRulesWatcher.PROPERTY_ENTRY_CREATE
                    || property == JRuleRulesWatcher.PROPERTY_ENTRY_MODIFY
                    || property == JRuleRulesWatcher.PROPERTY_ENTRY_DELETE) {
                Path newValue = (Path) evt.getNewValue();
                logger.debug("Directory watcher new value: {}", newValue);
                reloadRules();
                logger.debug("All Rules reloaded");

            }
        }
    }

    private void reloadRules() {
        compileUserRules();
        JRuleEngine.get().clear();
        // compiler.
        createRuleInstances();
    }
}
