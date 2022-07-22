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
package org.openhab.automation.jrule.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    private static final String LOG_NAME_UTIL = "JRuleUtil";

    public static String URLReader(URL url, Charset encoding) throws IOException {
        try (InputStream in = url.openStream()) {
            byte[] bytes = in.readAllBytes();
            return new String(bytes, encoding);
        }
    }

    public static String removeExtension(@NonNull String name, String extension) {
        return name.substring(0, name.lastIndexOf(extension));
    }

    public static @Nullable String getResourceAsString(@Nullable URL resource) {
        try (InputStream is = resource.openStream()) {
            final byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            JRuleLog.error(logger, LOG_NAME_UTIL, ERROR_RESOURCE, e.getMessage());
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
            command.get().thenAccept(completableFuture::complete).exceptionally(t -> {
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
        try (InputStream is = resource.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            JRuleLog.error(logger, LOG_NAME_UTIL, ERROR_RESOURCE, e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("null")
    public static @Nullable URL getResourceUrl(String resource) {
        URL resourceUrl = null;
        try {
            resourceUrl = FrameworkUtil.getBundle(JRuleHandler.class).getResource(resource);
        } catch (Exception x) {
            JRuleLog.error(logger, LOG_NAME_UTIL,
                    "Exception caught trying to load resource as osgi resource: {} message: {}", resource,
                    x.getMessage());
            try {
                resourceUrl = JRuleUtil.class.getClassLoader().getResource(resource);
            } catch (Exception x2) {
                JRuleLog.error(logger, LOG_NAME_UTIL, "Exception caught trying to load class resource: {} message: {}",
                        resource, x2.getMessage());

            }
        }
        if (resourceUrl == null) {
            JRuleLog.error(logger, LOG_NAME_UTIL, ERROR_RESOURCE, resource);
            return null;
        }
        JRuleLog.debug(logger, LOG_NAME_UTIL, DEBUG_RESOURCE, resourceUrl);
        return resourceUrl;
    }

    public static boolean isNotEmpty(String oldValue) {
        return oldValue != null && !oldValue.isEmpty();
    }

    public static String getFileAsString(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            JRuleLog.error(logger, LOG_NAME_UTIL, "Failed to read file", e);
        }

        return null;
    }

    public static File writeFile(byte[] bytes, String file) {
        final File f = new File(file);
        try (FileOutputStream out = new FileOutputStream(f)) {
            out.write(bytes);
        } catch (IOException e) {
            JRuleLog.error(logger, LOG_NAME_UTIL, "Failed to write file: {}", file, e);
        }
        return f;
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
            JRuleLog.error(logger, LOG_NAME_UTIL, "Error creating jar", e);
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
        int end = topic.indexOf(SEPARATOR, start);
        if (start > end) {
            end = topic.length();
        }
        return end > 0 && end > start ? topic.substring(start, end) : JRuleConstants.EMPTY;
    }

    public static String packageNameToPath(String packageName) {
        return packageName.replace('.', File.separatorChar);
    }
}
