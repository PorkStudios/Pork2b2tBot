/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.charset.Charset;

final class RedisCodecUtil {
    private RedisCodecUtil() {
    }

    static byte[] longToAsciiBytes(long value) {
        return Long.toString(value).getBytes(CharsetUtil.US_ASCII);
    }

    static short makeShort(char first, char second) {
        return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)(second << 8 | first) : (short)(first << 8 | second);
    }

    static byte[] shortToBytes(short value) {
        byte[] bytes = new byte[2];
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            bytes[1] = (byte)(value >> 8 & 255);
            bytes[0] = (byte)(value & 255);
        } else {
            bytes[0] = (byte)(value >> 8 & 255);
            bytes[1] = (byte)(value & 255);
        }
        return bytes;
    }
}

