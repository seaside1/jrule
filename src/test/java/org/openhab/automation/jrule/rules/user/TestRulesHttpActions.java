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
package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemReceivedCommand;

/**
 * The {@link TestRulesHttpActions}
 *
 * @author Robert Delbrück - Initial contribution
 */
public class TestRulesHttpActions extends JRule {
    public static final String ITEM_TRIGGER_HTTP_ACTION = "Trigger_HttpAction";
    public static final String NAME_SEND_HTTP_GET = "send Http GET";
    public static final String NAME_SEND_HTTP_POST = "send Http POST";
    public static final String NAME_SEND_HTTP_PUT = "send Http PUT";
    public static final String NAME_SEND_HTTP_DELETE = "send Http DELETE";
    public static final String HTTP_ACTION_GET_COMMAND = "get";
    public static final String HTTP_ACTION_POST_COMMAND = "post";
    public static final String HTTP_ACTION_PUT_COMMAND = "put";
    public static final String HTTP_ACTION_DELETE_COMMAND = "delete";
    public static final String HTTP_GET_SOMETHING = "/get-something";
    public static final String HTTP_POST_SOMETHING = "/post-something";
    public static final String HTTP_PUT_SOMETHING = "/put-something";
    public static final String HTTP_DELETE_SOMETHING = "/delete-something";

    @JRuleName(NAME_SEND_HTTP_GET)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_HTTP_ACTION, command = HTTP_ACTION_GET_COMMAND)
    public void sendHttpGet() {
        String response = sendHttpGetRequest("http://http-mock:8080" + HTTP_GET_SOMETHING, null);
        logInfo("send Http: {}", response);
    }

    @JRuleName(NAME_SEND_HTTP_POST)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_HTTP_ACTION, command = HTTP_ACTION_POST_COMMAND)
    public void sendHttpPost() {
        String response = sendHttpPostRequest("http://http-mock:8080" + HTTP_POST_SOMETHING, null);
        logInfo("send Http: {}", response);
    }

    @JRuleName(NAME_SEND_HTTP_PUT)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_HTTP_ACTION, command = HTTP_ACTION_PUT_COMMAND)
    public void sendHttpPut() {
        String response = sendHttpPutRequest("http://http-mock:8080" + HTTP_PUT_SOMETHING, null);
        logInfo("send Http: {}", response);
    }

    @JRuleName(NAME_SEND_HTTP_DELETE)
    @JRuleWhenItemReceivedCommand(item = ITEM_TRIGGER_HTTP_ACTION, command = HTTP_ACTION_DELETE_COMMAND)
    public void sendHttpDelete() {
        String response = sendHttpDeleteRequest("http://http-mock:8080" + HTTP_DELETE_SOMETHING, null);
        logInfo("send Http: {}", response);
    }
}
