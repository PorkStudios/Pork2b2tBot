/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLHandshakeException;

final class SslUtils {
    static final String PROTOCOL_SSL_V2_HELLO = "SSLv2Hello";
    static final String PROTOCOL_SSL_V2 = "SSLv2";
    static final String PROTOCOL_SSL_V3 = "SSLv3";
    static final String PROTOCOL_TLS_V1 = "TLSv1";
    static final String PROTOCOL_TLS_V1_1 = "TLSv1.1";
    static final String PROTOCOL_TLS_V1_2 = "TLSv1.2";
    static final int SSL_CONTENT_TYPE_CHANGE_CIPHER_SPEC = 20;
    static final int SSL_CONTENT_TYPE_ALERT = 21;
    static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
    static final int SSL_CONTENT_TYPE_APPLICATION_DATA = 23;
    static final int SSL_CONTENT_TYPE_EXTENSION_HEARTBEAT = 24;
    static final int SSL_RECORD_HEADER_LENGTH = 5;
    static final int NOT_ENOUGH_DATA = -1;
    static final int NOT_ENCRYPTED = -2;
    static final String[] DEFAULT_CIPHER_SUITES = new String[]{"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA"};

    static /* varargs */ void addIfSupported(Set<String> supported, List<String> enabled, String ... names) {
        for (String n : names) {
            if (!supported.contains(n)) continue;
            enabled.add(n);
        }
    }

    static void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, Iterable<String> fallbackCiphers) {
        if (defaultCiphers.isEmpty()) {
            for (String cipher : fallbackCiphers) {
                if (cipher.startsWith("SSL_") || cipher.contains("_RC4_")) continue;
                defaultCiphers.add(cipher);
            }
        }
    }

    static /* varargs */ void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, String ... fallbackCiphers) {
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, Arrays.asList(fallbackCiphers));
    }

    static SSLHandshakeException toSSLHandshakeException(Throwable e) {
        if (e instanceof SSLHandshakeException) {
            return (SSLHandshakeException)e;
        }
        return (SSLHandshakeException)new SSLHandshakeException(e.getMessage()).initCause(e);
    }

    static int getEncryptedPacketLength(ByteBuf buffer, int offset) {
        boolean tls;
        int packetLength = 0;
        switch (buffer.getUnsignedByte(offset)) {
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: {
                tls = true;
                break;
            }
            default: {
                tls = false;
            }
        }
        if (tls) {
            short majorVersion = buffer.getUnsignedByte(offset + 1);
            if (majorVersion == 3) {
                packetLength = SslUtils.unsignedShortBE(buffer, offset + 3) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            } else {
                tls = false;
            }
        }
        if (!tls) {
            int headerLength = (buffer.getUnsignedByte(offset) & 128) != 0 ? 2 : 3;
            short majorVersion = buffer.getUnsignedByte(offset + headerLength + 1);
            if (majorVersion == 2 || majorVersion == 3) {
                int n = packetLength = headerLength == 2 ? (SslUtils.shortBE(buffer, offset) & 32767) + 2 : (SslUtils.shortBE(buffer, offset) & 16383) + 3;
                if (packetLength <= headerLength) {
                    return -1;
                }
            } else {
                return -2;
            }
        }
        return packetLength;
    }

    private static int unsignedShortBE(ByteBuf buffer, int offset) {
        return buffer.order() == ByteOrder.BIG_ENDIAN ? buffer.getUnsignedShort(offset) : buffer.getUnsignedShortLE(offset);
    }

    private static short shortBE(ByteBuf buffer, int offset) {
        return buffer.order() == ByteOrder.BIG_ENDIAN ? buffer.getShort(offset) : buffer.getShortLE(offset);
    }

    private static short unsignedByte(byte b) {
        return (short)(b & 255);
    }

    private static int unsignedShortBE(ByteBuffer buffer, int offset) {
        return SslUtils.shortBE(buffer, offset) & 65535;
    }

    private static short shortBE(ByteBuffer buffer, int offset) {
        return buffer.order() == ByteOrder.BIG_ENDIAN ? buffer.getShort(offset) : ByteBufUtil.swapShort(buffer.getShort(offset));
    }

    static int getEncryptedPacketLength(ByteBuffer[] buffers, int offset) {
        ByteBuffer buffer = buffers[offset];
        if (buffer.remaining() >= 5) {
            return SslUtils.getEncryptedPacketLength(buffer);
        }
        ByteBuffer tmp = ByteBuffer.allocate(5);
        do {
            if ((buffer = buffers[offset++].duplicate()).remaining() > tmp.remaining()) {
                buffer.limit(buffer.position() + tmp.remaining());
            }
            tmp.put(buffer);
        } while (tmp.hasRemaining());
        tmp.flip();
        return SslUtils.getEncryptedPacketLength(tmp);
    }

    private static int getEncryptedPacketLength(ByteBuffer buffer) {
        boolean tls;
        int packetLength = 0;
        int pos = buffer.position();
        switch (SslUtils.unsignedByte(buffer.get(pos))) {
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: {
                tls = true;
                break;
            }
            default: {
                tls = false;
            }
        }
        if (tls) {
            short majorVersion = SslUtils.unsignedByte(buffer.get(pos + 1));
            if (majorVersion == 3) {
                packetLength = SslUtils.unsignedShortBE(buffer, pos + 3) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            } else {
                tls = false;
            }
        }
        if (!tls) {
            int headerLength = (SslUtils.unsignedByte(buffer.get(pos)) & 128) != 0 ? 2 : 3;
            short majorVersion = SslUtils.unsignedByte(buffer.get(pos + headerLength + 1));
            if (majorVersion == 2 || majorVersion == 3) {
                int n = packetLength = headerLength == 2 ? (SslUtils.shortBE(buffer, pos) & 32767) + 2 : (SslUtils.shortBE(buffer, pos) & 16383) + 3;
                if (packetLength <= headerLength) {
                    return -1;
                }
            } else {
                return -2;
            }
        }
        return packetLength;
    }

    static void notifyHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean notify) {
        ctx.flush();
        if (notify) {
            ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
        }
        ctx.close();
    }

    static void zeroout(ByteBuf buffer) {
        if (!buffer.isReadOnly()) {
            buffer.setZero(0, buffer.capacity());
        }
    }

    static void zerooutAndRelease(ByteBuf buffer) {
        SslUtils.zeroout(buffer);
        buffer.release();
    }

    static ByteBuf toBase64(ByteBufAllocator allocator, ByteBuf src) {
        ByteBuf dst = Base64.encode(src, src.readerIndex(), src.readableBytes(), true, Base64Dialect.STANDARD, allocator);
        src.readerIndex(src.writerIndex());
        return dst;
    }

    private SslUtils() {
    }
}

