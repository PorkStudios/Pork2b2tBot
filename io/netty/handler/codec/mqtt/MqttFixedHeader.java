/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public final class MqttFixedHeader {
    private final MqttMessageType messageType;
    private final boolean isDup;
    private final MqttQoS qosLevel;
    private final boolean isRetain;
    private final int remainingLength;

    public MqttFixedHeader(MqttMessageType messageType, boolean isDup, MqttQoS qosLevel, boolean isRetain, int remainingLength) {
        this.messageType = ObjectUtil.checkNotNull(messageType, "messageType");
        this.isDup = isDup;
        this.qosLevel = ObjectUtil.checkNotNull(qosLevel, "qosLevel");
        this.isRetain = isRetain;
        this.remainingLength = remainingLength;
    }

    public MqttMessageType messageType() {
        return this.messageType;
    }

    public boolean isDup() {
        return this.isDup;
    }

    public MqttQoS qosLevel() {
        return this.qosLevel;
    }

    public boolean isRetain() {
        return this.isRetain;
    }

    public int remainingLength() {
        return this.remainingLength;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "messageType=" + (Object)((Object)this.messageType) + ", isDup=" + this.isDup + ", qosLevel=" + (Object)((Object)this.qosLevel) + ", isRetain=" + this.isRetain + ", remainingLength=" + this.remainingLength + ']';
    }
}

