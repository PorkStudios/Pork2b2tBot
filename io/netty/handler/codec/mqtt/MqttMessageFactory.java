/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;

public final class MqttMessageFactory {
    public static MqttMessage newMessage(MqttFixedHeader mqttFixedHeader, Object variableHeader, Object payload) {
        switch (mqttFixedHeader.messageType()) {
            case CONNECT: {
                return new MqttConnectMessage(mqttFixedHeader, (MqttConnectVariableHeader)variableHeader, (MqttConnectPayload)payload);
            }
            case CONNACK: {
                return new MqttConnAckMessage(mqttFixedHeader, (MqttConnAckVariableHeader)variableHeader);
            }
            case SUBSCRIBE: {
                return new MqttSubscribeMessage(mqttFixedHeader, (MqttMessageIdVariableHeader)variableHeader, (MqttSubscribePayload)payload);
            }
            case SUBACK: {
                return new MqttSubAckMessage(mqttFixedHeader, (MqttMessageIdVariableHeader)variableHeader, (MqttSubAckPayload)payload);
            }
            case UNSUBACK: {
                return new MqttUnsubAckMessage(mqttFixedHeader, (MqttMessageIdVariableHeader)variableHeader);
            }
            case UNSUBSCRIBE: {
                return new MqttUnsubscribeMessage(mqttFixedHeader, (MqttMessageIdVariableHeader)variableHeader, (MqttUnsubscribePayload)payload);
            }
            case PUBLISH: {
                return new MqttPublishMessage(mqttFixedHeader, (MqttPublishVariableHeader)variableHeader, (ByteBuf)payload);
            }
            case PUBACK: {
                return new MqttPubAckMessage(mqttFixedHeader, (MqttMessageIdVariableHeader)variableHeader);
            }
            case PUBREC: 
            case PUBREL: 
            case PUBCOMP: {
                return new MqttMessage(mqttFixedHeader, variableHeader);
            }
            case PINGREQ: 
            case PINGRESP: 
            case DISCONNECT: {
                return new MqttMessage(mqttFixedHeader);
            }
        }
        throw new IllegalArgumentException("unknown message type: " + (Object)((Object)mqttFixedHeader.messageType()));
    }

    public static MqttMessage newInvalidMessage(Throwable cause) {
        return new MqttMessage(null, null, null, DecoderResult.failure(cause));
    }

    private MqttMessageFactory() {
    }

}

