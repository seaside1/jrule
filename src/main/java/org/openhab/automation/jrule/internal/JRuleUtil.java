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
package org.openhab.automation.jrule.internal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.internal.handler.JRuleHandler;
import org.openhab.core.common.ThreadPoolManager;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleUtil} Utilities
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleUtil {
    private static final String BACKSLASH_ESCAPE = "\\";

    private static final String SEPARATOR = "/";

    private static final String ITEMS_START = "items/";

    private static final Logger logger = LoggerFactory.getLogger(JRuleUtil.class);

    protected static final String ERROR_RESOURCE = "Can't find resource: {}";
    protected static final String DEBUG_RESOURCE = "Resources: {}";
    protected static final ScheduledExecutorService scheduler = ThreadPoolManager
            .getScheduledPool(ThreadPoolManager.THREAD_POOL_NAME_COMMON);

    public static String URLReader(URL url, Charset encoding) throws IOException {
        try (InputStream in = url.openStream()) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes, encoding);
        }
    }

    public static String removeExtension(@NonNull String name, String extention) {
        return name.substring(0, name.lastIndexOf(extention));
    }

    public static @Nullable String getResourceAsString(@Nullable URL resource) {
        try {
            @SuppressWarnings("null")
            final InputStream is = resource.openStream();
            final byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(ERROR_RESOURCE, e.getMessage());
        }
        return null;
    }

    public static <T> CompletableFuture<T> delayedExecution(long delay, TimeUnit unit) {
        Executor delayedExecutor = CompletableFuture.delayedExecutor(delay, unit, scheduler);
        return CompletableFuture.supplyAsync(() -> null, delayedExecutor);
    }

    public static <T> CompletableFuture<T> scheduleAsync(Supplier<CompletableFuture<T>> command, long delay,
            TimeUnit unit) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        scheduler.schedule((() -> {
            command.get().thenAccept(t -> {
                completableFuture.complete(t);
            }).exceptionally(t -> {
                completableFuture.completeExceptionally(t);
                return null;
            });
        }), delay, unit);
        return completableFuture;
    }

    public static <T> ScheduledFuture<T> scheduleCallable(ScheduledExecutorService executor, Callable<T> callable,
            long delay, TimeUnit unit) {
        return executor.schedule(callable, delay, unit);
    }

    public static byte[] getResourceAsBytes(@Nullable URL resource) {
        try {
            @SuppressWarnings("null")
            final InputStream is = resource.openStream();
            return is.readAllBytes();
        } catch (IOException e) {
            logger.error(ERROR_RESOURCE, e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("null")
    public static @Nullable URL getResourceUrl(String resource) {
        URL resourceUrl = null;
        try {
            resourceUrl = FrameworkUtil.getBundle(JRuleHandler.class).getResource(resource);
        } catch (Exception x) {
            logger.error("Exception caught trying to load resource as osgi resource: {} message: {}", resource,
                    x.getMessage());
            try {
                resourceUrl = JRuleUtil.class.getClassLoader().getResource(resource);
            } catch (Exception x2) {
                logger.error("Exception caught trying to load class resource: {} message: {}", resource,
                        x2.getMessage());

            }
        }
        if (resourceUrl == null) {
            logger.error(ERROR_RESOURCE, resource);
            return null;
        }
        logger.debug(DEBUG_RESOURCE, resourceUrl);
        return resourceUrl;
    }

    public static boolean isNotEmpty(String oldValue) {
        return oldValue != null && !oldValue.isEmpty();
    }

    @SuppressWarnings("rawtypes")
    public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new ClassNotFoundException("Could not get classloder");
        }
        final String pathFromPackage = packageName.replace('.', '/');
        final Enumeration<URL> resources = classLoader.getResources(pathFromPackage);
        logger.debug("Get ResClasses enum: {}", resources.hasMoreElements());
        final List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            final URLConnection connection = resource.openConnection();
            InputStream openStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(openStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                logger.debug("line: {}", line);
            }
            final FileNameMap fileNameMap = URLConnection.getFileNameMap();
            URI uri;
            try {
                uri = connection.getURL().toURI();
                logger.debug("Uri: {}", uri.toString());
            } catch (URISyntaxException e) {
                logger.debug("Uri syntax exception", e);
            }
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        final List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        final File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().contains(".")) {
                    // LOgger warn
                    continue;
                }
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(JRuleConstants.CLASS_FILE_TYPE)) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6),
                        false, JRuleUtil.class.getClassLoader()));
            }
        }
        return classes;
    }

    public static String getFileAsString(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            logger.error("Failed to read file", e);
        }

        return null;
    }

    public static void writeFile(byte[] bytes, String file) {
        try {
            FileOutputStream out = new FileOutputStream(new File(file));
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            logger.error("Failed to write file: {}", file, e);
        }
    }

    public static File createJarFile(String inputDirectory, String targetFile) {
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            JarOutputStream target = new JarOutputStream(new FileOutputStream(targetFile), manifest);
            File inputDir = new File(inputDirectory);
            for (File nestedFile : inputDir.listFiles()) {
                add(JRuleConstants.EMPTY, nestedFile, target);
            }
            target.close();
        } catch (IOException e) {
            logger.error("Error creating jar", e);
        }
        return new File(targetFile);
    }

    private static void add(String parents, File source, JarOutputStream target) throws IOException {
        BufferedInputStream in = null;
        try {
            String name = (parents + source.getName()).replace(BACKSLASH_ESCAPE, SEPARATOR);
            if (source.isDirectory()) {
                if (!name.isEmpty()) {
                    if (!name.endsWith(SEPARATOR)) {
                        name += SEPARATOR;
                    }
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile : source.listFiles()) {
                    add(name, nestedFile, target);
                }
                return;
            }

            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String getItemNameFromTopic(@NonNull String topic) {
        if (topic.isEmpty()) {
            return JRuleConstants.EMPTY;
        }
        final int start = topic.indexOf(ITEMS_START) + ITEMS_START.length();
        int end = topic.lastIndexOf(SEPARATOR);
        if (start > end) {
            end = topic.length();
        }
        return end > 0 && end > start ? topic.substring(start, end) : JRuleConstants.EMPTY;
    }
}
