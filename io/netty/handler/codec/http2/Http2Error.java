/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

public enum Http2Error {
    NO_ERROR(0L),
    PROTOCOL_ERROR(1L),
    INTERNAL_ERROR(2L),
    FLOW_CONTROL_ERROR(3L),
    SETTINGS_TIMEOUT(4L),
    STREAM_CLOSED(5L),
    FRAME_SIZE_ERROR(6L),
    REFUSED_STREAM(7L),
    CANCEL(8L),
    COMPRESSION_ERROR(9L),
    CONNECT_ERROR(10L),
    ENHANCE_YOUR_CALM(11L),
    INADEQUATE_SECURITY(12L),
    HTTP_1_1_REQUIRED(13L);
    
    private final long code;
    private static final Http2Error[] INT_TO_ENUM_MAP;

    private Http2Error(long code) {
        this.code = code;
    }

    public long code() {
        return this.code;
    }

    public static Http2Error valueOf(long value) {
        return value >= (long)INT_TO_ENUM_MAP.length || value < 0L ? null : INT_TO_ENUM_MAP[(int)value];
    }

    static {
        Http2Error[] errors = Http2Error.values();
        Http2Error[] map = new Http2Error[errors.length];
        Http2Error[] arrhttp2Error = errors;
        int n = arrhttp2Error.length;
        for (int i = 0; i < n; ++i) {
            Http2Error error;
            map[(int)error.code()] = error = arrhttp2Error[i];
        }
        INT_TO_ENUM_MAP = map;
    }
}

