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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.actions.JRuleActionClassGenerator;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleDelayedDebouncingExecutor;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.internal.compiler.JRuleCompiler;
import org.openhab.automation.jrule.internal.compiler.JRuleJarExtractor;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.events.JRuleEventSubscriber;
import org.openhab.automation.jrule.internal.watch.JRuleRulesWatcher;
import org.openhab.automation.jrule.items.JRuleItemClassGenerator;
import org.openhab.automation.jrule.items.JRuleItemNameClassGenerator;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.automation.jrule.things.JRuleThingClassGenerator;
import org.openhab.automation.jrule.things.JRuleThingRegistry;
import org.openhab.core.audio.AudioHTTPServer;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.items.events.ItemAddedEvent;
import org.openhab.core.items.events.ItemRemovedEvent;
import org.openhab.core.items.events.ItemUpdatedEvent;
import org.openhab.core.net.NetworkAddressService;
import org.openhab.core.scheduler.CronScheduler;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingManager;
import org.openhab.core.thing.ThingRegistry;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.events.ThingAddedEvent;
import org.openhab.core.thing.events.ThingRemovedEvent;
import org.openhab.core.thing.events.ThingUpdatedEvent;
import org.openhab.core.thing.link.ItemChannelLinkRegistry;
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
    private final ItemChannelLinkRegistry itemChannelLinkRegistry;

    @NonNullByDefault({})
    private final ThingRegistry thingRegistry;

    @NonNullByDefault({})
    private final JRuleEventSubscriber eventSubscriber;

    private final Logger logger = LoggerFactory.getLogger(JRuleHandler.class);

    @NonNullByDefault({})
    private final MetadataRegistry metadataRegistry;

    @Nullable
    private JRuleRulesWatcher directoryWatcher;

    private final JRuleItemClassGenerator itemGenerator;
    private final JRuleItemNameClassGenerator itemNameGenerator;

    private JRuleThingClassGenerator thingGenerator;
    private final JRuleActionClassGenerator actionGenerator;
    private final JRuleCompiler compiler;

    private final JRuleConfig config;
    private final JRuleJarExtractor jarExtractor = new JRuleJarExtractor();

    @Nullable
    private Thread rulesDirWatcherThread;

    private final JRuleDelayedDebouncingExecutor delayedRulesReloader;
    private final JRuleDelayedDebouncingExecutor delayedItemsCompiler;

    public JRuleHandler(JRuleConfig config, ItemRegistry itemRegistry, ItemChannelLinkRegistry itemChannelLinkRegistry,
            ThingRegistry thingRegistry, ThingManager thingManager, EventPublisher eventPublisher,
            JRuleEventSubscriber eventSubscriber, VoiceManager voiceManager, AudioHTTPServer audioHTTPServer,
            NetworkAddressService networkAddressService, CronScheduler cronScheduler, BundleContext bundleContext,
            MetadataRegistry metadataRegistry) {
        this.itemRegistry = itemRegistry;
        this.itemChannelLinkRegistry = itemChannelLinkRegistry;
        this.thingRegistry = thingRegistry;
        this.metadataRegistry = metadataRegistry;
        this.eventSubscriber = eventSubscriber;
        this.config = config;
        this.delayedRulesReloader = new JRuleDelayedDebouncingExecutor(config.getRulesInitDelaySeconds(),
                TimeUnit.SECONDS);
        this.delayedItemsCompiler = new JRuleDelayedDebouncingExecutor(config.getItemsRecompilationDelaySeconds(),
                TimeUnit.SECONDS);
        itemGenerator = new JRuleItemClassGenerator(config);
        itemNameGenerator = new JRuleItemNameClassGenerator(config);
        thingGenerator = new JRuleThingClassGenerator(config);
        actionGenerator = new JRuleActionClassGenerator(config);
        compiler = new JRuleCompiler(config);

        final JRuleEventHandler jRuleEventHandler = JRuleEventHandler.get();
        jRuleEventHandler.setEventPublisher(eventPublisher);
        jRuleEventHandler.setItemRegistry(itemRegistry);
        eventSubscriber.addPropertyChangeListener(this);
        final JRuleVoiceHandler jRuleVoiceHandler = JRuleVoiceHandler.get();
        jRuleVoiceHandler.setVoiceManager(voiceManager);
        jRuleVoiceHandler.setAudioHTTPServer(audioHTTPServer);
        jRuleVoiceHandler.setNetworkAddressService(networkAddressService);
        final JRuleTransformationHandler jRuleTransformationHandler = JRuleTransformationHandler.get();
        jRuleTransformationHandler.setBundleContext(bundleContext);

        final JRuleThingHandler thingHandler = JRuleThingHandler.get();
        thingHandler.setThingManager(thingManager);
        thingHandler.setThingRegistry(thingRegistry);

        final JRuleItemHandler itemHandler = JRuleItemHandler.get();
        itemHandler.setItemRegistry(itemRegistry);
        itemHandler.setItemChannelLinkRegistry(itemChannelLinkRegistry);
        itemHandler.setMetadataRegistry(metadataRegistry);
        logDebug("JRuleHandler()");
    }

    public synchronized final void initialize() {
        logInfo("Initializing Start Initializing JRule Automation");
        logDebug("Initializing SettingConfig name: {} config: {}", config.getClass(), config.toString());
        logDebug("Initializing JRule automation folder: {}", config.getWorkingDirectory());
        logDebug("Initializing JRule Rules folder: {}", config.getRulesDirectory());
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
        if (!initializeFolder(config.getThingsDirectory())) {
            return;
        }
        if (!initializeFolder(config.getActionsDirectory())) {
            return;
        }
        if (!initializeFolder(config.getRulesDirectory())) {
            return;
        }

        logInfo("Initializing JRule writing external Jars: {}", config.getJarDirectory());

        // Extract and copy jrules.jar as well as openhab-core.jar
        jarExtractor.extractJRuleJar(compiler.getJarPath(JRuleCompiler.JAR_JRULE_NAME));
        jarExtractor.extractOpenhabCoreJar(compiler.getJarPath(JRuleCompiler.JAR_OPENHAB_CORE_NAME));

        // Generate source files for all items and things
        Collection<Item> items = itemRegistry.getItems();
        Collection<Thing> things = thingRegistry.getAll();
        things.forEach(thingGenerator::generateThingSource);
        things.stream().filter(thing -> thing.getHandler() != null).filter(
                thing -> thing.getHandler().getServices().stream().anyMatch(ThingActions.class::isAssignableFrom))
                .forEach(actionGenerator::generateActionSource);

        // Compilation of items
        compileGeneratedSourcesInternal();

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
        JRuleEngine.get().dispose();
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
        JRuleThingRegistry.clear();
        logDebug("Dispose complete");
    }

    private void createRuleInstances() {
        final List<URL> urlList = new ArrayList<>();
        final List<URL> extLibPath = compiler.getExtLibsAsUrls();
        final List<URL> jarRulesPath = compiler.getJarRulesAsUrls();
        try {
            urlList.add(new File(config.getSourceDirectory()).toURI().toURL());
            urlList.add(new File(config.getRulesRootDirectory()).toURI().toURL());
        } catch (MalformedURLException x) {
            logError("Failed to build class path for creating rule instance");
        }
        urlList.addAll(extLibPath);
        urlList.addAll(jarRulesPath);
        logDebug("Classloader URLs: {}", urlList);

        final JRuleClassLoader loader = new JRuleClassLoader(urlList.toArray(URL[]::new),
                JRuleUtil.class.getClassLoader());

        // Load item/thing/action classes first
        compiler.loadClassesFromFolder(loader, new File(config.getSourceDirectory()), JRuleConfig.GENERATED_PACKAGE,
                false);
        // Clear registry from old items
        JRuleItemRegistry.clear();
        JRuleThingRegistry.clear();

        // Reload Items class - this will also instantiate all items and load them to the registry
        try {
            Class<?> cls = Class.forName(config.getGeneratedItemPackage() + ".JRuleItems", true, loader);
            cls.getDeclaredConstructor().newInstance();
            logger.info("Instantiated JRuleItems class");
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException
                | InvocationTargetException | Error e) {
            logger.error("Could not instantiate JRuleItems file", e);
        }

        // Reload Things class - this will also instantiate all things and load them to the registry
        try {
            Class<?> cls = Class.forName(config.getGeneratedThingPackage() + ".JRuleThings", true, loader);
            cls.getDeclaredConstructor().newInstance();
            logger.info("Instantiated JRuleThings class");
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException
                | InvocationTargetException | Error e) {
            logger.error("Could not instantiate JRuleThings file", e);
        }

        // Reload Actions class - this will also instantiate all actions
        try {
            Class<?> cls = Class.forName(config.getGeneratedActionPackage() + ".JRuleActions", true, loader);
            cls.getDeclaredConstructor().newInstance();
            logger.info("Instantiated JRuleActions class");
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException
                | InvocationTargetException | Error e) {
            logger.error("Could not instantiate JRuleActions file", e);
        }

        // Load rules that refer to the items
        compiler.loadClassesFromFolder(loader, new File(config.getRulesRootDirectory()), config.getRulesPackage(),
                true);
        compiler.loadClassesFromJar(loader, new File(config.getJarRulesDirectory()), config.getRulesPackage(), true);
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
    private synchronized Boolean compileGeneratedSourcesInternal() {
        logInfo("Compiling generated sources");
        itemGenerator.generateItemsSource(itemRegistry.getItems(), metadataRegistry);
        itemNameGenerator.generateItemNamesSource(itemRegistry.getItems(), metadataRegistry);
        thingGenerator.generateThingsSource(thingRegistry.getAll());
        Set<Thing> filteredThings = thingRegistry.getAll().stream().filter(thing -> {
            boolean b = thing.getHandler() != null;
            logDebug("has handler? -> {}", b);
            return b;
        }).filter(thing -> {
            boolean hasThingActions = thing.getHandler().getServices().stream()
                    .anyMatch(ThingActions.class::isAssignableFrom);
            logDebug("has thingActions? -> {}", hasThingActions);
            return hasThingActions;
        }).collect(Collectors.toSet());
        logDebug("generating actions for: {}",
                filteredThings.stream().map(thing -> thing.getUID().toString()).collect(Collectors.joining(", ")));
        filteredThings.forEach(actionGenerator::generateActionSource);
        actionGenerator.generateActionsSource(filteredThings);
        Boolean result = false;
        if (compiler.compileGeneratedSource()) {

            logInfo("Creating " + JRuleCompiler.JAR_JRULE_GENERATED_JAR_NAME);
            JRuleUtil.createJarFile(config.getSourceDirectory(),
                    compiler.getJarPath(JRuleCompiler.JAR_JRULE_GENERATED_JAR_NAME));
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
        eventSubscriber.resumeEventDelivery();
        return true;
    }

    @Nullable
    private Boolean compileAndReloadGeneratedSources() {
        if (compileGeneratedSourcesInternal()) {
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
                delayedItemsCompiler.call(this::compileGeneratedSourcesInternal);
            } else if (eventType.equals(ItemAddedEvent.TYPE) || event.getType().equals(ItemUpdatedEvent.TYPE)) {
                try {
                    logDebug("Added/updatedType: {}", evt);
                    Item item = itemRegistry.getItem(itemName);
                    delayedItemsCompiler.call(this::compileAndReloadGeneratedSources);
                } catch (ItemNotFoundException e) {
                    logDebug("Could not find new item", e);
                }
            } else {
                logDebug("Failed to do something with item event");
            }
        } else if (property.equals(JRuleEventSubscriber.PROPERTY_THING_REGISTRY_EVENT)) {
            Event event = (Event) evt.getNewValue();
            if (event == null) {
                logDebug("Event value null. ignoring: {}", evt);
                return;
            }
            String eventType = event.getType();
            String thingUID = JRuleUtil.getThingFromTopic(event.getTopic());

            if (eventType.equals(ThingRemovedEvent.TYPE)) {
                logDebug("Thing Removed: {}", evt);
                deleteSourceFileForThing(thingUID);
                deleteSourceFileForAction(thingUID);
                delayedItemsCompiler.call(this::compileGeneratedSourcesInternal);
            } else if (eventType.equals(ThingAddedEvent.TYPE)) {
                logDebug("Thing Added: {}", evt);
                Thing thing = thingRegistry.get(new ThingUID(thingUID));
                if (thing != null) {
                    thingGenerator.generateThingSource(thing);
                    if (thing.getHandler() != null && thing.getHandler().getServices().stream()
                            .anyMatch(ThingActions.class::isAssignableFrom)) {
                        actionGenerator.generateActionSource(thing);
                    }
                    delayedItemsCompiler.call(this::compileAndReloadGeneratedSources);
                }
            } else if (event.getType().equals(ThingUpdatedEvent.TYPE)) {
                logDebug("Thing Updated: {}", evt);
                ThingUpdatedEvent thingUpdatedEvent = (ThingUpdatedEvent) event;

                if (!Objects.equals(thingUpdatedEvent.getOldThing(), thingUpdatedEvent.getThing())) {
                    Thing thing = thingRegistry.get(new ThingUID(thingUID));
                    if (thing != null) {
                        thingGenerator.generateThingSource(thing);
                        if (thing.getHandler() != null && thing.getHandler().getServices().stream()
                                .anyMatch(ThingActions.class::isAssignableFrom)) {
                            actionGenerator.generateActionSource(thing);
                        }
                        delayedItemsCompiler.call(this::compileAndReloadGeneratedSources);
                    }
                } else {
                    logDebug("Thing updated, but no real change");
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
        final Path pathRules = new File(config.getRulesDirectory()).toPath();
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

    private synchronized void deleteSourceFileForThing(String thingUID) {
        deleteFile(new File(new StringBuilder().append(config.getThingsDirectory()).append(File.separator)
                .append(config.getGeneratedItemPrefix()).append(thingUID.replace(':', '_'))
                .append(JRuleConstants.JAVA_FILE_TYPE).toString()));
    }

    private synchronized void deleteSourceFileForAction(String thingUID) {
        deleteFile(new File(new StringBuilder().append(config.getActionsDirectory()).append(File.separator)
                .append(config.getGeneratedItemPrefix())
                .append(JRuleActionClassGenerator.getActionFriendlyName(thingUID)).append(JRuleConstants.JAVA_FILE_TYPE)
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
        private final static Logger logger = LoggerFactory.getLogger(JRuleClassLoader.class);

        public JRuleClassLoader(URL[] urls, @Nullable ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                for (URL url : getURLs()) {
                    if (url.getProtocol().equals("file")) {
                        File classFile = new File(url.getFile(),
                                name.replaceAll("\\.", "/") + JRuleConstants.CLASS_FILE_TYPE);
                        try (InputStream is = new FileInputStream(classFile)) {
                            byte[] buf = is.readAllBytes();
                            return defineClass(name, buf, 0, buf.length);
                        }
                    }
                }
                return super.loadClass(name);
            } catch (FileNotFoundException e) {
                return super.loadClass(name);
            } catch (IOException e) {
                JRuleLog.warn(logger, LOG_NAME_HANDLER,
                        "Trouble loading class {} from file system, deferring to parent clasloader: {}", name,
                        e.toString());
                return super.loadClass(name);
            }
        }
    }
}
