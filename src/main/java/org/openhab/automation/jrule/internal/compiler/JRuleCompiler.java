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
package org.openhab.automation.jrule.internal.compiler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.openhab.automation.jrule.rules.JRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleCompiler}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleCompiler {

    private static final String ORG_OPENHAB_CORE = "org.openhab.core-";
    private static final String ORG_OPENHAB_CORE_THING = "org.openhab.core.thing-";
    private static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";
    private static final String CLASSPATH_OPTION = "-classpath";
    public static final String JAR_JRULE_NAME = "jrule.jar";
    public static final String JAR_OPENHAB_CORE_NAME = "openhab-core.jar";
    public static final String JAR_JRULE_GENERATED_JAR_NAME = "jrule-generated.jar";
    private static final String LOG_NAME_COMPILER = "JRuleCompiler";
    private static final String FRONT_SLASH = "/";
    public static final String PROPERTY_KARAF_HOME_URI = "karaf.home.uri";
    public static final String PROPERTY_KARAF_DEFAULT_REPOSITORY = "karaf.default.repository";

    private final Logger logger = LoggerFactory.getLogger(JRuleCompiler.class);

    private final JRuleConfig jRuleConfig;

    public JRuleCompiler(JRuleConfig jRuleConfig) {
        this.jRuleConfig = jRuleConfig;
    }

    public void loadClassesFromJar(ClassLoader classLoader, File sourceFolder, String onlyInPackage,
            boolean createInstance) {
        try {
            final File[] jarFiles = sourceFolder.listFiles(JRuleFileNameFilter.JAR_FILTER);
            if (jarFiles == null || jarFiles.length == 0) {
                logInfo("Found no user defined java rules to load into memory in folder: {}",
                        sourceFolder.getAbsolutePath());
                return;
            } else {
                logInfo("Found java rules to load into memory in folder: {}", sourceFolder.getAbsolutePath());
            }

            Arrays.stream(jarFiles).forEach(jarItem -> logDebug("Attempting to load jar: {}", jarItem));
            Arrays.stream(jarFiles).forEach(jarItem -> {
                logDebug("Loading instance for jar: {}", jarItem.getName());
                try (JarFile jarFile = new JarFile(jarItem)) {
                    final Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry jarEntry = jarEntries.nextElement();
                        final String jarEntryName = jarEntry.getName() == null ? "" : jarEntry.getName();
                        final int lastIndexOfSlash = jarEntryName.lastIndexOf(FRONT_SLASH) + 1;
                        if (jarEntryName.length() < 1 || jarEntry.isDirectory()
                                || !jarEntryName.endsWith(JRuleConstants.CLASS_FILE_TYPE) || lastIndexOfSlash == -1
                                || !relativePathToFullClassname(jarEntryName).startsWith(onlyInPackage)) {
                            continue;
                        }

                        logger.debug("Attempting to load class from jar file {}: {}", jarItem.getAbsolutePath(),
                                jarEntryName);
                        loadClass(classLoader, relativePathToFullClassname(jarEntryName), createInstance);
                    }
                } catch (IllegalArgumentException | SecurityException | IOException e) {
                    logError("Error loading classes from jarfile {} due to {}", jarItem.getAbsolutePath(), e);
                }
                // Best effort
            });
        } catch (Exception e) {
            logError("Error loading classes from jarfile: {}", e);
        }
    }

    private String relativePathToFullClassname(String path) {
        if (path.endsWith(JRuleConstants.CLASS_FILE_TYPE)) {
            path = path.substring(0, path.lastIndexOf(JRuleConstants.CLASS_FILE_TYPE));
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path.replaceAll("/", ".");
    }

    public void loadClass(ClassLoader classLoader, String className, boolean createInstance) {
        Class<?> loadedClass;
        try {
            loadedClass = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            logDebug("Failed to load class {}: {}", className, e);
            return;
        }

        Method[] declaredMethods = loadedClass.getDeclaredMethods();
        logDebug("Loaded class {} with classLoader: {} and methods {}", loadedClass.getName(), classLoader.getName(),
                Arrays.asList(declaredMethods));

        if (createInstance) {
            if (Modifier.isAbstract(loadedClass.getModifiers())) {
                logDebug("Not creating and instance of abstract class: {}", className);
            } else if (JRule.class.isAssignableFrom(loadedClass)) {
                try {
                    final Object obj = loadedClass.getDeclaredConstructor().newInstance();
                    logDebug("Created instance: {} obj: {}", className, obj);
                } catch (Exception x) {
                    logError("Could not create create instance using default constructor: {}", className);
                    logger.error("Failed to load class", x);
                }
            }
        }
    }

    public void loadClassesFromFolder(ClassLoader classLoader, File rootFolder, String onlyInRootPackage,
            boolean createInstance) {

        String rootFolderPath = rootFolder.getAbsolutePath();

        List<String> classFiles = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(rootFolder.toPath(), FileVisitOption.FOLLOW_LINKS)) {
            classFiles = walk.filter(p -> !Files.isDirectory(p))
                    .filter(f -> f.getFileName().toString().endsWith(JRuleConstants.CLASS_FILE_TYPE))
                    .map(e -> e.toAbsolutePath().toString().replace(rootFolderPath, ""))
                    .map(this::relativePathToFullClassname).filter(e -> e.startsWith(onlyInRootPackage))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logError("Error loading classes in {} due to {}", rootFolder.getAbsolutePath(), e);
        }

        // classFiles is now in the form "packageRoot.subPackage.classname", filtered by prefix in onlyInRootPackage
        if (classFiles.isEmpty()) {
            logInfo("Found no classes to load into memory in folder: {}", rootFolder.getAbsolutePath());
        } else {
            logInfo("Number of classes to load in to memory: {} folder: {}", classFiles.size(),
                    rootFolder.getAbsolutePath());
            classFiles.forEach(classItem -> {
                logDebug("Attempting to load class: {}", classItem);
                loadClass(classLoader, classItem, createInstance);
            });
        }
    }

    public boolean compileGeneratedSource() {
        return compileGeneratedSource(new File(jRuleConfig.getSourceDirectory()));
    }

    private boolean compileGeneratedSource(File sourceFolder) {
        final String genClassPath = System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator
                + getJarPath(JAR_JRULE_NAME) + ":" + jRuleConfig.getSourceDirectory();
        logDebug("Compiling generated source in folder: {}", sourceFolder.getAbsolutePath());

        List<File> javaSourceFiles = new ArrayList<>();
        List<File> javaClassFiles = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(sourceFolder.toURI()))) {
            javaSourceFiles = paths.filter(Files::isRegularFile) // is a file
                    .filter(f -> f.getFileName().toString().endsWith(JRuleConstants.JAVA_FILE_TYPE)).map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logError("Error listing source files in folder: {}", sourceFolder.getAbsolutePath(), e);
        }
        try (Stream<Path> paths = Files.walk(Paths.get(sourceFolder.toURI()))) {
            javaClassFiles = paths.filter(Files::isRegularFile) // is a file
                    .filter(f -> f.getFileName().toString().endsWith(JRuleConstants.CLASS_FILE_TYPE)).map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logError("Error listing class files in folder: {}", sourceFolder.getAbsolutePath(), e);
        }

        Map<String, File> classFiles = new HashMap<>();
        javaClassFiles.forEach(classFile -> classFiles
                .put(JRuleUtil.removeExtension(classFile.getName(), JRuleConstants.CLASS_FILE_TYPE), classFile));

        Map<String, File> sourceFiles = new HashMap<>();
        javaSourceFiles.forEach(sourceFile -> sourceFiles
                .put(JRuleUtil.removeExtension(sourceFile.getName(), JRuleConstants.JAVA_FILE_TYPE), sourceFile));

        // First delete any class files with no corresponding source file
        classFiles.keySet().stream().filter(className -> !sourceFiles.containsKey(className))
                .forEach(className -> classFiles.get(className).delete());
        // Will trigger compilation of any missing or old item java files
        return compile(List.of(new File(jRuleConfig.getItemsDirectory(), "JRuleItems.java"),
                new File(jRuleConfig.getItemsDirectory(), "JRuleItemNames.java"),
                new File(jRuleConfig.getThingsDirectory(), "JRuleThings.java"),
                new File(jRuleConfig.getActionsDirectory(), "JRuleActions.java")), genClassPath);
    }

    public boolean compile(List<File> javaSourceFiles, String classPath) {
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            logError(
                    "Failed to get compiler, are you sure you are using a JDK? ToolProvider.getSystemJavaCompiler() returned null");
            return false;
        }
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        final List<String> optionList = new ArrayList<>();
        optionList.add(CLASSPATH_OPTION);
        final String openhabCoreJar = getOpenhabCoreJar().map(s -> s + File.pathSeparator).orElse("");
        logDebug("Openhab-Core Jar: {}", openhabCoreJar);
        final String openhabCoreThingJar = getOpenhabCoreThingJar().map(s -> s + File.pathSeparator).orElse("");
        logDebug("Openhab-Core-Thing Jar: {}", openhabCoreThingJar);
        String cp = openhabCoreJar + System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator
                + openhabCoreThingJar + System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator + classPath;
        optionList.add(cp);
        logDebug("1337Compiling classes using classpath: {}", cp);
        javaSourceFiles.stream().filter(javaSourceFile -> javaSourceFile.exists() && javaSourceFile.canRead())
                .forEach(javaSourceFile -> logDebug("Compiling java Source file: {}", javaSourceFile));

        final Iterable<? extends JavaFileObject> compilationUnit = fileManager
                .getJavaFileObjectsFromFiles(javaSourceFiles);
        final JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
                compilationUnit);
        try {
            if (task.call()) {
                logDebug("Compilation of classes successfully!");
                return true;
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    logError("Error on line {} in {}: {}", diagnostic.getLineNumber(),
                            diagnostic.getSource() == null ? "" : diagnostic.getSource().toUri(),
                            diagnostic.getMessage(Locale.getDefault()));
                }
            }
            fileManager.close();
        } catch (Exception x) {
            logError("Compiler threw error {}", x.toString());
        }

        return false;
    }

    private Optional<String> getOpenhabCoreJar() {
        return getOpenHABJar(ORG_OPENHAB_CORE);
    }

    private Optional<String> getOpenhabCoreThingJar() {
        return getOpenHABJar(ORG_OPENHAB_CORE_THING);
    }

    private Optional<String> getOpenHABJar(String jarPrefix) {
        if (!System.getProperties().containsKey(PROPERTY_KARAF_HOME_URI)
                && !System.getProperties().containsKey(PROPERTY_KARAF_DEFAULT_REPOSITORY)) {
            logWarn("required system properties does not exist [{}]",
                    !System.getProperties().containsKey(PROPERTY_KARAF_HOME_URI) ? PROPERTY_KARAF_HOME_URI
                            : PROPERTY_KARAF_DEFAULT_REPOSITORY);
            return Optional.empty();
        }
        String openhabJars = System.getProperty(PROPERTY_KARAF_HOME_URI).replace("file:", "")
                + System.getProperty(PROPERTY_KARAF_DEFAULT_REPOSITORY);
        logDebug("Openhab Jars path: {}", openhabJars);
        Optional<String> coreJarPath;

        try (Stream<Path> stream = Files.walk(Paths.get(openhabJars))) {
            coreJarPath = stream.filter(path -> path.getFileName().toString().endsWith(JRuleConstants.JAR_FILE_TYPE))
                    .filter(path -> path.getFileName().toString().startsWith(jarPrefix))
                    .map(path -> path.toFile().getAbsolutePath()).findFirst();
        } catch (IOException e) {
            logError(e.getMessage());
            return Optional.empty();
        }
        return coreJarPath;
    }

    public String getJarPath(String jarName) {
        return new StringBuilder().append(jRuleConfig.getJarDirectory()).append(File.separator).append(jarName)
                .toString();
    }

    public boolean compileRules() {
        String rulesClassPath = //
                System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator //
                        + getJarPath(JAR_JRULE_GENERATED_JAR_NAME) + File.pathSeparator //
                        + getJarPath(JAR_JRULE_NAME) + File.pathSeparator; //
        String extLibPath = getExtLibPaths();
        logDebug("extLibPath: {}", extLibPath);
        if (extLibPath != null && !extLibPath.isEmpty()) {
            rulesClassPath = rulesClassPath.concat(extLibPath);
        }
        logDebug("Compiling rules in folder: {}", jRuleConfig.getRulesDirectory());

        try (Stream<Path> paths = Files.walk(Paths.get(jRuleConfig.getRulesDirectory()),
                FileVisitOption.FOLLOW_LINKS)) {
            List<File> ruleJavaFiles = paths.filter(Files::isRegularFile) // is a file
                    .filter(f -> f.getFileName().toString().endsWith(JRuleConstants.JAVA_FILE_TYPE)).map(Path::toFile)
                    .collect(Collectors.toList());
            if (!ruleJavaFiles.isEmpty()) {
                return compile(ruleJavaFiles, rulesClassPath);
            } else {
                logWarn("Found no java rules to compile and use in folder {}", jRuleConfig.getRulesDirectory());
            }
        } catch (IOException e) {
            logError("Error listing java files in folder: {}", jRuleConfig.getRulesDirectory(), e);

        }
        return false;
    }

    public List<URL> getExtLibsAsUrls() {
        try {
            final File[] extLibsFiles = getExtLibsAsFiles();
            return Arrays.stream(extLibsFiles).map(this::getUrl).collect(Collectors.toList());
        } catch (Exception x) {
            logError("Failed to get extLib urls");
            return new ArrayList<>();
        }
    }

    public List<URL> getJarRulesAsUrls() {
        try {
            final File[] jarRulesFiles = getJarRulesAsFiles();
            return Arrays.stream(jarRulesFiles).map(this::getUrl).collect(Collectors.toList());
        } catch (Exception x) {
            logError("Failed to get jar-rules urls");
            return new ArrayList<>();
        }
    }

    private URL getUrl(File f) {
        try {
            return f.toURI().toURL();
        } catch (MalformedURLException e) {
            logError("Failed to convert to URL: {}", f.getAbsolutePath(), e);
        }
        return null;
    }

    public File[] getExtLibsAsFiles() {
        return new File(jRuleConfig.getExtlibDirectory()).listFiles(JRuleFileNameFilter.JAR_FILTER);
    }

    public File[] getJarRulesAsFiles() {
        return new File(jRuleConfig.getJarRulesDirectory()).listFiles(JRuleFileNameFilter.JAR_FILTER);
    }

    private String getExtLibPaths() {
        final File[] extLibs = getExtLibsAsFiles();
        final StringBuilder builder = new StringBuilder();
        if (extLibs != null && extLibs.length > 0) {
            Arrays.stream(extLibs).forEach(extLib -> builder.append(createJarPath(extLib)));
        }
        return builder.toString();
    }

    private String createJarPath(File extLib) {
        if (!extLib.canRead()) {
            logError("Invalid permissions for external lib jar, ignored: {}", extLib.getAbsolutePath());
            return JRuleConstants.EMPTY;
        }
        return extLib.getAbsolutePath().concat(File.pathSeparator);
    }

    private static class JRuleFileNameFilter implements FilenameFilter {

        private static final JRuleFileNameFilter JAVA_FILTER = new JRuleFileNameFilter(JRuleConstants.JAVA_FILE_TYPE);
        private static final JRuleFileNameFilter CLASS_FILTER = new JRuleFileNameFilter(JRuleConstants.CLASS_FILE_TYPE);
        private static final JRuleFileNameFilter JAR_FILTER = new JRuleFileNameFilter(JRuleConstants.JAR_FILE_TYPE);

        private final String fileType;

        public JRuleFileNameFilter(String fileType) {
            this.fileType = fileType;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(fileType);
        }
    }

    private void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, LOG_NAME_COMPILER, message, parameters);
    }

    private void logInfo(String message, Object... parameters) {
        JRuleLog.info(logger, LOG_NAME_COMPILER, message, parameters);
    }

    private void logError(String message, Object... parameters) {
        JRuleLog.error(logger, LOG_NAME_COMPILER, message, parameters);
    }

    private void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, LOG_NAME_COMPILER, message, parameters);
    }
}
