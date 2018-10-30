/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderException;

public final class MqttIdentifierRejectedException
extends DecoderException {
    private static final long serialVersionUID = -1323503322689614981L;

    public MqttIdentifierRejectedException() {
    }

    public MqttIdentifierRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqttIdentifierRejectedException(String message) {
        super(message);
    }

    public MqttIdentifierRejectedException(Throwable cause) {
        super(cause);
    }
}

