/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.compiler.JRuleCompiler;
import org.openhab.automation.jrule.internal.compiler.JRuleJarExtractor;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.internal.watch.JRuleRulesWatcher;
import org.openhab.automation.jrule.items.JRuleItemClassGenerator;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.events.ItemAddedEvent;
import org.openhab.core.items.events.ItemRemovedEvent;
import org.openhab.core.items.events.ItemUpdatedEvent;
import org.openhab.core.voice.VoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleHandler} is responsible for handling commands and status
 * updates for JRule State Machines.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
@NonNullByDefault
public class JRuleHandler implements PropertyChangeListener {

    private static final String JAR_LIB_PATH = "lib/";

    private static final String LOG_NAME_HANDLER = "JRuleHandler";

    @NonNullByDefault({})
    private ItemRegistry itemRegistry;

    @NonNullByDefault({})
    private JRuleEventSubscriber eventSubscriber;

    @NonNullByDefault({})
    private VoiceManager voiceManager;

    private final Logger logger = LoggerFactory.getLogger(JRuleHandler.class);

    @Nullable
    private JRuleRulesWatcher directoryWatcher;

    @Nullable
    private JRuleItemClassGenerator itemGenerator;

    @Nullable
    private JRuleCompiler compiler;

    private final JRuleConfig config;
    private final JRuleJarExtractor jarExtractor = new JRuleJarExtractor();

    @Nullable
    private Thread rulesDirWatcherThread;

    private volatile boolean recompileJar = true;

    public JRuleHandler(Map<String, Object> properties, ItemRegistry itemRegistry, EventPublisher eventPublisher,
            JRuleEventSubscriber eventSubscriber, VoiceManager voiceManager) {
        this.itemRegistry = itemRegistry;
        this.eventSubscriber = eventSubscriber;
        this.voiceManager = voiceManager;
        JRuleEventHandler jRuleEventHandler = JRuleEventHandler.get();
        jRuleEventHandler.setEventPublisher(eventPublisher);
        jRuleEventHandler.setItemRegistry(itemRegistry);
        eventSubscriber.addPropertyChangeListener(this);
        JRuleVoiceHandler jRuleVoiceHandler = JRuleVoiceHandler.get();
        jRuleVoiceHandler.setVoiceManager(voiceManager);
        config = new JRuleConfig(properties);

        logDebug("New instance: {}", properties);
    }

