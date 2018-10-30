/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;

public class MqttPublishMessage
extends MqttMessage
implements ByteBufHolder {
    public MqttPublishMessage(MqttFixedHeader mqttFixedHeader, MqttPublishVariableHeader variableHeader, ByteBuf payload) {
        super(mqttFixedHeader, variableHeader, payload);
    }

    @Override
    public MqttPublishVariableHeader variableHeader() {
        return (MqttPublishVariableHeader)super.variableHeader();
    }

    @Override
    public ByteBuf payload() {
        return this.content();
    }

    @Override
    public ByteBuf content() {
        ByteBuf data = (ByteBuf)super.payload();
        if (data.refCnt() <= 0) {
            throw new IllegalReferenceCountException(data.refCnt());
        }
        return data;
    }

    @Override
    public MqttPublishMessage copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public MqttPublishMessage duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public MqttPublishMessage retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public MqttPublishMessage replace(ByteBuf content) {
        return new MqttPublishMessage(this.fixedHeader(), this.variableHeader(), content);
    }

    @Override
    public int refCnt() {
        return this.content().refCnt();
    }

    @Override
    public MqttPublishMessage retain() {
        this.content().retain();
        return this;
    }

    @Override
    public MqttPublishMessage retain(int increment) {
        this.content().retain(increment);
        return this;
    }

    @Override
    public MqttPublishMessage touch() {
        this.content().touch();
        return this;
    }

    @Override
    public MqttPublishMessage touch(Object hint) {
        this.content().touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.content().release();
    }

    @Override
    public boolean release(int decrement) {
        return this.content().release(decrement);
    }
}

