/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.DecoratingHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionAdapter;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class StreamBufferingEncoder
extends DecoratingHttp2ConnectionEncoder {
    private final TreeMap<Integer, PendingStream> pendingStreams = new TreeMap();
    private int maxConcurrentStreams;
    private boolean closed;

    public StreamBufferingEncoder(Http2ConnectionEncoder delegate) {
        this(delegate, 100);
    }

    public StreamBufferingEncoder(Http2ConnectionEncoder delegate, int initialMaxConcurrentStreams) {
        super(delegate);
        this.maxConcurrentStreams = initialMaxConcurrentStreams;
        this.connection().addListener(new Http2ConnectionAdapter(){

            @Override
            public void onGoAwayReceived(int lastStreamId, long errorCode, ByteBuf debugData) {
                StreamBufferingEncoder.this.cancelGoAwayStreams(lastStreamId, errorCode, debugData);
            }

            @Override
            public void onStreamClosed(Http2Stream stream) {
                StreamBufferingEncoder.this.tryCreatePendingStreams();
            }
        });
    }

    public int numBufferedStreams() {
        return this.pendingStreams.size();
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, ChannelPromise promise) {
        return this.writeHeaders(ctx, streamId, headers, 0, (short)16, false, padding, endStream, promise);
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream, ChannelPromise promise) {
        if (this.closed) {
            return promise.setFailure(new Http2ChannelClosedException());
        }
        if (this.isExistingStream(streamId) || this.connection().goAwayReceived()) {
            return super.writeHeaders(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream, promise);
        }
        if (this.canCreateStream()) {
            return super.writeHeaders(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream, promise);
        }
        PendingStream pendingStream = this.pendingStreams.get(streamId);
        if (pendingStream == null) {
            pendingStream = new PendingStream(ctx, streamId);
            this.pendingStreams.put(streamId, pendingStream);
        }
        pendingStream.frames.add(new HeadersFrame(headers, streamDependency, weight, exclusive, padding, endOfStream, promise));
        return promise;
    }

    @Override
    public ChannelFuture writeRstStream(ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise) {
        if (this.isExistingStream(streamId)) {
            return super.writeRstStream(ctx, streamId, errorCode, promise);
        }
        PendingStream stream = this.pendingStreams.remove(streamId);
        if (stream != null) {
            stream.close(null);
            promise.setSuccess();
        } else {
            promise.setFailure(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream does not exist %d", streamId));
        }
        return promise;
    }

    @Override
    public ChannelFuture writeData(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream, ChannelPromise promise) {
        if (this.isExistingStream(streamId)) {
            return super.writeData(ctx, streamId, data, padding, endOfStream, promise);
        }
        PendingStream pendingStream = this.pendingStreams.get(streamId);
        if (pendingStream != null) {
            pendingStream.frames.add(new DataFrame(data, padding, endOfStream, promise));
        } else {
            ReferenceCountUtil.safeRelease(data);
            promise.setFailure(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream does not exist %d", streamId));
        }
        return promise;
    }

    @Override
    public void remoteSettings(Http2Settings settings) throws Http2Exception {
        super.remoteSettings(settings);
        this.maxConcurrentStreams = this.connection().local().maxActiveStreams();
        this.tryCreatePendingStreams();
    }

    @Override
    public void close() {
        try {
            if (!this.closed) {
                this.closed = true;
                Http2ChannelClosedException e = new Http2ChannelClosedException();
                while (!this.pendingStreams.isEmpty()) {
                    PendingStream stream = this.pendingStreams.pollFirstEntry().getValue();
                    stream.close(e);
                }
            }
        }
        finally {
            super.close();
        }
    }

    private void tryCreatePendingStreams() {
        while (!this.pendingStreams.isEmpty() && this.canCreateStream()) {
            Map.Entry<Integer, PendingStream> entry = this.pendingStreams.pollFirstEntry();
            PendingStream pendingStream = entry.getValue();
            try {
                pendingStream.sendFrames();
            }
            catch (Throwable t) {
                pendingStream.close(t);
            }
        }
    }

    private void cancelGoAwayStreams(int lastStreamId, long errorCode, ByteBuf debugData) {
        Iterator<PendingStream> iter = this.pendingStreams.values().iterator();
        Http2GoAwayException e = new Http2GoAwayException(lastStreamId, errorCode, ByteBufUtil.getBytes(debugData));
        while (iter.hasNext()) {
            PendingStream stream = iter.next();
            if (stream.streamId <= lastStreamId) continue;
            iter.remove();
            stream.close(e);
        }
    }

    private boolean canCreateStream() {
        return this.connection().local().numActiveStreams() < this.maxConcurrentStreams;
    }

    private boolean isExistingStream(int streamId) {
        return streamId <= this.connection().local().lastStreamCreated();
    }

    private final class DataFrame
    extends Frame {
        final ByteBuf data;
        final int padding;
        final boolean endOfStream;

        DataFrame(ByteBuf data, int padding, boolean endOfStream, ChannelPromise promise) {
            super(promise);
            this.data = data;
            this.padding = padding;
            this.endOfStream = endOfStream;
        }

        @Override
        void release(Throwable t) {
            super.release(t);
            ReferenceCountUtil.safeRelease(this.data);
        }

        @Override
        void send(ChannelHandlerContext ctx, int streamId) {
            StreamBufferingEncoder.this.writeData(ctx, streamId, this.data, this.padding, this.endOfStream, this.promise);
        }
    }

    private final class HeadersFrame
    extends Frame {
        final Http2Headers headers;
        final int streamDependency;
        final short weight;
        final boolean exclusive;
        final int padding;
        final boolean endOfStream;

        HeadersFrame(Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream, ChannelPromise promise) {
            super(promise);
            this.headers = headers;
            this.streamDependency = streamDependency;
            this.weight = weight;
            this.exclusive = exclusive;
            this.padding = padding;
            this.endOfStream = endOfStream;
        }

        @Override
        void send(ChannelHandlerContext ctx, int streamId) {
            StreamBufferingEncoder.this.writeHeaders(ctx, streamId, this.headers, this.streamDependency, this.weight, this.exclusive, this.padding, this.endOfStream, this.promise);
        }
    }

    private static abstract class Frame {
        final ChannelPromise promise;

        Frame(ChannelPromise promise) {
            this.promise = promise;
        }

        void release(Throwable t) {
            if (t == null) {
                this.promise.setSuccess();
            } else {
                this.promise.setFailure(t);
            }
        }

        abstract void send(ChannelHandlerContext var1, int var2);
    }

    private static final class PendingStream {
        final ChannelHandlerContext ctx;
        final int streamId;
        final Queue<Frame> frames = new ArrayDeque<Frame>(2);

        PendingStream(ChannelHandlerContext ctx, int streamId) {
            this.ctx = ctx;
            this.streamId = streamId;
        }

        void sendFrames() {
            for (Frame frame : this.frames) {
                frame.send(this.ctx, this.streamId);
            }
        }

        void close(Throwable t) {
            for (Frame frame : this.frames) {
                frame.release(t);
            }
        }
    }

    public static final class Http2GoAwayException
    extends Http2Exception {
        private static final long serialVersionUID = 1326785622777291198L;
        private final int lastStreamId;
        private final long errorCode;
        private final byte[] debugData;

        public Http2GoAwayException(int lastStreamId, long errorCode, byte[] debugData) {
            super(Http2Error.STREAM_CLOSED);
            this.lastStreamId = lastStreamId;
            this.errorCode = errorCode;
            this.debugData = debugData;
        }

        public int lastStreamId() {
            return this.lastStreamId;
        }

        public long errorCode() {
            return this.errorCode;
        }

        public byte[] debugData() {
            return this.debugData;
        }
    }

    public static final class Http2ChannelClosedException
    extends Http2Exception {
        private static final long serialVersionUID = 4768543442094476971L;

        public Http2ChannelClosedException() {
            super(Http2Error.REFUSED_STREAM, "Connection closed");
        }
    }

}

