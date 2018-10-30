/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;

public final class MqttSubscribeMessage
extends MqttMessage {
    public MqttSubscribeMessage(MqttFixedHeader mqttFixedHeader, MqttMessageIdVariableHeader variableHeader, MqttSubscribePayload payload) {
        super(mqttFixedHeader, variableHeader, payload);
    }

    @Override
    public MqttMessageIdVariableHeader variableHeader() {
        return (MqttMessageIdVariableHeader)super.variableHeader();
    }

    @Override
    public MqttSubscribePayload payload() {
        return (MqttSubscribePayload)super.payload();
    }
}

