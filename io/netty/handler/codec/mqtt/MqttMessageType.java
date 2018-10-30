/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

public enum MqttMessageType {
    CONNECT(1),
    CONNACK(2),
    PUBLISH(3),
    PUBACK(4),
    PUBREC(5),
    PUBREL(6),
    PUBCOMP(7),
    SUBSCRIBE(8),
    SUBACK(9),
    UNSUBSCRIBE(10),
    UNSUBACK(11),
    PINGREQ(12),
    PINGRESP(13),
    DISCONNECT(14);
    
    private final int value;

    private MqttMessageType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static MqttMessageType valueOf(int type) {
        for (MqttMessageType t : MqttMessageType.values()) {
            if (t.value != type) continue;
            return t;
        }
        throw new IllegalArgumentException("unknown message type: " + type);
    }
}

