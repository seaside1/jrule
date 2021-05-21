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
package org.openhab.automation.jrule.internal.compiler;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleCompiler}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleCompiler {

    private static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";
    private static final String CLASSPATH_OPTION = "-classpath";
    private static final String JRULE_USER_JAVA = "JRuleUser.java";
    private static final String JAR_USER_RULES = "user-rules.jar";
    public static final String JAR_JRULE_NAME = "jrule.jar";
    public static final String JAR_JRULE_ITEMS_NAME = "jrule-items.jar";
    public static final String JAR_ECLIPSE_ANNOTATIONS_NAME = "org.eclipse.jdt.annotation-2.2.100.jar";
    public static final String JAR_SLF4J_API_NAME = "slf4j-api-1.7.16.jar";

    private final Logger logger = LoggerFactory.getLogger(JRuleCompiler.class);

    private final JRuleConfig jRuleConfig;

    public JRuleCompiler(JRuleConfig jRuleConfig) {
        this.jRuleConfig = jRuleConfig;
    }

    public void loadClasses(ClassLoader classLoader, File classFolder, String classPackage, boolean createInstance) {
        try {
            final File[] classItems = classFolder.listFiles(GeneratedFileNameFilter.CLASS_FILTER);
            if (classItems == null || classItems.length == 0) {
                logger.info("Found no user defined java rules to load into memory in folder: {}",
                        classFolder.getAbsolutePath());
                return;
            }
            logger.info("Number of Java Rules classes to load in to memory: {} folder: {}", classItems.length,
                    classFolder.getAbsolutePath());
            Arrays.stream(classItems).forEach(classItem -> logger.debug("Attempting to load class: {}", classItem));

            Arrays.stream(classItems).forEach(classItem -> {
                try {
                    Class<?> loadedClass = classLoader.loadClass(classPackage
                            + JRuleUtil.removeExtension(classItem.getName(), JRuleConstants.CLASS_FILE_TYPE));
                    if (createInstance) {
                        Object obj = loadedClass.getDeclaredConstructor().newInstance();
                        logger.debug("Loaded and instance: {} obj: {}", classItem.getName(), obj);

                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                        | SecurityException e) {
                    logger.error("Could not find class", e);
                }
            });
        } catch (Exception e) {
            logger.error("error instance", e);
        }
    }

    public void compileClass(File javaSourceFile, String classPath) {
        logger.debug("Compiling java Source file: {} classPath: {}", javaSourceFile, classPath);
        if (javaSourceFile.exists()) {
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            List<String> optionList = new ArrayList<String>();
            optionList.add(CLASSPATH_OPTION);
            optionList.add(classPath);
            Iterable<? extends JavaFileObject> compilationUnit = fileManager
                    .getJavaFileObjectsFromFiles(Arrays.asList(javaSourceFile));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
                    compilationUnit);
            try {
                if (task.call()) {
                    logger.debug("Compilation of class successfull!");
                } else {
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        logger.info("Error on line {} in {}", diagnostic.getLineNumber(),
                                diagnostic.getSource().toUri());
                    }
                }
                fileManager.close();
            } catch (Exception x) {
                logger.error("error", x);
            }
        }
    }

    public File[] getJavaSourceItemsFromFolder(File folder) {
        return folder.listFiles(GeneratedFileNameFilter.JAVA_FILTER);
    }

    public void compileIitemsInFolder(File itemsFolder) {
        final String itemsClassPath = System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator
                + getJarPath(JAR_JRULE_NAME);
        logger.debug("Compiling items in folder: {}", itemsFolder.getAbsolutePath());
        final File[] javaItems = getJavaSourceItemsFromFolder(itemsFolder);
        final File[] classItems = itemsFolder.listFiles(GeneratedFileNameFilter.CLASS_FILTER);
        final Set<String> classNames = new HashSet<>();
        Arrays.stream(classItems).forEach(classItem -> classNames
                .add(JRuleUtil.removeExtension(classItem.getName(), JRuleConstants.CLASS_FILE_TYPE)));

        logger.debug("ClassNameSetSize: {}", classNames.size());
        Arrays.stream(javaItems)
                .filter(javaItem -> !classNames
                        .contains(JRuleUtil.removeExtension(javaItem.getName(), JRuleConstants.JAVA_FILE_TYPE)))
                .forEach(javaItem -> compileClass(javaItem, itemsClassPath));
        classNames.clear();
    }

    public String getJarPath(String jarName) {
        return new StringBuilder().append(jRuleConfig.getJarDirectory()).append(File.separator).append(jarName)
                .toString();
    }

    public void compileItems() {
        compileIitemsInFolder(new File(jRuleConfig.getItemsDirectory()));
    }

    public void compileRules() {
        String rulesClassPath = //
                System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator //
                        + getJarPath(JAR_JRULE_ITEMS_NAME) + File.pathSeparator //
                        + getJarPath(JAR_JRULE_NAME) + File.pathSeparator //
                        + getJarPath(JAR_ECLIPSE_ANNOTATIONS_NAME) + File.pathSeparator //
                        + getJarPath(JAR_SLF4J_API_NAME) + File.pathSeparator;
        logger.debug("Compiling rules in folder: {}", jRuleConfig.getRulesDirectory());
        File userFile = new File(jRuleConfig.getRulesDirectory() + File.separator + JRULE_USER_JAVA);
        if (userFile.exists()) {
            compileClass(userFile, rulesClassPath);
            JRuleUtil.createJarFile(jRuleConfig.getRulesRootDirectory(), getJarPath(JAR_USER_RULES));
        }
        File jarUserFilerFile = new File(getJarPath(JAR_USER_RULES));
        final String finalClassPath = jarUserFilerFile.exists()
                ? rulesClassPath + File.separator + jarUserFilerFile.getAbsolutePath()
                : rulesClassPath;
        final File[] javaFiles = new File(jRuleConfig.getRulesDirectory())
                .listFiles(GeneratedFileNameFilter.JAVA_FILTER);
        if (javaFiles == null || javaFiles.length == 0) {
            logger.info("Found no java rules to compile and use in folder: {}, no rules are loaded",
                    jRuleConfig.getRulesDirectory());
            return;
        }
        Arrays.stream(javaFiles).forEach(javaFile -> compileClass(javaFile, finalClassPath));
    }

    private static class GeneratedFileNameFilter implements FilenameFilter {

        private static final GeneratedFileNameFilter JAVA_FILTER = new GeneratedFileNameFilter(
                JRuleConstants.JAVA_FILE_TYPE);
        private static final GeneratedFileNameFilter CLASS_FILTER = new GeneratedFileNameFilter(
                JRuleConstants.CLASS_FILE_TYPE);
        private final String fileType;

        public GeneratedFileNameFilter(String fileType) {
            this.fileType = fileType;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(fileType);
        }
    }
}
