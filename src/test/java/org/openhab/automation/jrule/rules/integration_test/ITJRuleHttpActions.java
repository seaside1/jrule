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
package org.openhab.automation.jrule.rules.integration_test;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.rules.user.TestRulesHttpActions;

import com.github.tomakehurst.wiremock.client.WireMock;

/**
 * The {@link ITJRuleHttpActions}
 *
 * @author Robert Delbrück - Initial contribution
 */
// Please do not reuse items for testing, create new ones, otherwise every change will hurt sometimes
public class ITJRuleHttpActions extends JRuleITBase {
    @Test
    public void sendHttpGet() throws IOException {
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_GET_SOMETHING))
                .willReturn(WireMock.aResponse().withHeader("Content-Type", "text/plain").withBody("Get Something")));

        sendCommand(TestRulesHttpActions.ITEM_TRIGGER_HTTP_ACTION, TestRulesHttpActions.HTTP_ACTION_GET_COMMAND);
        verifyRuleWasExecuted(TestRulesHttpActions.NAME_SEND_HTTP_GET);

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_GET_SOMETHING)));
    }

    @Test
    public void sendHttpPost() throws IOException {
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_POST_SOMETHING))
                .willReturn(WireMock.aResponse().withHeader("Content-Type", "text/plain").withBody("Post Something")));

        sendCommand(TestRulesHttpActions.ITEM_TRIGGER_HTTP_ACTION, TestRulesHttpActions.HTTP_ACTION_POST_COMMAND);
        verifyRuleWasExecuted(TestRulesHttpActions.NAME_SEND_HTTP_POST);

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_POST_SOMETHING)));
    }

    @Test
    public void sendHttpPut() throws IOException {
        WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_PUT_SOMETHING))
                .willReturn(WireMock.aResponse().withHeader("Content-Type", "text/plain").withBody("Put Something")));

        sendCommand(TestRulesHttpActions.ITEM_TRIGGER_HTTP_ACTION, TestRulesHttpActions.HTTP_ACTION_PUT_COMMAND);
        verifyRuleWasExecuted(TestRulesHttpActions.NAME_SEND_HTTP_PUT);

        WireMock.verify(WireMock.putRequestedFor(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_PUT_SOMETHING)));
    }

    @Test
    public void sendHttpDelete() throws IOException {
        WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_DELETE_SOMETHING)).willReturn(
                WireMock.aResponse().withHeader("Content-Type", "text/plain").withBody("Delete Something")));

        sendCommand(TestRulesHttpActions.ITEM_TRIGGER_HTTP_ACTION, TestRulesHttpActions.HTTP_ACTION_DELETE_COMMAND);
        verifyRuleWasExecuted(TestRulesHttpActions.NAME_SEND_HTTP_DELETE);

        WireMock.verify(WireMock.deleteRequestedFor(WireMock.urlEqualTo(TestRulesHttpActions.HTTP_DELETE_SOMETHING)));
    }
}
