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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.awaitility.Awaitility;
import org.eclipse.paho.client.mqttv3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.TestRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import org.testcontainers.utility.MountableFile;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPut;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ParseException;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The {@link JRuleITBase}
 *
 * @author Robert Delbrück - Initial contribution
 */
public abstract class JRuleITBase {
    private static final Network network = Network.newNetwork();
    protected static final List<String> logLines = new CopyOnWriteArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(ITJRule.class);
    private static final String version;

    static {
        try {
            version = IOUtils.resourceToString("/version", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<String> receivedMqttMessages = new ArrayList<>();

    @SuppressWarnings("resource")
    private static final GenericContainer<?> mqttContainer = new GenericContainer<>("eclipse-mosquitto:2.0")
            .withExposedPorts(1883, 9001).withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("docker.mqtt")))
            .withCopyFileToContainer(MountableFile.forClasspathResource("/docker/mosquitto/mosquitto.conf"),
                    "/mosquitto/config/mosquitto.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("/docker/mosquitto/default.acl"),
                    "/mosquitto/config/default.conf")
            .waitingFor(new LogMessageWaitStrategy().withRegEx(".*mosquitto version.*")).withNetwork(network)
            .withReuse(true);

    private static final ToxiproxyContainer toxiproxyContainer = new ToxiproxyContainer(
            "ghcr.io/shopify/toxiproxy:2.5.0").withNetworkAliases("mqtt").withNetwork(network).dependsOn(mqttContainer)
            .withReuse(true);

    protected static final GenericContainer<?> influxDbContainer = new GenericContainer<>("influxdb:2.0")
            .withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup").withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "admin")
            .withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "influxdb").withEnv("DOCKER_INFLUXDB_INIT_ORG", "openhab")
            .withEnv("DOCKER_INFLUXDB_INIT_BUCKET", "autogen").withEnv("DOCKER_INFLUXDB_INIT_RETENTION", "1w")
            .withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", "mytoken").withNetworkAliases("influxdb")
            .withExposedPorts(8086)
            .waitingFor(new LogMessageWaitStrategy()
                    .withRegEx(".*service=tcp-listener transport=http addr=:8086 port=8086.*"))
            .withNetwork(network).withReuse(true);

