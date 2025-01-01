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
package org.openhab.automation.jrule.internal.generator;

import java.nio.charset.StandardCharsets;

import org.openhab.automation.jrule.internal.JRuleConfig;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * The {@link JRuleAbstractClassGenerator} provides common setup of Freemarker for the class generators
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleAbstractClassGenerator {
    protected final JRuleConfig jRuleConfig;
    public static final Configuration freemarkerConfiguration;

    static {
        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.29) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        freemarkerConfiguration.setClassForTemplateLoading(JRuleAbstractClassGenerator.class, "/templates");
        // From here we will set the settings recommended for new projects. These
        // aren't the defaults for backward compatibilty.

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        freemarkerConfiguration.setDefaultEncoding(StandardCharsets.UTF_8.name());

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        freemarkerConfiguration.setLogTemplateExceptions(false);

        // Wrap unchecked exceptions thrown during template processing into TemplateException-s:
        freemarkerConfiguration.setWrapUncheckedExceptions(true);

        // Do not fall back to higher scopes when reading a null loop variable:
        freemarkerConfiguration.setFallbackOnNullLoopVariable(false);
    }

    public JRuleAbstractClassGenerator(JRuleConfig jRuleConfig) {
        this.jRuleConfig = jRuleConfig;
    }
}
