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
package org.openhab.binding.jrule.internal.handler;

import java.io.File;
import java.io.FilenameFilter;
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

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.jrule.internal.JRuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleCompiler}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleCompiler {

    private final Logger logger = LoggerFactory.getLogger(JRuleCompiler.class);

    private final JRuleConfig jRuleConfig;

    public JRuleCompiler(JRuleConfig jRuleConfig) {
        this.jRuleConfig = jRuleConfig;
    }

    public void loadClasses(ClassLoader classLoader, File classFolder, String classPackage, boolean createInstance) {
        try {
            final File[] classItems = classFolder.listFiles(GeneratedFileNameFilter.CLASS_FILTER);
            logger.debug("Number of classes to load: {} folder: {}", classItems.length, classFolder.getAbsolutePath());
            Arrays.stream(classItems).forEach(classItem -> logger.debug("Attempting to load class: {}", classItem));

            Arrays.stream(classItems).forEach(classItem -> {
                try {
                    // "org.openhab.binding.jrule.items.generated."
                    Class<?> loadedClass = classLoader.loadClass(classPackage
                            + removeExtension(classItem.getName(), GeneratedFileNameFilter.CLASS_FILE_TYPE));
                    if (createInstance) {
                        Object obj = loadedClass.newInstance();
                        logger.debug("Loaded and instance: {}", classItem.getName());

                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    logger.error("COuld not find class", e);
                }
            });
            // Class<?> loadedClass = classLoader.loadClass("org.openhab.binding.jrule.items.generated." + _MotionUl");

            // Object obj = loadedClass.newInstance();
            // logger.debug("obj: {}", obj.getClass().getSimpleName());
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
            optionList.add("-classpath");
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
                        logger.debug("Error on line {} in {}", diagnostic.getLineNumber(),
                                diagnostic.getSource().toUri());
                    }
                }
                fileManager.close();
            } catch (Exception x) {
                logger.error("eeror", x);
            }
        }
    }

    public void compileIitemsInFolder(File itemsFolder) {
        final String itemsClassPath = System.getProperty("java.class.path") + File.pathSeparator
                + "/opt/jrule/jar/jrule.jar";
        logger.debug("Compiling items in folder: {}", itemsFolder.getAbsolutePath());
        final File[] javaItems = itemsFolder.listFiles(GeneratedFileNameFilter.JAVA_FILTER);
        final File[] classItems = itemsFolder.listFiles(GeneratedFileNameFilter.CLASS_FILTER);
        final Set<String> classNames = new HashSet<>();
        Arrays.stream(classItems).forEach(classItem -> classNames
                .add(removeExtension(classItem.getName(), GeneratedFileNameFilter.CLASS_FILE_TYPE)));

        logger.debug("++ ClassNameSetSize: {}", classNames.size());
        Arrays.stream(javaItems)
                .filter(javaItem -> !classNames
                        .contains(removeExtension(javaItem.getName(), GeneratedFileNameFilter.JAVA_FILE_TYPE)))
                .forEach(javaItem -> compileClass(javaItem, itemsClassPath));
        classNames.clear();
    }

    public void compileRulesInFolder(File rulesFolder) {
        String rulesClassPath = System.getProperty("java.class.path") + File.pathSeparator
                + "/opt/jrule/jar/jruleItems.jar" + File.pathSeparator + "/opt/jrule/jar/jrule.jar" + File.pathSeparator
                + "/opt/jrule/jar/org.eclipse.jdt.annotation-2.2.100.jar" + File.pathSeparator
                + "/opt/jrule/jar/slf4j-api-1.7.16.jar";
        logger.debug("Compiling rules in folder: {}", rulesFolder.getAbsolutePath());
        final File[] javaFiles = rulesFolder.listFiles(GeneratedFileNameFilter.JAVA_FILTER);
        Arrays.stream(javaFiles).forEach(javaFile -> compileClass(javaFile, rulesClassPath));
    }

    private String removeExtension(@NonNull String name, String extention) {
        return name.substring(0, name.lastIndexOf(extention));
    }

    public void compileItems() {
        compileIitemsInFolder(new File(jRuleConfig.getWorkingDirectory() + JRuleConfig.ITEMS_DIR));
        // TODO Auto-generated method stub
    }

    public void compileRules() {
        compileRulesInFolder(new File(jRuleConfig.getWorkingDirectory() + JRuleConfig.RULES_DIR));
    }

    public void compileRule(String ruleName) {
    }

    private static class GeneratedFileNameFilter implements FilenameFilter {

        private static final String JAVA_FILE_TYPE = ".java";
        private static final String CLASS_FILE_TYPE = ".class";

        private static final String PREFIX = "_";
        private static final GeneratedFileNameFilter JAVA_FILTER = new GeneratedFileNameFilter(JAVA_FILE_TYPE);
        private static final GeneratedFileNameFilter CLASS_FILTER = new GeneratedFileNameFilter(CLASS_FILE_TYPE);

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
