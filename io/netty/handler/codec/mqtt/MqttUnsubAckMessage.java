/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;

public final class MqttUnsubAckMessage
extends MqttMessage {
    public MqttUnsubAckMessage(MqttFixedHeader mqttFixedHeader, MqttMessageIdVariableHeader variableHeader) {
        super(mqttFixedHeader, variableHeader, null);
    }

    @Override
    public MqttMessageIdVariableHeader variableHeader() {
        return (MqttMessageIdVariableHeader)super.variableHeader();
    }
}

