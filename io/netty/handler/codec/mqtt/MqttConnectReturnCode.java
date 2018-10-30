/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MqttConnectReturnCode {
    CONNECTION_ACCEPTED(0),
    CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION(1),
    CONNECTION_REFUSED_IDENTIFIER_REJECTED(2),
    CONNECTION_REFUSED_SERVER_UNAVAILABLE(3),
    CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD(4),
    CONNECTION_REFUSED_NOT_AUTHORIZED(5);
    
    private static final Map<Byte, MqttConnectReturnCode> VALUE_TO_CODE_MAP;
    private final byte byteValue;

    private MqttConnectReturnCode(byte byteValue) {
        this.byteValue = byteValue;
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public static MqttConnectReturnCode valueOf(byte b) {
        if (VALUE_TO_CODE_MAP.containsKey(b)) {
            return VALUE_TO_CODE_MAP.get(b);
        }
        throw new IllegalArgumentException("unknown connect return code: " + (b & 255));
    }

    static {
        HashMap<Byte, MqttConnectReturnCode> valueMap = new HashMap<Byte, MqttConnectReturnCode>();
        for (MqttConnectReturnCode code : MqttConnectReturnCode.values()) {
            valueMap.put(code.byteValue, code);
        }
        VALUE_TO_CODE_MAP = Collections.unmodifiableMap(valueMap);
    }
}

