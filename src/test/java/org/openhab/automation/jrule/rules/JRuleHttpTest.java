package org.openhab.automation.jrule.rules;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openhab.core.io.net.http.HttpUtil;

class JRuleHttpTest {
    private final JRule jRule = new JRule();

    @Test
    void testSendHttpGetRequest() {
        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.GET), Mockito.anyString(), Mockito.isNull(),
                    Mockito.isNull(), Mockito.isNull(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo",
                    jRule.sendHttpGetRequest("http://notexistingurl/service", Duration.ofSeconds(5)));
        }

        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.GET), Mockito.anyString(), Mockito.any(),
                    Mockito.isNull(), Mockito.isNull(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo",
                    jRule.sendHttpGetRequest("http://notexistingurl/service", new HashMap<>(), Duration.ofSeconds(5)));
        }
    }

    @Test
    void testSendHttpPutRequest() {
        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.PUT), Mockito.anyString(), Mockito.isNull(),
                    Mockito.isNull(), Mockito.isNull(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo",
                    jRule.sendHttpPutRequest("http://notexistingurl/service", Duration.ofSeconds(5)));
        }

        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.PUT), Mockito.anyString(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo", jRule.sendHttpPutRequest("http://notexistingurl/service",
                    MediaType.APPLICATION_JSON, "string-content", new HashMap<>(), Duration.ofSeconds(5)));
        }

        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.PUT), Mockito.anyString(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo",
                    jRule.sendHttpPutRequest("http://notexistingurl/service", MediaType.APPLICATION_OCTET_STREAM,
                            "binary-content".getBytes(StandardCharsets.UTF_8), new HashMap<>(), Duration.ofSeconds(5)));
        }
    }

    @Test
    void testSendHttpPostRequest() {
        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.POST), Mockito.anyString(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo",
                    jRule.sendHttpPostRequest("http://notexistingurl/service", Duration.ofSeconds(5)));
        }

        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.POST), Mockito.anyString(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo", jRule.sendHttpPostRequest("http://notexistingurl/service",
                    MediaType.APPLICATION_JSON, "string-content", new HashMap<>(), Duration.ofSeconds(5)));
        }

        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.POST), Mockito.anyString(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo",
                    jRule.sendHttpPostRequest("http://notexistingurl/service", MediaType.APPLICATION_OCTET_STREAM,
                            "binary-content".getBytes(StandardCharsets.UTF_8), new HashMap<>(), Duration.ofSeconds(5)));
        }
    }

    @Test
    void testSendHttpDeleteRequest() {
        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.DELETE), Mockito.anyString(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo",
                    jRule.sendHttpDeleteRequest("http://notexistingurl/service", Duration.ofSeconds(5)));
        }

        try (MockedStatic<HttpUtil> mocked = Mockito.mockStatic(HttpUtil.class)) {
            mocked.when(() -> HttpUtil.executeUrl(Mockito.eq(HttpMethod.DELETE), Mockito.anyString(), Mockito.any(),
                    Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("foo");
            Assertions.assertEquals("foo", jRule.sendHttpDeleteRequest("http://notexistingurl/service", new HashMap<>(),
                    Duration.ofSeconds(5)));
        }
    }
}
