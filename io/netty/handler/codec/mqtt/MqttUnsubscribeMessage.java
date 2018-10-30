/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;

public final class MqttUnsubscribeMessage
extends MqttMessage {
    public MqttUnsubscribeMessage(MqttFixedHeader mqttFixedHeader, MqttMessageIdVariableHeader variableHeader, MqttUnsubscribePayload payload) {
        super(mqttFixedHeader, variableHeader, payload);
    }

    @Override
    public MqttMessageIdVariableHeader variableHeader() {
        return (MqttMessageIdVariableHeader)super.variableHeader();
    }

    @Override
    public MqttUnsubscribePayload payload() {
        return (MqttUnsubscribePayload)super.payload();
    }
}

