/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.mqtt.MqttUnacceptableProtocolVersionException;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;

public enum MqttVersion {
    MQTT_3_1("MQIsdp", 3),
    MQTT_3_1_1("MQTT", 4);
    
    private final String name;
    private final byte level;

    private MqttVersion(String protocolName, byte protocolLevel) {
        this.name = ObjectUtil.checkNotNull(protocolName, "protocolName");
        this.level = protocolLevel;
    }

    public String protocolName() {
        return this.name;
    }

    public byte[] protocolNameBytes() {
        return this.name.getBytes(CharsetUtil.UTF_8);
    }

    public byte protocolLevel() {
        return this.level;
    }

    public static MqttVersion fromProtocolNameAndLevel(String protocolName, byte protocolLevel) {
        for (MqttVersion mv : MqttVersion.values()) {
            if (!mv.name.equals(protocolName)) continue;
            if (mv.level == protocolLevel) {
                return mv;
            }
            throw new MqttUnacceptableProtocolVersionException(protocolName + " and " + protocolLevel + " are not match");
        }
        throw new MqttUnacceptableProtocolVersionException(protocolName + "is unknown protocol name");
    }
}

