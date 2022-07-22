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
package org.openhab.automation.jrule.internal.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Vector;

import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.rules.JRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {} Utilities
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleJarExtractor {

    private final static int BUFFER_SIZE = 4096;
    private static final Logger logger = LoggerFactory.getLogger(JRuleJarExtractor.class);
    private static final String LOG_NAME_JAR = "JRuleJar";

    public void extractJRuleJar(String jarFilePath) {
        Vector<Class<?>> loadedClasses = getLoadedClasses(JRule.class.getClassLoader());
        if (loadedClasses == null || loadedClasses.isEmpty()) {
            JRuleLog.error(logger, LOG_NAME_JAR, "Failed to write and extract jar: {}", jarFilePath);
            return;
        }
        Class<?> clazz = loadedClasses.get(0);
        final URL jarUrl = getClassLocation(clazz);
        copyJRulesJarToFolder(jarUrl, jarFilePath);
    }

    private Vector<Class<?>> getLoadedClasses(ClassLoader classLoader) {
        Field field = null;
        try {
            field = ClassLoader.class.getDeclaredField("classes");
            field.setAccessible(true);
        } catch (Throwable t) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            final Vector<Class<?>> classes = (Vector<Class<?>>) field.get(classLoader);
            return classes;
        } catch (IllegalArgumentException | IllegalAccessException ignored) {
        }
        return null;
    }

    private void copyJRulesJarToFolder(URL jarUrl, String jarFilePath) {
        if (jarUrl == null) {
            JRuleLog.debug(logger, LOG_NAME_JAR, "ignoring jar: {}", jarUrl);
            return;
        }
        JRuleLog.info(logger, LOG_NAME_JAR, "Extracting jar: {} to: {}", jarFilePath, jarFilePath);
        try {
            copyFile(jarUrl, jarFilePath);
        } catch (IOException x) {
            JRuleLog.error(logger, LOG_NAME_JAR, "Failed to write file to: {}", jarFilePath, x);
        }
    }

    private URL getClassLocation(Class<?> clazz) {
        final ProtectionDomain pd = clazz.getProtectionDomain();
        final CodeSource cs = pd.getCodeSource();
        if (cs == null) {
            JRuleLog.error(logger, LOG_NAME_JAR, "Code source is null failed to get location for jarClass: {}", clazz);
            return null;
        }
        return cs.getLocation();
    }

    private void copyFile(URL source, String destFileName) throws IOException {
        final File destFile = new File(destFileName);
        final File destDirectory = destFile.getParentFile();
        destDirectory.mkdirs();
        try (InputStream in = source.openStream(); OutputStream out = new FileOutputStream(destFile)) {
            writeStream(in, out);
        }
    }

    private void writeStream(InputStream in, OutputStream out) throws IOException {
        final byte[] bytes = new byte[BUFFER_SIZE];
        for (;;) {
            int length = in.read(bytes);
            if (length == -1) {
                return;
            }
            out.write(bytes, 0, length);
        }
    }
}
