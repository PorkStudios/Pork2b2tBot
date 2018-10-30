/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.util.internal.StringUtil;

public final class MqttConnAckVariableHeader {
    private final MqttConnectReturnCode connectReturnCode;
    private final boolean sessionPresent;

    public MqttConnAckVariableHeader(MqttConnectReturnCode connectReturnCode, boolean sessionPresent) {
        this.connectReturnCode = connectReturnCode;
        this.sessionPresent = sessionPresent;
    }

    public MqttConnectReturnCode connectReturnCode() {
        return this.connectReturnCode;
    }

    public boolean isSessionPresent() {
        return this.sessionPresent;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "connectReturnCode=" + (Object)((Object)this.connectReturnCode) + ", sessionPresent=" + this.sessionPresent + ']';
    }
}