    public static final int TIMEOUT = 180;
    public static final String LOG_REGEX_START = "^\\d+:\\d+:\\d+.\\d+.*";
    @SuppressWarnings("resource")
    private static final GenericContainer<?> openhabContainer = new GenericContainer<>("openhab/openhab:4.2.0-debian")
            .withCopyToContainer(MountableFile.forClasspathResource("docker/conf", 0777), "/openhab/conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("docker/log4j2.xml", 0777),
                    "/openhab/userdata/etc/log4j2.xml")
            .withCopyFileToContainer(MountableFile.forClasspathResource("docker/users.json", 0777),
                    "/openhab/userdata/jsondb/users.json")
            .withCopyFileToContainer(MountableFile
                    .forHostPath(String.format("target/org.openhab.automation.jrule-%s.jar", version), 0777),
                    "/openhab/addons/jrule-engine.jar")
            .withCopyToContainer(
                    MountableFile.forHostPath("src/test/java/org/openhab/automation/jrule/rules/user", 0777),
                    "/openhab/conf/automation/jrule/rules/org/openhab/automation/jrule/rules/user")
            .withExposedPorts(8080).withLogConsumer(outputFrame -> {
                String line = outputFrame.getUtf8String().strip();
                logLines.add(line);
                if (!line.matches(LOG_REGEX_START)) {
                    // this line was splitted
                    int index = logLines.size() - 2;
                    if (logLines.size() > 2) {
                        String prevLine = logLines.get(index);
                        if (prevLine.matches(LOG_REGEX_START)) {
                            logLines.remove(index);
                            String newLine = prevLine + " " + line;
                            log.warn("merged two lines: {}", newLine);
                            logLines.add(newLine);
                        }
                    }
                }
                new Slf4jLogConsumer(LoggerFactory.getLogger("docker.openhab")).accept(outputFrame);
            }).waitingFor(new WaitAllStrategy(WaitAllStrategy.Mode.WITH_MAXIMUM_OUTER_TIMEOUT)
                    .withStrategy(new AbstractWaitStrategy() {
                        @Override
                        protected void waitUntilReady() {
                            Awaitility.await().with().pollDelay(10, TimeUnit.SECONDS).timeout(TIMEOUT, TimeUnit.SECONDS)
                                    .pollInterval(2, TimeUnit.SECONDS).await("thing online").until(() -> {
                                        try {
                                            return getThingState("mqtt:topic:mqtt:generic");
                                        } catch (Exception e) {
                                            return e.getMessage();
                                        }
                                    }, s -> s.equals("ONLINE"));
                        }
                    }).withStrategy(new AbstractWaitStrategy() {
                        @Override
                        protected void waitUntilReady() {
                            Awaitility.await().with().pollDelay(10, TimeUnit.SECONDS).timeout(TIMEOUT, TimeUnit.SECONDS)
                                    .pollInterval(2, TimeUnit.SECONDS).await("items loaded").until(() -> {
                                        try {
                                            return getItemCount();
                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    }, s -> s > 0);
                        }
                    }).withStartupTimeout(Duration.of(TIMEOUT, ChronoUnit.SECONDS)))
            .withNetwork(network).dependsOn(influxDbContainer).withReuse(true);

    protected static final GenericContainer<?> mockServer = new GenericContainer<>("wiremock/wiremock:3.8.0")
            .withExposedPorts(8080).withNetwork(network).withNetworkAliases("http-mock");

    protected static ToxiproxyContainer.ContainerProxy mqttProxy;
    private @NotNull IMqttClient mqttClient;

    @BeforeAll
    static void initClass() {
        toxiproxyContainer.start();
        mqttProxy = toxiproxyContainer.getProxy(mqttContainer, 1883);
        System.out.println(mqttProxy.getOriginalProxyPort());
        openhabContainer.start();
        mockServer.start();
    }

    @BeforeEach
    void initTest(TestInfo testInfo) throws IOException, InterruptedException, MqttException {
        mqttProxy.setConnectionCut(false);
        Awaitility.await().with().pollDelay(1, TimeUnit.SECONDS).timeout(20, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS).await("thing online")
                .until(() -> getThingState("mqtt:topic:mqtt:generic"), s -> s.equals("ONLINE"));

        logLines.clear();
        openhabContainer.execInContainer("rm", "/openhab/userdata/example.txt");
        sendCommand(TestRules.ITEM_RECEIVING_COMMAND_SWITCH, JRuleSwitchItem.OFF);
        sendCommand(TestRules.ITEM_PRECONDITION_STRING, JRuleSwitchItem.OFF);
        sendCommand(TestRules.ITEM_GET_MEMBERS_OF_GROUP_SWITCH, JRuleSwitchItem.OFF);
        sendCommand(TestRules.ITEM_MQTT_ACTION_TRIGGER, JRuleSwitchItem.OFF);
        sendCommand(TestRules.ITEM_PRECONDITIONED_SWITCH, JRuleSwitchItem.OFF);
        sendCommand(TestRules.ITEM_PRECONDITION_STRING, "");
        sendCommand(TestRules.ITEM_SWITCH_GROUP_MEMBER1, JRuleSwitchItem.OFF);
        sendCommand(TestRules.ITEM_SWITCH_GROUP_MEMBER2, JRuleSwitchItem.OFF);

        receivedMqttMessages.clear();
        mqttClient = getMqttClient();
        subscribeMqtt("number/state");
        publishMqttMessage("number/state", "0");

        WireMock.configureFor(mockServer.getHost(), mockServer.getFirstMappedPort());
        WireMock.reset();

        log.info("=== starting test '{}'", testInfo.getTestMethod());
    }

    @AfterEach
    void unloadTest(TestInfo testInfo) throws MqttException {
        log.info("=== stopped test '{}'", testInfo.getTestMethod());

        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
    }

    @AfterAll
    static void testFinished() {
        if (openhabContainer != null && openhabContainer.isRunning()) {
            try {
                openhabContainer.copyFileFromContainer("/openhab/conf/automation/jrule/jar/jrule.jar",
                        "target/jrule.jar");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            try {
                openhabContainer.copyFileFromContainer("/openhab/conf/automation/jrule/jar/jrule-generated.jar",
                        "target/jrule-generated.jar");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        try (Stream<Path> stream = Files.list(Path.of("/"))
                .filter(path -> path.getFileName().startsWith("ITJRule-tcplocalhost"))) {
            stream.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            log.warn("cannot remove temp files", e);
        }
    }

    private void subscribeMqtt(String topic) throws MqttException {
        mqttClient.subscribe(topic, (s, mqttMessage) -> receivedMqttMessages
                .add(new String(mqttMessage.getPayload(), StandardCharsets.UTF_8)));
    }

    @NotNull
    private static IMqttClient getMqttClient() throws MqttException {
        IMqttClient publisher = new MqttClient(String.format("tcp://%s:%s", getMqttHost(), getMqttPort()), "ITJRule");
        MqttConnectOptions options = getMqttConnectOptions();
        publisher.connect(options);
        return publisher;
    }

    @NotNull
    private static MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(2);
        return options;
    }

    private boolean containsLine(String line, List<String> logLines) {
        return logLines.stream().anyMatch(s -> s.contains(line));
    }

    private boolean matchesLine(String line, List<String> logLines) {
        return logLines.stream().anyMatch(s -> s.matches(line));
    }

    private boolean notContainsLine(String line, List<String> logLines) {
        return logLines.stream().noneMatch(s -> s.contains(line));
    }

    protected void sendCommand(String itemName, String value) throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(
                    String.format("http://%s:%s/rest/items/%s", getOpenhabHost(), getOpenhabPort(), itemName));
            request.setEntity(new StringEntity(value));
            CloseableHttpResponse response = client.execute(request);
            Assertions.assertEquals(2, response.getCode() / 100);
        }
    }

    protected void postUpdate(String itemName, String value) throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPut request = new HttpPut(
                    String.format("http://%s:%s/rest/items/%s/state", getOpenhabHost(), getOpenhabPort(), itemName));
            request.setEntity(new StringEntity(value));
            CloseableHttpResponse response = client.execute(request);
            Assertions.assertEquals(2, response.getCode() / 100);
        }
    }

    protected static void clearLog() {
        logLines.clear();
    }

    private Optional<Double> getDoubleState(String itemName) throws IOException, ParseException {
        return Optional.ofNullable(getState(itemName)).map(Double::parseDouble);
    }

    private String getState(String itemName) throws IOException, ParseException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(String.format("http://%s:%s/rest/items/" + itemName + "/state",
                    getOpenhabHost(), getOpenhabPort()));
            CloseableHttpResponse response = client.execute(request);
            Assertions.assertEquals(2, response.getCode() / 100);
            return Optional.of(EntityUtils.toString(response.getEntity())).filter(s -> !s.equals("NULL")).orElse(null);
        }
    }

    protected static String getThingState(String thing) throws IOException, ParseException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(String.format("http://%s:%s/rest/things/%s/status", getOpenhabHost(),
                    getOpenhabPort(), URLEncoder.encode(thing, StandardCharsets.UTF_8)));
            byte[] credentials = Base64.getEncoder().encode(("admin:admin").getBytes(StandardCharsets.UTF_8));
            request.setHeader("Authorization", "Basic " + new String(credentials, StandardCharsets.UTF_8));
            CloseableHttpResponse response = client.execute(request);
            if (isHttp2xx(response)) {
                return response.getReasonPhrase();
            }
            JsonElement jsonElement = JsonParser.parseString(EntityUtils.toString(response.getEntity()));
            String status = jsonElement.getAsJsonObject().getAsJsonPrimitive("status").getAsString();
            log.debug("querying status for '{}' -> '{}'", thing, status);
            return status;
        }
    }

    protected static int getItemCount() throws IOException, ParseException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(
                    String.format("http://%s:%s/rest/items?recursive=false", getOpenhabHost(), getOpenhabPort()));
            byte[] credentials = Base64.getEncoder().encode(("admin:admin").getBytes(StandardCharsets.UTF_8));
            request.setHeader("Authorization", "Basic " + new String(credentials, StandardCharsets.UTF_8));
            CloseableHttpResponse response = client.execute(request);
            if (isHttp2xx(response)) {
                // error case
                return 0;
            }
            JsonElement jsonElement = JsonParser.parseString(EntityUtils.toString(response.getEntity()));
            return jsonElement.getAsJsonArray().size();
        }
    }

    private static boolean isHttp2xx(CloseableHttpResponse response) {
        return 2 != response.getCode() / 100;
    }

    private static int getOpenhabPort() {
        return openhabContainer.getFirstMappedPort();
    }

    private static String getOpenhabHost() {
        return openhabContainer.getHost();
    }

    private static int getMqttPort() {
        return mqttContainer.getFirstMappedPort();
    }

    private static String getMqttHost() {
        return mqttContainer.getHost();
    }

    protected void publishMqttMessage(String topic, String message) throws MqttException {
        MqttMessage msg = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
        msg.setQos(0);
        msg.setRetained(true);
        mqttClient.publish(topic, msg);
    }

    protected void verifyFileExist() throws IOException, InterruptedException {
        Container.ExecResult execResult = openhabContainer.execInContainer("ls", "/openhab/userdata/example.txt");
        Assertions.assertEquals(0, execResult.getExitCode());
    }

    protected void verifyRuleWasExecuted(String ruleLogLine) {
        Awaitility.await().with().timeout(20, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS)
                .await("rule executed").until(() -> logLines, v -> containsLine(toMethodCallLogEntry(ruleLogLine), v));
    }

    protected void verifyLogEntry(String ruleLogLine) {
        Awaitility.await().with().timeout(20, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS)
                .await("rule executed").until(() -> logLines, v -> containsLine(ruleLogLine, v));
    }

    protected Matcher getLogEntry(String ruleLogLinePattern) {
        Awaitility.await().with().timeout(20, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS)
                .await("rule executed").until(() -> logLines, v -> matchesLine(ruleLogLinePattern, v));
        Pattern p = Pattern.compile(ruleLogLinePattern);
        return logLines.stream().filter(s -> s.matches(ruleLogLinePattern)).findFirst().map(input -> {
            Matcher matcher = p.matcher(input);
            matcher.matches();
            return matcher;
        }).orElseThrow();
    }

    protected void verifyNoLogEntry(String ruleLogLine) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // no problem here
        }
        Assertions.assertTrue(notContainsLine(ruleLogLine, logLines));
    }

    protected void verifyCommandEventFor(String itemName) {
        verifyLogEntry(String.format("Item '%s' received command", itemName));
    }

    protected void verifyStateChangeEventFor(String itemName) {
        verifyLogEntry(String.format("Item '%s' changed", itemName));
    }

    protected void verifyNoError() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // no problem here
        }
        Assertions.assertTrue(notContainsLine("ERROR", logLines));
    }

    private static String toMethodCallLogEntry(String ruleLogLine) {
        return String.format("[+%s+]", ruleLogLine);
    }

    protected void verifyRuleWasNotExecuted(String ruleLogLine) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // no problem here
        }
        Assertions.assertTrue(notContainsLine(toMethodCallLogEntry(ruleLogLine), logLines));
    }

    protected void verifyMqttMessageReceived(String s) {
        Assertions.assertTrue(receivedMqttMessages.contains(s));
    }
}
