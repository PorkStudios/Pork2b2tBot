/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

public enum MqttQoS {
    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2),
    FAILURE(128);
    
    private final int value;

    private MqttQoS(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static MqttQoS valueOf(int value) {
        for (MqttQoS q : MqttQoS.values()) {
            if (q.value != value) continue;
            return q;
        }
        throw new IllegalArgumentException("invalid QoS: " + value);
    }
}

