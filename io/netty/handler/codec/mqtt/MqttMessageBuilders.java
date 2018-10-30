/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class MqttMessageBuilders {
    public static ConnectBuilder connect() {
        return new ConnectBuilder();
    }

    public static ConnAckBuilder connAck() {
        return new ConnAckBuilder();
    }

    public static PublishBuilder publish() {
        return new PublishBuilder();
    }

    public static SubscribeBuilder subscribe() {
        return new SubscribeBuilder();
    }

    public static UnsubscribeBuilder unsubscribe() {
        return new UnsubscribeBuilder();
    }

    private MqttMessageBuilders() {
    }

    public static final class ConnAckBuilder {
        private MqttConnectReturnCode returnCode;
        private boolean sessionPresent;

        ConnAckBuilder() {
        }

        public ConnAckBuilder returnCode(MqttConnectReturnCode returnCode) {
            this.returnCode = returnCode;
            return this;
        }

        public ConnAckBuilder sessionPresent(boolean sessionPresent) {
            this.sessionPresent = sessionPresent;
            return this;
        }

        public MqttConnAckMessage build() {
            MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
            MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(this.returnCode, this.sessionPresent);
            return new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
        }
    }

    public static final class UnsubscribeBuilder {
        private List<String> topicFilters;
        private int messageId;

        UnsubscribeBuilder() {
        }

        public UnsubscribeBuilder addTopicFilter(String topic) {
            if (this.topicFilters == null) {
                this.topicFilters = new ArrayList<String>(5);
            }
            this.topicFilters.add(topic);
            return this;
        }

        public UnsubscribeBuilder messageId(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public MqttUnsubscribeMessage build() {
            MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
            MqttMessageIdVariableHeader mqttVariableHeader = MqttMessageIdVariableHeader.from(this.messageId);
            MqttUnsubscribePayload mqttSubscribePayload = new MqttUnsubscribePayload(this.topicFilters);
            return new MqttUnsubscribeMessage(mqttFixedHeader, mqttVariableHeader, mqttSubscribePayload);
        }
    }

    public static final class SubscribeBuilder {
        private List<MqttTopicSubscription> subscriptions;
        private int messageId;

        SubscribeBuilder() {
        }

        public SubscribeBuilder addSubscription(MqttQoS qos, String topic) {
            if (this.subscriptions == null) {
                this.subscriptions = new ArrayList<MqttTopicSubscription>(5);
            }
            this.subscriptions.add(new MqttTopicSubscription(topic, qos));
            return this;
        }

        public SubscribeBuilder messageId(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public MqttSubscribeMessage build() {
            MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
            MqttMessageIdVariableHeader mqttVariableHeader = MqttMessageIdVariableHeader.from(this.messageId);
            MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(this.subscriptions);
            return new MqttSubscribeMessage(mqttFixedHeader, mqttVariableHeader, mqttSubscribePayload);
        }
    }

    public static final class ConnectBuilder {
        private MqttVersion version = MqttVersion.MQTT_3_1_1;
        private String clientId;
        private boolean cleanSession;
        private boolean hasUser;
        private boolean hasPassword;
        private int keepAliveSecs;
        private boolean willFlag;
        private boolean willRetain;
        private MqttQoS willQos = MqttQoS.AT_MOST_ONCE;
        private String willTopic;
        private byte[] willMessage;
        private String username;
        private byte[] password;

        ConnectBuilder() {
        }

        public ConnectBuilder protocolVersion(MqttVersion version) {
            this.version = version;
            return this;
        }

        public ConnectBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public ConnectBuilder cleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public ConnectBuilder keepAlive(int keepAliveSecs) {
            this.keepAliveSecs = keepAliveSecs;
            return this;
        }

        public ConnectBuilder willFlag(boolean willFlag) {
            this.willFlag = willFlag;
            return this;
        }

        public ConnectBuilder willQoS(MqttQoS willQos) {
            this.willQos = willQos;
            return this;
        }

        public ConnectBuilder willTopic(String willTopic) {
            this.willTopic = willTopic;
            return this;
        }

        @Deprecated
        public ConnectBuilder willMessage(String willMessage) {
            this.willMessage(willMessage == null ? null : willMessage.getBytes(CharsetUtil.UTF_8));
            return this;
        }

        public ConnectBuilder willMessage(byte[] willMessage) {
            this.willMessage = willMessage;
            return this;
        }

        public ConnectBuilder willRetain(boolean willRetain) {
            this.willRetain = willRetain;
            return this;
        }

        public ConnectBuilder hasUser(boolean value) {
            this.hasUser = value;
            return this;
        }

        public ConnectBuilder hasPassword(boolean value) {
            this.hasPassword = value;
            return this;
        }

        public ConnectBuilder username(String username) {
            this.hasUser = username != null;
            this.username = username;
            return this;
        }

        @Deprecated
        public ConnectBuilder password(String password) {
            this.password(password == null ? null : password.getBytes(CharsetUtil.UTF_8));
            return this;
        }

        public ConnectBuilder password(byte[] password) {
            this.hasPassword = password != null;
            this.password = password;
            return this;
        }

        public MqttConnectMessage build() {
            MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
            MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(this.version.protocolName(), this.version.protocolLevel(), this.hasUser, this.hasPassword, this.willRetain, this.willQos.value(), this.willFlag, this.cleanSession, this.keepAliveSecs);
            MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(this.clientId, this.willTopic, this.willMessage, this.username, this.password);
            return new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
        }
    }

    public static final class PublishBuilder {
        private String topic;
        private boolean retained;
        private MqttQoS qos;
        private ByteBuf payload;
        private int messageId;

        PublishBuilder() {
        }

        public PublishBuilder topicName(String topic) {
            this.topic = topic;
            return this;
        }

        public PublishBuilder retained(boolean retained) {
            this.retained = retained;
            return this;
        }

        public PublishBuilder qos(MqttQoS qos) {
            this.qos = qos;
            return this;
        }

        public PublishBuilder payload(ByteBuf payload) {
            this.payload = payload;
            return this;
        }

        public PublishBuilder messageId(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public MqttPublishMessage build() {
            MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, this.qos, this.retained, 0);
            MqttPublishVariableHeader mqttVariableHeader = new MqttPublishVariableHeader(this.topic, this.messageId);
            return new MqttPublishMessage(mqttFixedHeader, mqttVariableHeader, Unpooled.buffer().writeBytes(this.payload));
        }
    }

}

