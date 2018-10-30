/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;

public final class Http2Settings
extends CharObjectHashMap<Long> {
    private static final int DEFAULT_CAPACITY = 13;
    private static final Long FALSE = 0L;
    private static final Long TRUE = 1L;

    public Http2Settings() {
        this(13);
    }

    public Http2Settings(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Http2Settings(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public Long put(char key, Long value) {
        Http2Settings.verifyStandardSetting(key, value);
        return super.put(key, value);
    }

    public Long headerTableSize() {
        return (Long)this.get('\u0001');
    }

    public Http2Settings headerTableSize(long value) {
        this.put('\u0001', value);
        return this;
    }

    public Boolean pushEnabled() {
        Long value = (Long)this.get('\u0002');
        if (value == null) {
            return null;
        }
        return TRUE.equals(value);
    }

    public Http2Settings pushEnabled(boolean enabled) {
        this.put('\u0002', enabled ? TRUE : FALSE);
        return this;
    }

    public Long maxConcurrentStreams() {
        return (Long)this.get('\u0003');
    }

    public Http2Settings maxConcurrentStreams(long value) {
        this.put('\u0003', value);
        return this;
    }

    public Integer initialWindowSize() {
        return this.getIntValue('\u0004');
    }

    public Http2Settings initialWindowSize(int value) {
        this.put('\u0004', Long.valueOf(value));
        return this;
    }

    public Integer maxFrameSize() {
        return this.getIntValue('\u0005');
    }

    public Http2Settings maxFrameSize(int value) {
        this.put('\u0005', Long.valueOf(value));
        return this;
    }

    public Long maxHeaderListSize() {
        return (Long)this.get('\u0006');
    }

    public Http2Settings maxHeaderListSize(long value) {
        this.put('\u0006', value);
        return this;
    }

    public Http2Settings copyFrom(Http2Settings settings) {
        this.clear();
        this.putAll(settings);
        return this;
    }

    public Integer getIntValue(char key) {
        Long value = (Long)this.get(key);
        if (value == null) {
            return null;
        }
        return value.intValue();
    }

    private static void verifyStandardSetting(int key, Long value) {
        ObjectUtil.checkNotNull(value, "value");
        switch (key) {
            case 1: {
                if (value >= 0L && value <= 0xFFFFFFFFL) break;
                throw new IllegalArgumentException("Setting HEADER_TABLE_SIZE is invalid: " + value);
            }
            case 2: {
                if (value == 0L || value == 1L) break;
                throw new IllegalArgumentException("Setting ENABLE_PUSH is invalid: " + value);
            }
            case 3: {
                if (value >= 0L && value <= 0xFFFFFFFFL) break;
                throw new IllegalArgumentException("Setting MAX_CONCURRENT_STREAMS is invalid: " + value);
            }
            case 4: {
                if (value >= 0L && value <= Integer.MAX_VALUE) break;
                throw new IllegalArgumentException("Setting INITIAL_WINDOW_SIZE is invalid: " + value);
            }
            case 5: {
                if (Http2CodecUtil.isMaxFrameSizeValid(value.intValue())) break;
                throw new IllegalArgumentException("Setting MAX_FRAME_SIZE is invalid: " + value);
            }
            case 6: {
                if (value >= 0L && value <= 0xFFFFFFFFL) break;
                throw new IllegalArgumentException("Setting MAX_HEADER_LIST_SIZE is invalid: " + value);
            }
        }
    }

    @Override
    protected String keyToString(char key) {
        switch (key) {
            case '\u0001': {
                return "HEADER_TABLE_SIZE";
            }
            case '\u0002': {
                return "ENABLE_PUSH";
            }
            case '\u0003': {
                return "MAX_CONCURRENT_STREAMS";
            }
            case '\u0004': {
                return "INITIAL_WINDOW_SIZE";
            }
            case '\u0005': {
                return "MAX_FRAME_SIZE";
            }
            case '\u0006': {
                return "MAX_HEADER_LIST_SIZE";
            }
        }
        return super.keyToString(key);
    }

    public static Http2Settings defaultSettings() {
        return new Http2Settings().maxHeaderListSize(8192L);
    }
}

