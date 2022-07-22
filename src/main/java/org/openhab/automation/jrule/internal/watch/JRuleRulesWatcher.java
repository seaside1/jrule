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
package org.openhab.automation.jrule.internal.watch;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleRulesWatcher}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleRulesWatcher implements Runnable {

    private static final String BASIC_IS_DIRECTORY = "basic:isDirectory";

    private final List<Path> watchFolders;
    private WatchService watchService = null;
    private final Logger logger = LoggerFactory.getLogger(JRuleRulesWatcher.class);

    public static final String PROPERTY_ENTRY_CREATE = "ENTRY_CREATE";
    public static final String PROPERTY_ENTRY_MODIFY = "ENTRY_MODIFY";
    public static final String PROPERTY_ENTRY_DELETE = "ENTRY_DELETE";

    private static final String LOG_NAME_RULESWATCHER = "JRuleRulesWatcher";

    private final PropertyChangeSupport propertyChangeSupport;

    public JRuleRulesWatcher(List<Path> watchFolders) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.watchFolders = watchFolders;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logError("Could not start file watcher service. No rules will be loaded: {}", e);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        logDebug("Adding listener for watcher");
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        logDebug("Adding listener for Watcher");
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }

    private void registerListenerForFolder(Path watchFolder) throws IOException {
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(watchFolder, BASIC_IS_DIRECTORY);
            if (!isFolder) {
                logError("Failed to watch folder since it is not a directory: {}",
                        watchFolder.toFile().getAbsolutePath());
                return;
            }
        } catch (IOException ioe) {
            logError("Failed to start watching folder: {}", watchFolder.toFile().getAbsolutePath(), ioe);
            return;
        }
        logDebug("Watching for rule changes: {}", watchFolder);

        watchFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Override
    public void run() {
        try {
            watchFolders.stream().forEach(folder -> {
                try {
                    registerListenerForFolder(folder);
                } catch (IOException e) {
                    logger.error("Failed to setup listener for folder: {}", folder);
                }
            });
            WatchKey key;
            do {
                key = watchService.take();
                Kind<?> kind;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        logDebug("overflow");
                        continue;
                    }
                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
                    if (!(newPath.getFileName().toString().endsWith(JRuleConstants.JAVA_FILE_TYPE)
                            || newPath.getFileName().toString().endsWith(JRuleConstants.JAR_FILE_TYPE))) {
                        continue;
                    }
                    if (ENTRY_CREATE == kind) {
                        logDebug("New Path {} created in watchFolder", newPath);
                        propertyChangeSupport.firePropertyChange(PROPERTY_ENTRY_CREATE, null, newPath);
                    } else if (ENTRY_MODIFY == kind) {
                        logDebug("New path modified: {} fn: {}", newPath, newPath.getFileName());
                        propertyChangeSupport.firePropertyChange(PROPERTY_ENTRY_MODIFY, null, newPath);
                    } else if (ENTRY_DELETE == kind) {
                        logDebug("New path deleted: {}", newPath);
                        propertyChangeSupport.firePropertyChange(PROPERTY_ENTRY_DELETE, null, newPath);
                    } else {
                        logWarn("Unhandled case: {}", kind.name());
                    }
                }
            } while (key.reset());

        } catch (InterruptedException e) {
            logDebug("Folder watcher was interrupted {}", e);
        } catch (Exception e) {
            logError("Folder watcher terminated due to exception {}", e);
        } finally {
            try {
                watchService.close();
            } catch (IOException e) {
                logError("Error closing watch service: {}", e);
            }
        }
    }

    private void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, LOG_NAME_RULESWATCHER, message, parameters);
    }

    private void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, LOG_NAME_RULESWATCHER, message, parameters);
    }

    private void logError(String message, Object... parameters) {
        JRuleLog.error(logger, LOG_NAME_RULESWATCHER, message, parameters);
    }
}
