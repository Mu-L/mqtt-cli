/*
 * Copyright 2019-present HiveMQ and the HiveMQ Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.cli.commands.cli.publish;

import com.google.common.collect.ImmutableList;
import com.hivemq.cli.utils.MqttVersionConverter;
import com.hivemq.cli.utils.broker.HiveMQ;
import com.hivemq.cli.utils.cli.MqttCli;
import com.hivemq.cli.utils.cli.results.ExecutionResult;
import com.hivemq.extension.sdk.api.packets.connack.ConnackPacket;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PayloadFormatIndicator;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.builder.WillPublishBuilder;
import com.hivemq.extensions.packets.general.UserPropertiesImpl;
import com.hivemq.mqtt.message.mqtt5.MqttUserProperty;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hivemq.cli.utils.broker.assertions.ConnectAssertion.assertConnectPacket;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublishConnectST {

    @RegisterExtension
    private static final @NotNull HiveMQ HIVEMQ = HiveMQ.builder().build();

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectWrongHost(final char mqttVersion) throws Exception {
        final List<String> publishCommand = List.of("pub",
                "-h",
                "wrong-host",
                "-p",
                String.valueOf(HIVEMQ.getMqttPort()),
                "-V",
                String.valueOf(mqttVersion),
                "-t",
                "test",
                "-m",
                "test",
                "-d");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertEquals(0, executionResult.getExitCode());
        assertTrue(
                executionResult.getErrorOutput().contains("unreachable-host: Temporary failure in name resolution") ||
                        executionResult.getErrorOutput().contains("nodename nor servname provided, or not known"));
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectWrongPort(final char mqttVersion) throws Exception {
        final List<String> publishCommand = List.of("pub",
                "-h",
                HIVEMQ.getHost(),
                "-p",
                "22",
                "-V",
                String.valueOf(mqttVersion),
                "-t",
                "test",
                "-m",
                "test",
                "-d");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertEquals(0, executionResult.getExitCode());
        assertTrue(executionResult.getErrorOutput().contains("readAddress(..) failed: Connection reset by peer") ||
                executionResult.getErrorOutput().contains("Connection refused"));
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectCleanStart(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("--no-cleanStart");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setCleanStart(false);
            if (mqttVersion == '3') {
                connectAssertion.setSessionExpiryInterval(4294967295L);
            }
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectKeepAlive(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-k");
        publishCommand.add("100");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setKeepAlive(100);
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectNoClientId(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.remove("-i");
        publishCommand.remove("cliTest");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertTrue(executionResult.getStandardOutput().contains("sending CONNECT"));
        assertTrue(executionResult.getStandardOutput().contains("received CONNACK"));

        final String expectedClientId;
        if (mqttVersion == '5') {
            final ConnackPacket connackPacket = HIVEMQ.getConnackPackets().get(0);
            assertTrue(connackPacket.getAssignedClientIdentifier().isPresent());
            expectedClientId = connackPacket.getAssignedClientIdentifier().get();
        } else {
            final ConnectPacket connectPacket = HIVEMQ.getConnectPackets().get(0);
            expectedClientId = connectPacket.getClientId();
        }

        assertTrue(executionResult.getStandardOutput()
                .contains(String.format("Client '%s@%s' sending PUBLISH", expectedClientId, HIVEMQ.getHost())));
        assertTrue(executionResult.getStandardOutput()
                .contains(String.format("Client '%s@%s' received PUBLISH acknowledgement",
                        expectedClientId,
                        HIVEMQ.getHost())));
        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setClientId(expectedClientId);
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectIdentifierPrefix(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.remove("-i");
        publishCommand.remove("cliTest");
        publishCommand.add("-ip");
        publishCommand.add("test-");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        final ConnectPacket connectPacket = HIVEMQ.getConnectPackets().get(0);
        if (mqttVersion == '3') {
            assertTrue(connectPacket.getClientId().startsWith("test-"));
        }

        assertConnectPacket(connectPacket, connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setClientId(connectPacket.getClientId());
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectUserName(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-u");
        publishCommand.add("username");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setUserName("username");
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectPassword(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-pw");
        publishCommand.add("password");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Password-Only Authentication is not allowed in MQTT 3"));
            assertEquals(0, executionResult.getExitCode());
        } else {
            assertPublishOutput(executionResult);
            assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
                connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
                connectAssertion.setPassword(ByteBuffer.wrap("password".getBytes(StandardCharsets.UTF_8)));
            });
        }
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectPasswordEnv(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-pw:env");
        publishCommand.add("PASSWORD");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand, Map.of("PASSWORD", "password"));

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Password-Only Authentication is not allowed in MQTT 3"));
            assertEquals(0, executionResult.getExitCode());
        } else {
            assertPublishOutput(executionResult);
            assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
                connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
                connectAssertion.setPassword(ByteBuffer.wrap("password".getBytes(StandardCharsets.UTF_8)));
            });
        }
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectPasswordFile(final char mqttVersion) throws Exception {
        final Path passwordFile = Files.createTempFile("password-file", ".txt");
        passwordFile.toFile().deleteOnExit();
        Files.writeString(passwordFile, "password");

        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-pw:file");
        publishCommand.add(passwordFile.toString());

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Password-Only Authentication is not allowed in MQTT 3"));
            assertEquals(0, executionResult.getExitCode());
        } else {
            assertPublishOutput(executionResult);
            assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
                connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
                connectAssertion.setPassword(ByteBuffer.wrap("password".getBytes(StandardCharsets.UTF_8)));
            });
        }
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectUserNameAndPassword(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-u");
        publishCommand.add("username");
        publishCommand.add("-pw");
        publishCommand.add("password");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        assertPublishOutput(executionResult);
        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setUserName("username");
            connectAssertion.setPassword(ByteBuffer.wrap("password".getBytes(StandardCharsets.UTF_8)));
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectUserNameAndPasswordEnv(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-u");
        publishCommand.add("username");
        publishCommand.add("-pw:env");
        publishCommand.add("PASSWORD");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand, Map.of("PASSWORD", "password"));

        assertPublishOutput(executionResult);
        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setUserName("username");
            connectAssertion.setPassword(ByteBuffer.wrap("password".getBytes(StandardCharsets.UTF_8)));
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectUserNamePasswordFile(final char mqttVersion) throws Exception {
        final Path passwordFile = Files.createTempFile("password-file", ".txt");
        passwordFile.toFile().deleteOnExit();
        Files.writeString(passwordFile, "password");

        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-u");
        publishCommand.add("username");
        publishCommand.add("-pw:file");
        publishCommand.add(passwordFile.toString());

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);

        assertPublishOutput(executionResult);
        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            connectAssertion.setUserName("username");
            connectAssertion.setPassword(ByteBuffer.wrap("password".getBytes(StandardCharsets.UTF_8)));
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectWill(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-Wt");
        publishCommand.add("test-will-topic");
        publishCommand.add("-Wm");
        publishCommand.add("will-message");
        publishCommand.add("-Wq");
        publishCommand.add("2");
        publishCommand.add("-Wr");
        publishCommand.add("-We");
        publishCommand.add("120");
        publishCommand.add("-Wd");
        publishCommand.add("180");
        publishCommand.add("-Wpf");
        publishCommand.add(PayloadFormatIndicator.UTF_8.name());
        publishCommand.add("-Wct");
        publishCommand.add("content-type");
        publishCommand.add("-Wrt");
        publishCommand.add("will-response-topic");
        publishCommand.add("-Wup");
        publishCommand.add("key1=value1");
        publishCommand.add("-Wup");
        publishCommand.add("key2=value2");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Will Message Expiry was set but is unused in MQTT Version MQTT_3_1_1"));
            assertTrue(executionResult.getErrorOutput()
                    .contains("Will Payload Format was set but is unused in MQTT Version MQTT_3_1_1"));
            assertTrue(executionResult.getErrorOutput()
                    .contains("Will Delay Interval was set but is unused in MQTT Version MQTT_3_1_1"));
            assertTrue(executionResult.getErrorOutput()
                    .contains("Will Content Type was set but is unused in MQTT Version MQTT_3_1_1"));
            assertTrue(executionResult.getErrorOutput()
                    .contains("Will Response Topic was set but is unused in MQTT Version MQTT_3_1_1"));
            assertTrue(executionResult.getErrorOutput()
                    .contains("Will User Properties was set but is unused in MQTT Version MQTT_3_1_1"));

            assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
                final WillPublishBuilder expectedWillBuilder = Builders.willPublish()
                        .payload(ByteBuffer.wrap("will-message".getBytes(StandardCharsets.UTF_8)))
                        .topic("test-will-topic")
                        .qos(Qos.EXACTLY_ONCE)
                        .messageExpiryInterval(4294967295L)
                        .retain(true);

                connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
                connectAssertion.setWillPublish(expectedWillBuilder.build());
            });

        } else {
            assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
                final WillPublishBuilder expectedWillBuilder = Builders.willPublish()
                        .payload(ByteBuffer.wrap("will-message".getBytes(StandardCharsets.UTF_8)))
                        .topic("test-will-topic")
                        .qos(Qos.EXACTLY_ONCE)
                        .retain(true)
                        .payloadFormatIndicator(PayloadFormatIndicator.UNSPECIFIED)
                        .messageExpiryInterval(120)
                        .willDelay(180)
                        .payloadFormatIndicator(PayloadFormatIndicator.UTF_8)
                        .contentType("content-type")
                        .responseTopic("will-response-topic")
                        .userProperty("key1", "value1")
                        .userProperty("key2", "value2");

                connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
                connectAssertion.setWillPublish(expectedWillBuilder.build());
            });
        }
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectReceiveMax(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("--rcvMax");
        publishCommand.add("100");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Restriction receive maximum was set but is unused in MQTT Version MQTT_3_1_1"));
        }

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            if (mqttVersion == '5') {
                connectAssertion.setReceiveMaximum(100);
            }
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectMaxPacketSize(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("--maxPacketSize");
        publishCommand.add("100");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Restriction maximum packet size was set but is unused in MQTT Version MQTT_3_1_1"));
        }

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            if (mqttVersion == '5') {
                connectAssertion.setMaximumPacketSize(100);
            }
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectTopicAliasMaximum(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("--topicAliasMax");
        publishCommand.add("100");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Restriction topic alias maximum was set but is unused in MQTT Version MQTT_3_1_1"));
        }

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            if (mqttVersion == '5') {
                connectAssertion.setTopicAliasMaximum(100);
            }
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectRequestProblemInformation(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("--no-reqProblemInfo");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Restriction request problem information was set but is unused in MQTT Version MQTT_3_1_1"));
        }

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            if (mqttVersion == '5') {
                connectAssertion.setRequestProblemInformation(false);
            }
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectRequestResponseInformation(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("--reqResponseInfo");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains(
                            "Restriction request response information was set but is unused in MQTT Version MQTT_3_1_1"));
        }

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            if (mqttVersion == '5') {
                connectAssertion.setRequestResponseInformation(true);
            }
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectSessionExpiryInterval(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-se");
        publishCommand.add("100");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Connect session expiry interval was set but is unused in MQTT Version MQTT_3_1_1"));
        }

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            if (mqttVersion == '5') {
                connectAssertion.setSessionExpiryInterval(100);
            }
        });
    }

    @ParameterizedTest
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    @ValueSource(chars = {'3', '5'})
    void test_connectUserProperties(final char mqttVersion) throws Exception {
        final List<String> publishCommand = defaultPublishCommand(mqttVersion);
        publishCommand.add("-Cup");
        publishCommand.add("key1=value1");
        publishCommand.add("-Cup");
        publishCommand.add("key2=value2");

        final ExecutionResult executionResult = MqttCli.execute(publishCommand);
        assertPublishOutput(executionResult);

        if (mqttVersion == '3') {
            assertTrue(executionResult.getErrorOutput()
                    .contains("Connect user properties were set but are unused in MQTT Version MQTT_3_1_1"));
        }

        assertConnectPacket(HIVEMQ.getConnectPackets().get(0), connectAssertion -> {
            connectAssertion.setMqttVersion(MqttVersionConverter.toExtensionSdkVersion(mqttVersion));
            if (mqttVersion == '5') {
                final UserPropertiesImpl expectedUserProperties = UserPropertiesImpl.of(ImmutableList.of(
                        MqttUserProperty.of("key1", "value1"),
                        MqttUserProperty.of("key2", "value2")));
                connectAssertion.setUserProperties(expectedUserProperties);
            }
        });
    }

    private void assertPublishOutput(final @NotNull ExecutionResult executionResult) {
        assertEquals(0, executionResult.getExitCode());
        assertTrue(executionResult.getStandardOutput().contains("sending CONNECT"));
        assertTrue(executionResult.getStandardOutput().contains("received CONNACK"));
        assertTrue(executionResult.getStandardOutput().contains("sending PUBLISH"));
        assertTrue(executionResult.getStandardOutput().contains("received PUBLISH acknowledgement"));
    }

    private @NotNull List<String> defaultPublishCommand(final char mqttVersion) {
        final ArrayList<String> publishCommand = new ArrayList<>();
        publishCommand.add("pub");
        publishCommand.add("-h");
        publishCommand.add(HIVEMQ.getHost());
        publishCommand.add("-p");
        publishCommand.add(String.valueOf(HIVEMQ.getMqttPort()));
        publishCommand.add("-V");
        publishCommand.add(String.valueOf(mqttVersion));
        publishCommand.add("-i");
        publishCommand.add("cliTest");
        publishCommand.add("-t");
        publishCommand.add("test");
        publishCommand.add("-m");
        publishCommand.add("test");
        publishCommand.add("-d");
        return publishCommand;
    }
}
