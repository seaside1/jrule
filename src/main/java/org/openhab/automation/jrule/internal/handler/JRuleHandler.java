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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.DelayedDebouncingExecutor;
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
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.events.ItemAddedEvent;
import org.openhab.core.items.events.ItemRemovedEvent;
import org.openhab.core.items.events.ItemUpdatedEvent;
import org.openhab.core.voice.VoiceManager;
import org.osgi.framework.BundleContext;
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

    private static final String LOG_NAME_HANDLER = "JRuleHandler";

    @NonNullByDefault({})
    private final ItemRegistry itemRegistry;

    @NonNullByDefault({})
    private final JRuleEventSubscriber eventSubscriber;

    private final Logger logger = LoggerFactory.getLogger(JRuleHandler.class);

    @Nullable
    private JRuleRulesWatcher directoryWatcher;

    @Nullable
    private final JRuleItemClassGenerator itemGenerator;

    private final JRuleCompiler compiler;

    private final JRuleConfig config;
    private final JRuleJarExtractor jarExtractor = new JRuleJarExtractor();

    @Nullable
    private Thread rulesDirWatcherThread;

    private final DelayedDebouncingExecutor delayedRulesReloader;
    private final DelayedDebouncingExecutor delayedItemsCompiler;

    public JRuleHandler(JRuleConfig config, ItemRegistry itemRegistry, EventPublisher eventPublisher,
            JRuleEventSubscriber eventSubscriber, VoiceManager voiceManager, BundleContext bundleContext) {
        this.itemRegistry = itemRegistry;
        this.eventSubscriber = eventSubscriber;
        this.config = config;
        this.delayedRulesReloader = new DelayedDebouncingExecutor(config.getRulesInitDelaySeconds(), TimeUnit.SECONDS);
        this.delayedItemsCompiler = new DelayedDebouncingExecutor(config.getItemsRecompilationDelaySeconds(),
                TimeUnit.SECONDS);
        itemGenerator = new JRuleItemClassGenerator(config);
        compiler = new JRuleCompiler(config);

        JRuleEventHandler jRuleEventHandler = JRuleEventHandler.get();
        jRuleEventHandler.setEventPublisher(eventPublisher);
        jRuleEventHandler.setItemRegistry(itemRegistry);
        eventSubscriber.addPropertyChangeListener(this);
        JRuleVoiceHandler jRuleVoiceHandler = JRuleVoiceHandler.get();
        jRuleVoiceHandler.setVoiceManager(voiceManager);
        JRuleTransformationHandler jRuleTransformationHandler = JRuleTransformationHandler.get();
        jRuleTransformationHandler.setBundleContext(bundleContext);
        logDebug("JRuleHandler()");
    }

    public synchronized final void initialize() {
        logInfo("Start Initializing JRule Automation");
        logDebug("SettingConfig name: {} config: {}", config.getClass(), config.toString());
        logDebug("Initializing JRule automation folder: {}", config.getWorkingDirectory());
        if (!initializeFolder(config.getWorkingDirectory())) {
            return;
        }
        if (!initializeFolder(config.getExtlibDirectory())) {
            return;
        }
        if (!initializeFolder(config.getJarRulesDirectory())) {
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

        // Extract and copy jrules.jar
        jarExtractor.extractJRuleJar(compiler.getJarPath(JRuleCompiler.JAR_JRULE_NAME));

        // Generate source files for all items + Items.java
        Collection<Item> items = itemRegistry.getItems();
        items.forEach(itemGenerator::generateItemSource);
        itemGenerator.generateItemsSource(items);

        // Compilation of items
        compileItemsInternal();

        // Compile rules
        logInfo("Compiling rules");
        compiler.compileRules();

        // Reload rules
        createRuleInstances();

        // Start directory watcher for source file changes
        startDirectoryWatcher();
        eventSubscriber.startSubscriber();

        logInfo("JRule Engine Initializing done! {}", JRuleEngine.get().getRuleLoadingStatistics());
    }

    public synchronized void dispose() {
        logDebug("Dispose called!");
        eventSubscriber.stopSubscriber();
        delayedRulesReloader.cancel();
        delayedRulesReloader.shutdown();
        delayedItemsCompiler.cancel();
        delayedItemsCompiler.shutdown();
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
        JRuleItemRegistry.clear();
        logDebug("Dispose complete");
    }

    private void createRuleInstances() {
        final List<URL> urlList = new ArrayList<>();
        final List<URL> extLibPath = compiler.getExtLibsAsUrls();
        final List<URL> jarRulesPath = compiler.getJarRulesAsUrls();
        try {
            urlList.add(new File(config.getItemsRootDirectory()).toURI().toURL());
            urlList.add(new File(config.getRulesRootDirectory()).toURI().toURL());
        } catch (MalformedURLException x) {
            logError("Failed to build class path for creating rule instance");
        }
        urlList.addAll(extLibPath);
        urlList.addAll(jarRulesPath);

        final JRuleClassLoader loader = new JRuleClassLoader(urlList.toArray(URL[]::new),
                JRuleUtil.class.getClassLoader());

        // Load item classes first
        compiler.loadClassesFromFolder(loader, new File(config.getItemsRootDirectory()), JRuleConfig.ITEMS_PACKAGE,
                false);
        // Clear registry from old items
        JRuleItemRegistry.clear();
        // Reload Items class - this will also instantiate all items and load them to the registry
        try {
            Class<?> cls = Class.forName(config.getGeneratedItemPackage() + ".Items", true, loader);
            cls.getDeclaredConstructor().newInstance();
            logger.info("Instantiated Items class");
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException
                | InvocationTargetException e) {
            logger.error("Could not instantiate Items file {}", e.toString());
        }

        // Load rules that refer to the items
        compiler.loadClassesFromFolder(loader, new File(config.getRulesRootDirectory()), JRuleConfig.RULES_PACKAGE,
                true);
        compiler.loadClassesFromJar(loader, new File(config.getJarRulesDirectory()), JRuleConfig.RULES_PACKAGE, true);
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

    @Nullable
    private synchronized Boolean compileItemsInternal() {
        logInfo("Compiling items");
        itemGenerator.generateItemsSource(itemRegistry.getItems());
        Boolean result = false;
        if (compiler.compileItems()) {

            logInfo("Creating items jar");
            JRuleUtil.createJarFile(config.getItemsRootDirectory(),
                    compiler.getJarPath(JRuleCompiler.JAR_JRULE_ITEMS_NAME));
            result = true;
        } else {
            logError("Compilation failed, not creating jar file");

        }
        return result;
    }

    @Nullable
    private synchronized Boolean compileAndReloadRules() {
        eventSubscriber.pauseEventDelivery();
        compiler.compileRules();
        JRuleEngine.get().reset();
        createRuleInstances();
        logInfo("JRule Engine Rules Reloaded! {}", JRuleEngine.get().getRuleLoadingStatistics());
        eventSubscriber.registerSubscribedItemsAndChannels();
        eventSubscriber.resumeEventDelivery();
        return true;
    }

    @Nullable
    private Boolean compileAndReloadItemsAndRules() {
        if (compileItemsInternal()) {
            compileAndReloadRules();
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
                deleteSourceFileForItem(itemName);
                delayedItemsCompiler.call(this::compileItemsInternal);
            } else if (eventType.equals(ItemAddedEvent.TYPE) || event.getType().equals(ItemUpdatedEvent.TYPE)) {
                try {
                    logDebug("Added/updatedType: {}", evt);
                    Item item = itemRegistry.getItem(itemName);
                    itemGenerator.generateItemSource(item);
                    delayedItemsCompiler.call(this::compileAndReloadItemsAndRules);
                } catch (ItemNotFoundException e) {
                    logDebug("Could not find new item", e);
                }
            } else {
                logDebug("Failed to do something with item event");
            }
        } else if (JRuleRulesWatcher.PROPERTY_ENTRY_CREATE.equals(property)
                || JRuleRulesWatcher.PROPERTY_ENTRY_MODIFY.equals(property)
                || JRuleRulesWatcher.PROPERTY_ENTRY_DELETE.equals(property)) {
            Path newValue = (Path) evt.getNewValue();
            logDebug("Directory watcher new value: {}", newValue);
            delayedRulesReloader.call(this::compileAndReloadRules);
        }
    }

    private void startDirectoryWatcher() {
        List<Path> paths = new ArrayList<>();
        final Path pathRules = new File(config.getWorkingDirectory() + File.separator + JRuleConfig.RULES_DIR).toPath();
        final Path pathJarRules = new File(config.getWorkingDirectory() + File.separator + JRuleConfig.JAR_RULES_DIR)
                .toPath();
        paths.add(pathRules);
        paths.add(pathJarRules);
        directoryWatcher = new JRuleRulesWatcher(paths);
        directoryWatcher.addPropertyChangeListener(this);
        rulesDirWatcherThread = new Thread(directoryWatcher);
        rulesDirWatcherThread.start();
    }

    private synchronized boolean deleteFile(File f) {
        if (f.exists()) {
            logDebug("Deleting file: {}", f.getAbsolutePath());
            return f.delete();
        }
        return false;
    }

    private synchronized void deleteSourceFileForItem(String itemName) {
        deleteFile(new File(new StringBuilder().append(config.getItemsDirectory()).append(File.separator)
                .append(config.getGeneratedItemPrefix()).append(itemName).append(JRuleConstants.JAVA_FILE_TYPE)
                .toString()));
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

    private static class JRuleClassLoader extends URLClassLoader {
        public JRuleClassLoader(URL[] urls, @Nullable ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                for (URL url : getURLs()) {
                    if (url.getProtocol().equals("file")) {
                        FileInputStream is = new FileInputStream(
                                url.getFile() + "/" + name.replaceAll("\\.", "/") + JRuleConstants.CLASS_FILE_TYPE);
                        if (is != null) {
                            byte[] buf = is.readAllBytes();
                            is.close();
                            return defineClass(name, is.readAllBytes(), 0, buf.length);
                        }
                    }
                }
                return super.loadClass(name);

            } catch (IOException e) {
                return super.loadClass(name);
            }
        }
    }
}
