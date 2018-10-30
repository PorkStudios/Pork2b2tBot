/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.JZlibDecoder;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
    private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
    private static final int DEFAULT_JDK_MEM_LEVEL = 8;
    private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", PlatformDependent.javaVersion() < 7);
    private static final boolean noJdkZlibEncoder;
    private static final boolean supportsWindowSizeAndMemLevel;

    public static boolean isSupportingWindowSizeAndMemLevel() {
        return supportsWindowSizeAndMemLevel;
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
            return new JZlibEncoder(compressionLevel);
        }
        return new JdkZlibEncoder(compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
            return new JZlibEncoder(wrapper);
        }
        return new JdkZlibEncoder(wrapper);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
            return new JZlibEncoder(wrapper, compressionLevel);
        }
        return new JdkZlibEncoder(wrapper, compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder || windowBits != 15 || memLevel != 8) {
            return new JZlibEncoder(wrapper, compressionLevel, windowBits, memLevel);
        }
        return new JdkZlibEncoder(wrapper, compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
            return new JZlibEncoder(dictionary);
        }
        return new JdkZlibEncoder(dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
            return new JZlibEncoder(compressionLevel, dictionary);
        }
        return new JdkZlibEncoder(compressionLevel, dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder || windowBits != 15 || memLevel != 8) {
            return new JZlibEncoder(compressionLevel, windowBits, memLevel, dictionary);
        }
        return new JdkZlibEncoder(compressionLevel, dictionary);
    }

    public static ZlibDecoder newZlibDecoder() {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder();
        }
        return new JdkZlibDecoder();
    }

    public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder(wrapper);
        }
        return new JdkZlibDecoder(wrapper);
    }

    public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder(dictionary);
        }
        return new JdkZlibDecoder(dictionary);
    }

    private ZlibCodecFactory() {
    }

    static {
        logger.debug("-Dio.netty.noJdkZlibDecoder: {}", (Object)noJdkZlibDecoder);
        noJdkZlibEncoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibEncoder", false);
        logger.debug("-Dio.netty.noJdkZlibEncoder: {}", (Object)noJdkZlibEncoder);
        supportsWindowSizeAndMemLevel = noJdkZlibDecoder || PlatformDependent.javaVersion() >= 7;
    }
}

