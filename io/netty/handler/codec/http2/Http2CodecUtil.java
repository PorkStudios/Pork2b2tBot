/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.StreamByteDistributor;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public final class Http2CodecUtil {
    public static final int CONNECTION_STREAM_ID = 0;
    public static final int HTTP_UPGRADE_STREAM_ID = 1;
    public static final CharSequence HTTP_UPGRADE_SETTINGS_HEADER = AsciiString.cached("HTTP2-Settings");
    public static final CharSequence HTTP_UPGRADE_PROTOCOL_NAME = "h2c";
    public static final CharSequence TLS_UPGRADE_PROTOCOL_NAME = "h2";
    public static final int PING_FRAME_PAYLOAD_LENGTH = 8;
    public static final short MAX_UNSIGNED_BYTE = 255;
    public static final int MAX_PADDING = 256;
    public static final long MAX_UNSIGNED_INT = 0xFFFFFFFFL;
    public static final int FRAME_HEADER_LENGTH = 9;
    public static final int SETTING_ENTRY_LENGTH = 6;
    public static final int PRIORITY_ENTRY_LENGTH = 5;
    public static final int INT_FIELD_LENGTH = 4;
    public static final short MAX_WEIGHT = 256;
    public static final short MIN_WEIGHT = 1;
    private static final ByteBuf CONNECTION_PREFACE = Unpooled.unreleasableBuffer(Unpooled.directBuffer(24).writeBytes("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(CharsetUtil.UTF_8))).asReadOnly();
    private static final ByteBuf EMPTY_PING = Unpooled.unreleasableBuffer(Unpooled.directBuffer(8).writeZero(8)).asReadOnly();
    private static final int MAX_PADDING_LENGTH_LENGTH = 1;
    public static final int DATA_FRAME_HEADER_LENGTH = 10;
    public static final int HEADERS_FRAME_HEADER_LENGTH = 15;
    public static final int PRIORITY_FRAME_LENGTH = 14;
    public static final int RST_STREAM_FRAME_LENGTH = 13;
    public static final int PUSH_PROMISE_FRAME_HEADER_LENGTH = 14;
    public static final int GO_AWAY_FRAME_HEADER_LENGTH = 17;
    public static final int WINDOW_UPDATE_FRAME_LENGTH = 13;
    public static final int CONTINUATION_FRAME_HEADER_LENGTH = 10;
    public static final char SETTINGS_HEADER_TABLE_SIZE = '\u0001';
    public static final char SETTINGS_ENABLE_PUSH = '\u0002';
    public static final char SETTINGS_MAX_CONCURRENT_STREAMS = '\u0003';
    public static final char SETTINGS_INITIAL_WINDOW_SIZE = '\u0004';
    public static final char SETTINGS_MAX_FRAME_SIZE = '\u0005';
    public static final char SETTINGS_MAX_HEADER_LIST_SIZE = '\u0006';
    public static final int NUM_STANDARD_SETTINGS = 6;
    public static final long MAX_HEADER_TABLE_SIZE = 0xFFFFFFFFL;
    public static final long MAX_CONCURRENT_STREAMS = 0xFFFFFFFFL;
    public static final int MAX_INITIAL_WINDOW_SIZE = Integer.MAX_VALUE;
    public static final int MAX_FRAME_SIZE_LOWER_BOUND = 16384;
    public static final int MAX_FRAME_SIZE_UPPER_BOUND = 16777215;
    public static final long MAX_HEADER_LIST_SIZE = 0xFFFFFFFFL;
    public static final long MIN_HEADER_TABLE_SIZE = 0L;
    public static final long MIN_CONCURRENT_STREAMS = 0L;
    public static final int MIN_INITIAL_WINDOW_SIZE = 0;
    public static final long MIN_HEADER_LIST_SIZE = 0L;
    public static final int DEFAULT_WINDOW_SIZE = 65535;
    public static final short DEFAULT_PRIORITY_WEIGHT = 16;
    public static final int DEFAULT_HEADER_TABLE_SIZE = 4096;
    public static final long DEFAULT_HEADER_LIST_SIZE = 8192L;
    public static final int DEFAULT_MAX_FRAME_SIZE = 16384;
    public static final int SMALLEST_MAX_CONCURRENT_STREAMS = 100;
    static final int DEFAULT_MAX_RESERVED_STREAMS = 100;
    static final int DEFAULT_MIN_ALLOCATION_CHUNK = 1024;
    static final int DEFAULT_INITIAL_HUFFMAN_DECODE_CAPACITY = 32;
    public static final long DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS = TimeUnit.MILLISECONDS.convert(30L, TimeUnit.SECONDS);

    public static long calculateMaxHeaderListSizeGoAway(long maxHeaderListSize) {
        return maxHeaderListSize + (maxHeaderListSize >>> 2);
    }

    public static boolean isOutboundStream(boolean server, int streamId) {
        boolean even = (streamId & 1) == 0;
        return streamId > 0 && server == even;
    }

    public static boolean isStreamIdValid(int streamId) {
        return streamId >= 0;
    }

    public static boolean isMaxFrameSizeValid(int maxFrameSize) {
        return maxFrameSize >= 16384 && maxFrameSize <= 16777215;
    }

    public static ByteBuf connectionPrefaceBuf() {
        return CONNECTION_PREFACE.retainedDuplicate();
    }

    public static ByteBuf emptyPingBuf() {
        return EMPTY_PING.retainedDuplicate();
    }

    public static Http2Exception getEmbeddedHttp2Exception(Throwable cause) {
        while (cause != null) {
            if (cause instanceof Http2Exception) {
                return (Http2Exception)cause;
            }
            cause = cause.getCause();
        }
        return null;
    }

    public static ByteBuf toByteBuf(ChannelHandlerContext ctx, Throwable cause) {
        if (cause == null || cause.getMessage() == null) {
            return Unpooled.EMPTY_BUFFER;
        }
        return ByteBufUtil.writeUtf8(ctx.alloc(), (CharSequence)cause.getMessage());
    }

    public static int readUnsignedInt(ByteBuf buf) {
        return buf.readInt() & Integer.MAX_VALUE;
    }

    public static void writeFrameHeader(ByteBuf out, int payloadLength, byte type, Http2Flags flags, int streamId) {
        out.ensureWritable(9 + payloadLength);
        Http2CodecUtil.writeFrameHeaderInternal(out, payloadLength, type, flags, streamId);
    }

    public static int streamableBytes(StreamByteDistributor.StreamState state) {
        return Math.max(0, Math.min(state.pendingBytes(), state.windowSize()));
    }

    public static void headerListSizeExceeded(int streamId, long maxHeaderListSize, boolean onDecode) throws Http2Exception {
        throw Http2Exception.headerListSizeError(streamId, Http2Error.PROTOCOL_ERROR, onDecode, "Header size exceeded max allowed size (%d)", maxHeaderListSize);
    }

    public static void headerListSizeExceeded(long maxHeaderListSize) throws Http2Exception {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size (%d)", maxHeaderListSize);
    }

    static void writeFrameHeaderInternal(ByteBuf out, int payloadLength, byte type, Http2Flags flags, int streamId) {
        out.writeMedium(payloadLength);
        out.writeByte(type);
        out.writeByte(flags.value());
        out.writeInt(streamId);
    }

    public static void verifyPadding(int padding) {
        if (padding < 0 || padding > 256) {
            throw new IllegalArgumentException(String.format("Invalid padding '%d'. Padding must be between 0 and %d (inclusive).", padding, 256));
        }
    }

    private Http2CodecUtil() {
    }

    static final class SimpleChannelPromiseAggregator
    extends DefaultChannelPromise {
        private final ChannelPromise promise;
        private int expectedCount;
        private int doneCount;
        private Throwable lastFailure;
        private boolean doneAllocating;

        SimpleChannelPromiseAggregator(ChannelPromise promise, Channel c, EventExecutor e) {
            super(c, e);
            assert (promise != null && !promise.isDone());
            this.promise = promise;
        }

        public ChannelPromise newPromise() {
            assert (!this.doneAllocating);
            ++this.expectedCount;
            return this;
        }

        public ChannelPromise doneAllocatingPromises() {
            if (!this.doneAllocating) {
                this.doneAllocating = true;
                if (this.doneCount == this.expectedCount || this.expectedCount == 0) {
                    return this.setPromise();
                }
            }
            return this;
        }

        @Override
        public boolean tryFailure(Throwable cause) {
            if (this.allowFailure()) {
                ++this.doneCount;
                this.lastFailure = cause;
                if (this.allPromisesDone()) {
                    return this.tryPromise();
                }
                return true;
            }
            return false;
        }

        @Override
        public ChannelPromise setFailure(Throwable cause) {
            if (this.allowFailure()) {
                ++this.doneCount;
                this.lastFailure = cause;
                if (this.allPromisesDone()) {
                    return this.setPromise();
                }
            }
            return this;
        }

        @Override
        public ChannelPromise setSuccess(Void result) {
            if (this.awaitingPromises()) {
                ++this.doneCount;
                if (this.allPromisesDone()) {
                    this.setPromise();
                }
            }
            return this;
        }

        @Override
        public boolean trySuccess(Void result) {
            if (this.awaitingPromises()) {
                ++this.doneCount;
                if (this.allPromisesDone()) {
                    return this.tryPromise();
                }
                return true;
            }
            return false;
        }

        private boolean allowFailure() {
            return this.awaitingPromises() || this.expectedCount == 0;
        }

        private boolean awaitingPromises() {
            return this.doneCount < this.expectedCount;
        }

        private boolean allPromisesDone() {
            return this.doneCount == this.expectedCount && this.doneAllocating;
        }

        private ChannelPromise setPromise() {
            if (this.lastFailure == null) {
                this.promise.setSuccess();
                return super.setSuccess(null);
            }
            this.promise.setFailure(this.lastFailure);
            return super.setFailure(this.lastFailure);
        }

        private boolean tryPromise() {
            if (this.lastFailure == null) {
                this.promise.trySuccess();
                return super.trySuccess(null);
            }
            this.promise.tryFailure(this.lastFailure);
            return super.tryFailure(this.lastFailure);
        }
    }

}

