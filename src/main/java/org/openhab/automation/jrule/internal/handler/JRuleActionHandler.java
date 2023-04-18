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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.HttpMethod;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.exception.JRuleRuntimeException;
import org.openhab.core.io.net.exec.ExecUtil;
import org.openhab.core.io.net.http.HttpUtil;

/**
 * The {@link JRuleEventHandler} is responsible for handling commands and status
 * updates for JRule
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleActionHandler {

    private static volatile JRuleActionHandler instance = null;

    private JRuleActionHandler() {
    }

    public static JRuleActionHandler get() {
        if (instance == null) {
            synchronized (JRuleActionHandler.class) {
                if (instance == null) {
                    instance = new JRuleActionHandler();
                }
            }
        }
        return instance;
    }

    public void executeCommandLine(String... commandLine) {
        ExecUtil.executeCommandLine(commandLine);
    }

    public String executeCommandAndAwaitResponse(Duration timeout, String... commandLine) {
        return ExecUtil.executeCommandLineAndWaitResponse(timeout, commandLine);
    }

    /**
     * Sends a GET-HTTP request with the given request headers, and timeout in ms, and returns the result as a String
     * 
     * @param url Target URL
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled
     * @return Result as String
     */
    public String sendHttpGetRequest(String url, @Nullable Map<String, String> headers, @Nullable Duration timeout) {
        try {
            return HttpUtil.executeUrl(HttpMethod.GET, url, mapToProperties(headers), null, null,
                    getTimeoutAsInt(timeout));
        } catch (IOException e) {
            throw new JRuleRuntimeException("Error executing Http action", e);
        }
    }

    /**
     * Sends a PUT-HTTP request with the given content, request headers, and timeout in ms, and returns the result as a
     * String
     * 
     * @param url Target URL
     * @param contentType @see javax.ws.rs.core.MediaType
     * @param content Request content
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled
     * @return Result as String
     */
    public String sendHttpPutRequest(String url, String contentType, @Nullable String content,
            Map<String, String> headers, @Nullable Duration timeout) {
        try {
            return HttpUtil.executeUrl(HttpMethod.PUT, url, mapToProperties(headers),
                    content != null ? new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)) : null,
                    contentType, getTimeoutAsInt(timeout));
        } catch (IOException e) {
            throw new JRuleRuntimeException("Error executing Http action", e);
        }
    }

    /**
     * Sends a POST-HTTP request with the given content, request headers, and timeout in ms, and returns the result as a
     * String
     * <br/>
     * 
     * @param url Target URL
     * @param contentType @see javax.ws.rs.core.MediaType
     * @param content Request content
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled
     * @return Result as String
     */
    public String sendHttpPostRequest(String url, String contentType, @Nullable String content,
            Map<String, String> headers, @Nullable Duration timeout) {
        try {
            return HttpUtil.executeUrl(HttpMethod.POST, url, mapToProperties(headers),
                    content != null ? new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)) : null,
                    contentType, getTimeoutAsInt(timeout));
        } catch (IOException e) {
            throw new JRuleRuntimeException("Error executing Http action", e);
        }
    }

    /**
     * Sends a DELETE-HTTP request with the given request headers, and timeout in ms, and returns the result as a String
     * 
     * @param url Target URL
     * @param headers Header parameters for the request
     * @param timeout Time after the request will be canceled
     * @return Result as String
     */
    public String sendHttpDeleteRequest(String url, Map<String, String> headers, @Nullable Duration timeout) {
        try {
            return HttpUtil.executeUrl(HttpMethod.DELETE, url, mapToProperties(headers), null, null,
                    getTimeoutAsInt(timeout));
        } catch (IOException e) {
            throw new JRuleRuntimeException("Error executing Http action", e);
        }
    }

    private static Properties mapToProperties(Map<String, String> headers) {
        if (headers == null) {
            return null;
        }
        Properties properties = new Properties();
        properties.putAll(headers);
        return properties;
    }

    private int getTimeoutAsInt(Duration timeout) {
        if (timeout == null) {
            return 0;
        }
        return (int) timeout.toMillis();
    }
}
