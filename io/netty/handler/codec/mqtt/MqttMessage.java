/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.util.internal.StringUtil;

public class MqttMessage {
    private final MqttFixedHeader mqttFixedHeader;
    private final Object variableHeader;
    private final Object payload;
    private final DecoderResult decoderResult;

    public MqttMessage(MqttFixedHeader mqttFixedHeader) {
        this(mqttFixedHeader, null, null);
    }

    public MqttMessage(MqttFixedHeader mqttFixedHeader, Object variableHeader) {
        this(mqttFixedHeader, variableHeader, null);
    }

    public MqttMessage(MqttFixedHeader mqttFixedHeader, Object variableHeader, Object payload) {
        this(mqttFixedHeader, variableHeader, payload, DecoderResult.SUCCESS);
    }

    public MqttMessage(MqttFixedHeader mqttFixedHeader, Object variableHeader, Object payload, DecoderResult decoderResult) {
        this.mqttFixedHeader = mqttFixedHeader;
        this.variableHeader = variableHeader;
        this.payload = payload;
        this.decoderResult = decoderResult;
    }

    public MqttFixedHeader fixedHeader() {
        return this.mqttFixedHeader;
    }

    public Object variableHeader() {
        return this.variableHeader;
    }

    public Object payload() {
        return this.payload;
    }

    public DecoderResult decoderResult() {
        return this.decoderResult;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "fixedHeader=" + (this.fixedHeader() != null ? this.fixedHeader().toString() : "") + ", variableHeader=" + (this.variableHeader() != null ? this.variableHeader.toString() : "") + ", payload=" + (this.payload() != null ? this.payload.toString() : "") + ']';
    }
}

