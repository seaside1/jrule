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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.jrule.internal.JRuleBindingConstants;
import org.openhab.binding.jrule.internal.JRuleChannel;
import org.openhab.binding.jrule.internal.JRuleConfig;
import org.openhab.binding.jrule.internal.JRuleUtil;
import org.openhab.binding.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.binding.jrule.internal.watch.JRuleRulesWatcher;
import org.openhab.binding.jrule.items.JRuleItemClassGenerator;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.events.ItemAddedEvent;
import org.openhab.core.items.events.ItemRemovedEvent;
import org.openhab.core.items.events.ItemUpdatedEvent;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
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

    private static final String JAR_FOLDER = "jar/";

    private static final String JAR_LIB_PATH = "lib/";

    @NonNullByDefault({})
    private ItemRegistry itemRegistry;

    @NonNullByDefault({})
    private JRuleEventSubscriber eventSubscriber;

    @NonNullByDefault({})
    private VoiceManager voiceManager;

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

    private volatile boolean recompileJar = true;

    public JRuleThingHandler(Thing thing, ItemRegistry itemRegistry, EventPublisher eventPublisher,
            JRuleEventSubscriber eventSubscriber, VoiceManager voiceManager) {
        super(thing);
        this.itemRegistry = itemRegistry;
        this.eventSubscriber = eventSubscriber;
        this.voiceManager = voiceManager;
        JRuleEventHandler jRuleEventHandler = JRuleEventHandler.get();
        jRuleEventHandler.setEventPublisher(eventPublisher);
        jRuleEventHandler.setItemRegistry(itemRegistry);
        eventSubscriber.addPropertyChangeListener(this);
        JRuleVoiceHandler jRuleVoiceHandler = JRuleVoiceHandler.get();
        jRuleVoiceHandler.setVoiceManager(voiceManager);
        logger.debug("New instance JRuleThingHandler: {}", thing.getUID());
    }

    private void createRuleInstances() {
        try {
            final URL[] urls = new URL[] { new File(config.getItemsRootDirectory()).toURI().toURL(),
                    new File(config.getRulesRootDirectory()).toURI().toURL() };

            final ClassLoader loader = new URLClassLoader(urls, JRuleUtil.class.getClassLoader());
            compiler.loadClasses(loader, new File(config.getRulesDirectory()), JRuleConfig.RULES_PACKAGE, true);

        } catch (MalformedURLException e) {
            logger.debug("Failed to create instance", e);
        }
    }

    private void compileUserRules() {
        logger.debug("Compile User Rules");
        if (compiler != null) {
            compiler.compileRules();
        } else {
            logger.debug("Compiler is null aborting");
            return;
        }
    }

    private boolean initializeFolder(String folder) {
        File fileFolder = new File(folder);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        if (!fileFolder.canRead() || !fileFolder.canWrite()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR,
                    "Folder can not read or write: " + folder);
            logger.error("JarFolder can not read or write: {}", folder);
            return false;
        }
        return true;
    }

    @SuppressWarnings("null")
    @Override
    public synchronized final void initialize() {
        logger.info("Start Initializing JRule Binding");

        updateStatus(ThingStatus.UNKNOWN);
        config = getConfigAs(JRuleConfig.class);
        itemGenerator = new JRuleItemClassGenerator(config);
        compiler = new JRuleCompiler(config);
        logger.debug("SettingConfig name: {} config: {}", config.getClass(), config.toString());

        if (!initializeFolder(config.getWorkingDirectory())) {
            return;
        }
        if (!initializeFolder(config.getJarDirectory())) {
            return;
        }
        if (!initializeFolder(config.getItemsDirectory())) {
            return;
        }
        if (!initializeFolder(config.getRulesDirectory())) {
            return;
        }

        updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.BRIDGE_UNINITIALIZED, "Writing jars");
        writeExternalJars();
        handleItemSources();
        updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.BRIDGE_UNINITIALIZED, "Compiling rules");
        compileUserRules();
        createRuleInstances();
        startDirectoryWatcher();
        eventSubscriber.startSubscriper();
        logger.info("Initializing, setting status ONLINE");
        updateStatus(ONLINE);
    }

    private void handleItemSources() {
        updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.BRIDGE_UNINITIALIZED, "Generating items");
        generateItemSources();
        if (recompileJar) {
            updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.BRIDGE_UNINITIALIZED, "Compiling items");
            compileItems();
            updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.BRIDGE_UNINITIALIZED, "Creating items jar");
            createItemsJar();
            updateStatus(ONLINE);
        }
    }

    private void startDirectoryWatcher() {
        final Path path = new File(config.getWorkingDirectory() + File.separator + JRuleConfig.RULES_DIR).toPath();
        directoryWatcher = new JRuleRulesWatcher(path);
        directoryWatcher.addPropertyChangeListener(this);
        rulesDirWatcherThread = new Thread(directoryWatcher);
        rulesDirWatcherThread.start();
    }

    @SuppressWarnings("null")
    private synchronized void createItemsJar() {
        if (config != null && compiler != null) {
            JRuleUtil.createJarFile(config.getItemsRootDirectory(),
                    compiler.getJarPath(JRuleCompiler.JAR_JRULE_ITEMS_NAME));
            recompileJar = false;
        } else {
            logger.error("Failed to create items due to config {}, compiler {}", config, compiler);
        }
    }

    private synchronized void writeExternalJars() {
        writeJar(JRuleCompiler.JAR_JRULE_NAME);
        writeJar(JRuleCompiler.JAR_SLF4J_API_NAME);
        writeJar(JRuleCompiler.JAR_ECLIPSE_ANNOTATIONS_NAME);
    }

    private synchronized void writeJar(String name) {
        final URL jarUrl = JRuleUtil.getResourceUrl(JAR_LIB_PATH.concat(name));
        final byte[] jarBytes = JRuleUtil.getResourceAsBytes(jarUrl);
        JRuleUtil.writeFile(jarBytes, config.getWorkingDirectory() + File.separator + JAR_FOLDER + name);
    }

    @SuppressWarnings("null")
    @Override
    public synchronized void dispose() {
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
        eventSubscriber.removePropertyChangeListener(this);
    }

    public synchronized void generateItemSources() {
        File[] javaSourceItemsFromFolder = compiler.getJavaSourceItemsFromFolder(new File(config.getItemsDirectory()));
        Collection<Item> items = itemRegistry.getItems();
        Set<String> itemNames = new HashSet<>();
        items.forEach(item -> itemNames.add(item.getName()));

        // Delete items that are not present anymore
        Arrays.stream(javaSourceItemsFromFolder).filter(
                f -> itemNames.contains(JRuleUtil.removeExtension(f.getName(), JRuleBindingConstants.JAVA_FILE_TYPE)))
                .forEach(f -> deleteFile(f));

        items.stream().forEach(item -> generateItemSource(item));
    }

    private synchronized boolean deleteFile(File f) {
        if (f.exists()) {
            logger.debug("Deleting file: {}", f.getAbsolutePath());
            return f.delete();
        }
        return false;
    }

    private synchronized void generateItemSource(Item item) {
        if (itemGenerator != null) {
            final boolean itemUpdated = itemGenerator.generateItemSource(item);
            recompileJar |= itemUpdated;
            if (itemUpdated) {
                deleteClassFileForItem(item);
            }
        } else {
            logger.error("Failed to generate Item sources, not initialized");
        }
    }

    private synchronized void deleteClassFileForItem(Item item) {
        deleteClassFileForItem(item.getName());
    }

    private synchronized void deleteClassFileForItem(String itemName) {
        deleteFile(new File(new StringBuilder().append(config.getItemsDirectory()).append(File.separator)
                .append(JRuleBindingConstants.JRULE_GENERATION_PREFIX).append(itemName)
                .append(JRuleBindingConstants.CLASS_FILE_TYPE).toString()));
    }

    private synchronized void deleteSourceFileForItem(String itemName) {
        deleteFile(new File(new StringBuilder().append(config.getItemsDirectory()).append(File.separator)
                .append(JRuleBindingConstants.JRULE_GENERATION_PREFIX).append(itemName)
                .append(JRuleBindingConstants.JAVA_FILE_TYPE).toString()));
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

    private synchronized void compileItems() {
        compiler.compileItems();
    }

    @Override
    public void propertyChange(@Nullable PropertyChangeEvent evt) {
        if (evt == null) {
            return;
        }

        final String property = evt.getPropertyName();
        if (property.equals(JRuleEventSubscriber.PROPERTY_ITEM_REGISTRY_EVENT)) {
            Event event = (Event) evt.getNewValue();
            if (event == null) {
                logger.debug("Event value null. ignoring: {}", evt);
                return;
            }
            String eventType = event.getType();
            String itemName = JRuleUtil.getItemNameFromTopic(event.getTopic());

            if (eventType.equals(ItemRemovedEvent.TYPE)) {
                logger.debug("RemovedType: {}", evt);
                deleteClassFileForItem(itemName);
                deleteSourceFileForItem(itemName);
                recompileJar = true;
            } else if (eventType.equals(ItemAddedEvent.TYPE) || event.getType().equals(ItemUpdatedEvent.TYPE)) {
                try {
                    logger.debug("Added/updatedType: {}", evt);
                    Item item = itemRegistry.getItem(itemName);
                    generateItemSource(item);
                } catch (ItemNotFoundException e) {
                    logger.debug("Could not find new item", e);
                }
            } else {
                logger.debug("Fauled to do something with item event");
            }
            if (recompileJar) {
                compileItems();
                createItemsJar();
            }
        } else if (property == JRuleRulesWatcher.PROPERTY_ENTRY_CREATE
                || property == JRuleRulesWatcher.PROPERTY_ENTRY_MODIFY
                || property == JRuleRulesWatcher.PROPERTY_ENTRY_DELETE) {
            Path newValue = (Path) evt.getNewValue();
            logger.debug("Directory watcher new value: {}", newValue);
            reloadRules();
            logger.debug("All Rules reloaded");

        }
    }

    private void reloadRules() {
        compileUserRules();
        JRuleEngine.get().reset();
        createRuleInstances();
    }
}