    private void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, LOG_NAME_HANDLER, message, parameters);
    }

    private void logInfo(String message, Object... parameters) {
        JRuleLog.info(logger, LOG_NAME_HANDLER, message, parameters);
    }

    private void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, LOG_NAME_HANDLER, message, parameters);
    }

    private void logError(String message, Object... parameters) {
        JRuleLog.error(logger, LOG_NAME_HANDLER, message, parameters);
    }

    @SuppressWarnings("null")
    private void createRuleInstances() {
        if (compiler == null) {
            logError("No compiler set failed to create rule instances");
            return;
        }
        final List<URL> urlList = new ArrayList<>();
        final List<URL> extLibPath = compiler.getExtLibsAsUrls();
        try {
            urlList.add(new File(config.getItemsRootDirectory()).toURI().toURL());
            urlList.add(new File(config.getRulesRootDirectory()).toURI().toURL());
        } catch (MalformedURLException x) {
            logError("Failed to build class path for creating rule instance");
        }
        urlList.addAll(extLibPath);
        final ClassLoader loader = new URLClassLoader(urlList.toArray(URL[]::new), JRuleUtil.class.getClassLoader());
        compiler.loadClasses(loader, new File(config.getRulesDirectory()), JRuleConfig.RULES_PACKAGE, true);
    }

    private void compileUserRules() {
        if (compiler != null) {
            compiler.compileRules();
        } else {
            logDebug("Compiler is null aborting");
        }
    }

    private boolean initializeFolder(String folder) {
        File fileFolder = new File(folder);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        if (!fileFolder.canRead() || !fileFolder.canWrite()) {
            logError("JarFolder can not read or write: {}", folder);
            return false;
        }
        return true;
    }

    public synchronized final void initialize() {
        logInfo("Start Initializing JRule Automation");
        config.initConfig();
        itemGenerator = new JRuleItemClassGenerator(config);
        compiler = new JRuleCompiler(config);
        logDebug("SettingConfig name: {} config: {}", config.getClass(), config.toString());
        logDebug("Initializing JRule automation folder: {}", config.getWorkingDirectory());
        if (!initializeFolder(config.getWorkingDirectory())) {
            return;
        }
        if (!initializeFolder(config.getExtlibDirectory())) {
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

        logInfo("Initializing JRule writing external Jars: {}", config.getJarDirectory());
        writeAndExtractJruleJar();
        handleItemSources();
        logInfo("Compiling rules");
        compileUserRules();
        createRuleInstances();
        startDirectoryWatcher();
        eventSubscriber.startSubscriber();
        logInfo("JRule Engine Initializing done!");
    }

    private void writeAndExtractJruleJar() {
        jarExtractor.extractJRuleJar(compiler.getJarPath(JRuleCompiler.JAR_JRULE_NAME));
    }

    private void handleItemSources() {
        logInfo("Generating items");
        generateItemSources();
        if (recompileJar) {
            logInfo("Compiling items");
            compileItems();
            logInfo("Creating items jar");
            createItemsJar();
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
        if (compiler != null) {
            JRuleUtil.createJarFile(config.getItemsRootDirectory(),
                    compiler.getJarPath(JRuleCompiler.JAR_JRULE_ITEMS_NAME));
            recompileJar = false;
        } else {
            logError("Failed to create items due to config {}, compiler is null", config);
        }
    }

    public synchronized void dispose() {
        logDebug("Dispose called!");
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
        Arrays.stream(javaSourceItemsFromFolder)
                .filter(f -> itemNames.contains(JRuleUtil.removeExtension(f.getName(), JRuleConstants.JAVA_FILE_TYPE)))
                .forEach(this::deleteFile);

        items.forEach(this::generateItemSource);
    }

    private synchronized boolean deleteFile(File f) {
        if (f.exists()) {
            logDebug("Deleting file: {}", f.getAbsolutePath());
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
            logError("Failed to generate Item sources, not initialized");
        }
    }

    private synchronized void deleteClassFileForItem(Item item) {
        deleteClassFileForItem(item.getName());
    }

    private synchronized void deleteClassFileForItem(String itemName) {
        deleteFile(new File(new StringBuilder().append(config.getItemsDirectory()).append(File.separator)
                .append(config.getGeneratedItemPrefix()).append(itemName).append(JRuleConstants.CLASS_FILE_TYPE)
                .toString()));
    }

    private synchronized void deleteSourceFileForItem(String itemName) {
        deleteFile(new File(new StringBuilder().append(config.getItemsDirectory()).append(File.separator)
                .append(config.getGeneratedItemPrefix()).append(itemName).append(JRuleConstants.JAVA_FILE_TYPE)
                .toString()));
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
                logDebug("Event value null. ignoring: {}", evt);
                return;
            }
            String eventType = event.getType();
            String itemName = JRuleUtil.getItemNameFromTopic(event.getTopic());

            if (eventType.equals(ItemRemovedEvent.TYPE)) {
                logDebug("RemovedType: {}", evt);
                deleteClassFileForItem(itemName);
                deleteSourceFileForItem(itemName);
                recompileJar = true;
            } else if (eventType.equals(ItemAddedEvent.TYPE) || event.getType().equals(ItemUpdatedEvent.TYPE)) {
                try {
                    logDebug("Added/updatedType: {}", evt);
                    Item item = itemRegistry.getItem(itemName);
                    generateItemSource(item);
                } catch (ItemNotFoundException e) {
                    logDebug("Could not find new item", e);
                }
            } else {
                logDebug("Failed to do something with item event");
            }
            if (recompileJar) {
                compileItems();
                createItemsJar();
            }
        } else if (JRuleRulesWatcher.PROPERTY_ENTRY_CREATE.equals(property)
                || JRuleRulesWatcher.PROPERTY_ENTRY_MODIFY.equals(property)
                || JRuleRulesWatcher.PROPERTY_ENTRY_DELETE.equals(property)) {
            Path newValue = (Path) evt.getNewValue();
            logDebug("Directory watcher new value: {}", newValue);
            reloadRules();
            logDebug("All Rules reloaded");

        }
    }

    private void reloadRules() {
        compileUserRules();
        JRuleEngine.get().reset();
        createRuleInstances();
        eventSubscriber.stopSubscriber();
        eventSubscriber.startSubscriber();
        logInfo("JRule Engine Rules Reloaded!");
    }
}
