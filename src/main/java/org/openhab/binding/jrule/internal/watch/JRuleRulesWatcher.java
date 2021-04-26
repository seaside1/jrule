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
package org.openhab.binding.jrule.internal.watch;

import static java.nio.file.StandardWatchEventKinds.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleRulesWatcher}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleRulesWatcher implements Runnable {

    private static final String BASIC_IS_DIRECTORY = "basic:isDirectory";

    private final Path watchFolder;

    private final Logger logger = LoggerFactory.getLogger(JRuleRulesWatcher.class);

    public static final String PROPERTY_ENTRY_CREATE = "ENTRY_CREATE";
    public static final String PROPERTY_ENTRY_MODIFY = "ENTRY_MODIFY";
    public static final String PROPERTY_ENTRY_DELETE = "ENTRY_DELETE";

    private final PropertyChangeSupport propertyChangeSupport;

    public JRuleRulesWatcher(Path watchFolder) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.watchFolder = watchFolder;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        logger.debug("Adding listener for watcher");
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        logger.debug("Adding listener for Watcher");
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }

    @Override
    public void run() {
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(watchFolder, BASIC_IS_DIRECTORY);
            if (!isFolder) {
                logger.error("Failed to watch folder since it is not a directory: {}",
                        watchFolder.toFile().getAbsolutePath());
                return;
                // return false;
            }
        } catch (IOException ioe) {
            logger.error("Failed to start watching folder: {}", watchFolder.toFile().getAbsolutePath(), ioe);
            return;
        }
        logger.debug("Watching for rule changes: {}", watchFolder);
        FileSystem fs = watchFolder.getFileSystem();
        try {
            WatchService service = fs.newWatchService();
            watchFolder.register(service, ENTRY_CREATE);
            watchFolder.register(service, ENTRY_MODIFY);
            watchFolder.register(service, ENTRY_DELETE);
            WatchKey key = null;
            while (true) {
                key = service.take();
                Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue;
                    } else if (ENTRY_CREATE == kind) {
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        logger.debug("New Path created in watchFolder");
                        propertyChangeSupport.firePropertyChange(PROPERTY_ENTRY_CREATE, null, newPath);
                    } else if (ENTRY_MODIFY == kind) {
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        logger.debug("New path modified: {}", newPath);
                        propertyChangeSupport.firePropertyChange(PROPERTY_ENTRY_MODIFY, null, newPath);
                    } else if (ENTRY_DELETE == kind) {
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        logger.debug("New path deleted: {}", newPath);
                        propertyChangeSupport.firePropertyChange(PROPERTY_ENTRY_DELETE, null, newPath);
                    }
                }

                if (!key.reset()) {
                    break; // loop
                }
            }
        } catch (InterruptedException e) {
            logger.debug("Watcher Thread interrupted, closing down");
            return;
        } catch (Exception e) {
            logger.error("Folder watcher terminated due to exception", e);
            return;
        }
    }
}
