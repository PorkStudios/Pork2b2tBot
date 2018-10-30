/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.RedisCodecException;

public enum RedisMessageType {
    SIMPLE_STRING(43, true),
    ERROR(45, true),
    INTEGER(58, true),
    BULK_STRING(36, false),
    ARRAY_HEADER(42, false),
    ARRAY(42, false);
    
    private final byte value;
    private final boolean inline;

    private RedisMessageType(byte value, boolean inline) {
        this.value = value;
        this.inline = inline;
    }

    public byte value() {
        return this.value;
    }

    public boolean isInline() {
        return this.inline;
    }

    public static RedisMessageType valueOf(byte value) {
        switch (value) {
            case 43: {
                return SIMPLE_STRING;
            }
            case 45: {
                return ERROR;
            }
            case 58: {
                return INTEGER;
            }
            case 36: {
                return BULK_STRING;
            }
            case 42: {
                return ARRAY_HEADER;
            }
        }
        throw new RedisCodecException("Unknown RedisMessageType: " + value);
    }
}

