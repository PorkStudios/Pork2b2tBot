/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CoalescingBufferQueue;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http2.DefaultHttp2RemoteFlowController;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameSizePolicy;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.handler.codec.http2.Http2LifecycleManager;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2RemoteFlowController;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;

public class DefaultHttp2ConnectionEncoder
implements Http2ConnectionEncoder {
    private final Http2FrameWriter frameWriter;
    private final Http2Connection connection;
    private Http2LifecycleManager lifecycleManager;
    private final ArrayDeque<Http2Settings> outstandingLocalSettingsQueue = new ArrayDeque(4);

    public DefaultHttp2ConnectionEncoder(Http2Connection connection, Http2FrameWriter frameWriter) {
        this.connection = ObjectUtil.checkNotNull(connection, "connection");
        this.frameWriter = ObjectUtil.checkNotNull(frameWriter, "frameWriter");
        if (connection.remote().flowController() == null) {
            connection.remote().flowController(new DefaultHttp2RemoteFlowController(connection));
        }
    }

    @Override
    public void lifecycleManager(Http2LifecycleManager lifecycleManager) {
        this.lifecycleManager = ObjectUtil.checkNotNull(lifecycleManager, "lifecycleManager");
    }

    @Override
    public Http2FrameWriter frameWriter() {
        return this.frameWriter;
    }

    @Override
    public Http2Connection connection() {
        return this.connection;
    }

    @Override
    public final Http2RemoteFlowController flowController() {
        return this.connection().remote().flowController();
    }

    @Override
    public void remoteSettings(Http2Settings settings) throws Http2Exception {
        Long maxHeaderListSize;
        Integer maxFrameSize;
        Long maxConcurrentStreams;
        Long headerTableSize;
        Integer initialWindowSize;
        Boolean pushEnabled = settings.pushEnabled();
        Http2FrameWriter.Configuration config = this.configuration();
        Http2HeadersEncoder.Configuration outboundHeaderConfig = config.headersConfiguration();
        Http2FrameSizePolicy outboundFrameSizePolicy = config.frameSizePolicy();
        if (pushEnabled != null) {
            if (!this.connection.isServer() && pushEnabled.booleanValue()) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Client received a value of ENABLE_PUSH specified to other than 0", new Object[0]);
            }
            this.connection.remote().allowPushTo(pushEnabled);
        }
        if ((maxConcurrentStreams = settings.maxConcurrentStreams()) != null) {
            this.connection.local().maxActiveStreams((int)Math.min(maxConcurrentStreams, Integer.MAX_VALUE));
        }
        if ((headerTableSize = settings.headerTableSize()) != null) {
            outboundHeaderConfig.maxHeaderTableSize((int)Math.min(headerTableSize, Integer.MAX_VALUE));
        }
        if ((maxHeaderListSize = settings.maxHeaderListSize()) != null) {
            outboundHeaderConfig.maxHeaderListSize(maxHeaderListSize);
        }
        if ((maxFrameSize = settings.maxFrameSize()) != null) {
            outboundFrameSizePolicy.maxFrameSize(maxFrameSize);
        }
        if ((initialWindowSize = settings.initialWindowSize()) != null) {
            this.flowController().initialWindowSize(initialWindowSize);
        }
    }

    @Override
    public ChannelFuture writeData(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream, ChannelPromise promise) {
        Http2Stream stream;
        try {
            stream = this.requireStream(streamId);
            switch (stream.state()) {
                case OPEN: 
                case HALF_CLOSED_REMOTE: {
                    break;
                }
                default: {
                    throw new IllegalStateException("Stream " + stream.id() + " in unexpected state " + (Object)((Object)stream.state()));
                }
            }
        }
        catch (Throwable e) {
            data.release();
            return promise.setFailure(e);
        }
        this.flowController().addFlowControlled(stream, new FlowControlledData(stream, data, padding, endOfStream, promise));
        return promise;
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, ChannelPromise promise) {
        return this.writeHeaders(ctx, streamId, headers, 0, (short)16, false, padding, endStream, promise);
    }

    private static boolean validateHeadersSentState(Http2Stream stream, Http2Headers headers, boolean isServer, boolean endOfStream) {
        boolean isInformational;
        boolean bl = isInformational = isServer && HttpStatusClass.valueOf(headers.status()) == HttpStatusClass.INFORMATIONAL;
        if ((isInformational || !endOfStream) && stream.isHeadersSent() || stream.isTrailersSent()) {
            throw new IllegalStateException("Stream " + stream.id() + " sent too many headers EOS: " + endOfStream);
        }
        return isInformational;
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream, ChannelPromise promise) {
        try {
            Http2Stream stream = this.connection.stream(streamId);
            if (stream == null) {
                try {
                    stream = this.connection.local().createStream(streamId, endOfStream);
                }
                catch (Http2Exception cause) {
                    if (this.connection.remote().mayHaveCreatedStream(streamId)) {
                        promise.tryFailure(new IllegalStateException("Stream no longer exists: " + streamId, cause));
                        return promise;
                    }
                    throw cause;
                }
            } else {
                switch (stream.state()) {
                    case RESERVED_LOCAL: {
                        stream.open(endOfStream);
                        break;
                    }
                    case OPEN: 
                    case HALF_CLOSED_REMOTE: {
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Stream " + stream.id() + " in unexpected state " + (Object)((Object)stream.state()));
                    }
                }
            }
            Http2RemoteFlowController flowController = this.flowController();
            if (!endOfStream || !flowController.hasFlowControlled(stream)) {
                Throwable failureCause;
                ChannelFuture future;
                boolean isInformational = DefaultHttp2ConnectionEncoder.validateHeadersSentState(stream, headers, this.connection.isServer(), endOfStream);
                if (endOfStream) {
                    final Http2Stream finalStream = stream;
                    ChannelFutureListener closeStreamLocalListener = new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            DefaultHttp2ConnectionEncoder.this.lifecycleManager.closeStreamLocal(finalStream, future);
                        }
                    };
                    promise = promise.unvoid().addListener(closeStreamLocalListener);
                }
                if ((failureCause = (future = this.frameWriter.writeHeaders(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream, promise)).cause()) == null) {
                    stream.headersSent(isInformational);
                } else {
                    this.lifecycleManager.onError(ctx, failureCause);
                }
                return future;
            }
            flowController.addFlowControlled(stream, new FlowControlledHeaders(stream, headers, streamDependency, weight, exclusive, padding, true, promise));
            return promise;
        }
        catch (Throwable t) {
            this.lifecycleManager.onError(ctx, t);
            promise.tryFailure(t);
            return promise;
        }
    }

    @Override
    public ChannelFuture writePriority(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive, ChannelPromise promise) {
        return this.frameWriter.writePriority(ctx, streamId, streamDependency, weight, exclusive, promise);
    }

    @Override
    public ChannelFuture writeRstStream(ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise) {
        return this.lifecycleManager.resetStream(ctx, streamId, errorCode, promise);
    }

    @Override
    public ChannelFuture writeSettings(ChannelHandlerContext ctx, Http2Settings settings, ChannelPromise promise) {
        this.outstandingLocalSettingsQueue.add(settings);
        try {
            Boolean pushEnabled = settings.pushEnabled();
            if (pushEnabled != null && this.connection.isServer()) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server sending SETTINGS frame with ENABLE_PUSH specified", new Object[0]);
            }
        }
        catch (Throwable e) {
            return promise.setFailure(e);
        }
        return this.frameWriter.writeSettings(ctx, settings, promise);
    }

    @Override
    public ChannelFuture writeSettingsAck(ChannelHandlerContext ctx, ChannelPromise promise) {
        return this.frameWriter.writeSettingsAck(ctx, promise);
    }

    @Override
    public ChannelFuture writePing(ChannelHandlerContext ctx, boolean ack, ByteBuf data, ChannelPromise promise) {
        return this.frameWriter.writePing(ctx, ack, data, promise);
    }

    @Override
    public ChannelFuture writePushPromise(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding, ChannelPromise promise) {
        try {
            if (this.connection.goAwayReceived()) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Sending PUSH_PROMISE after GO_AWAY received.", new Object[0]);
            }
            Http2Stream stream = this.requireStream(streamId);
            this.connection.local().reservePushStream(promisedStreamId, stream);
            ChannelFuture future = this.frameWriter.writePushPromise(ctx, streamId, promisedStreamId, headers, padding, promise);
            Throwable failureCause = future.cause();
            if (failureCause == null) {
                stream.pushPromiseSent();
            } else {
                this.lifecycleManager.onError(ctx, failureCause);
            }
            return future;
        }
        catch (Throwable t) {
            this.lifecycleManager.onError(ctx, t);
            promise.tryFailure(t);
            return promise;
        }
    }

    @Override
    public ChannelFuture writeGoAway(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData, ChannelPromise promise) {
        return this.lifecycleManager.goAway(ctx, lastStreamId, errorCode, debugData, promise);
    }

    @Override
    public ChannelFuture writeWindowUpdate(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement, ChannelPromise promise) {
        return promise.setFailure(new UnsupportedOperationException("Use the Http2[Inbound|Outbound]FlowController objects to control window sizes"));
    }

    @Override
    public ChannelFuture writeFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload, ChannelPromise promise) {
        return this.frameWriter.writeFrame(ctx, frameType, streamId, flags, payload, promise);
    }

    @Override
    public void close() {
        this.frameWriter.close();
    }

    @Override
    public Http2Settings pollSentSettings() {
        return this.outstandingLocalSettingsQueue.poll();
    }

    @Override
    public Http2FrameWriter.Configuration configuration() {
        return this.frameWriter.configuration();
    }

    private Http2Stream requireStream(int streamId) {
        Http2Stream stream = this.connection.stream(streamId);
        if (stream == null) {
            String message = this.connection.streamMayHaveExisted(streamId) ? "Stream no longer exists: " + streamId : "Stream does not exist: " + streamId;
            throw new IllegalArgumentException(message);
        }
        return stream;
    }

    public abstract class FlowControlledBase
    implements Http2RemoteFlowController.FlowControlled,
    ChannelFutureListener {
        protected final Http2Stream stream;
        protected ChannelPromise promise;
        protected boolean endOfStream;
        protected int padding;

        FlowControlledBase(Http2Stream stream, int padding, boolean endOfStream, ChannelPromise promise) {
            if (padding < 0) {
                throw new IllegalArgumentException("padding must be >= 0");
            }
            this.padding = padding;
            this.endOfStream = endOfStream;
            this.stream = stream;
            this.promise = promise;
        }

        @Override
        public void writeComplete() {
            if (this.endOfStream) {
                DefaultHttp2ConnectionEncoder.this.lifecycleManager.closeStreamLocal(this.stream, this.promise);
            }
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                this.error(DefaultHttp2ConnectionEncoder.this.flowController().channelHandlerContext(), future.cause());
            }
        }
    }

    private final class FlowControlledHeaders
    extends FlowControlledBase {
        private final Http2Headers headers;
        private final int streamDependency;
        private final short weight;
        private final boolean exclusive;

        FlowControlledHeaders(Http2Stream stream, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream, ChannelPromise promise) {
            super(stream, padding, endOfStream, promise);
            this.headers = headers;
            this.streamDependency = streamDependency;
            this.weight = weight;
            this.exclusive = exclusive;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void error(ChannelHandlerContext ctx, Throwable cause) {
            if (ctx != null) {
                DefaultHttp2ConnectionEncoder.this.lifecycleManager.onError(ctx, cause);
            }
            this.promise.tryFailure(cause);
        }

        @Override
        public void write(ChannelHandlerContext ctx, int allowedBytes) {
            boolean isInformational = DefaultHttp2ConnectionEncoder.validateHeadersSentState(this.stream, this.headers, DefaultHttp2ConnectionEncoder.this.connection.isServer(), this.endOfStream);
            if (this.promise.isVoid()) {
                this.promise = ctx.newPromise();
            }
            this.promise.addListener(this);
            ChannelFuture f = DefaultHttp2ConnectionEncoder.this.frameWriter.writeHeaders(ctx, this.stream.id(), this.headers, this.streamDependency, this.weight, this.exclusive, this.padding, this.endOfStream, this.promise);
            Throwable failureCause = f.cause();
            if (failureCause == null) {
                this.stream.headersSent(isInformational);
            } else {
                DefaultHttp2ConnectionEncoder.this.lifecycleManager.onError(ctx, failureCause);
            }
        }

        @Override
        public boolean merge(ChannelHandlerContext ctx, Http2RemoteFlowController.FlowControlled next) {
            return false;
        }
    }

    private final class FlowControlledData
    extends FlowControlledBase {
        private final CoalescingBufferQueue queue;
        private int dataSize;

        FlowControlledData(Http2Stream stream, ByteBuf buf, int padding, boolean endOfStream, ChannelPromise promise) {
            super(stream, padding, endOfStream, promise);
            this.queue = new CoalescingBufferQueue(promise.channel());
            this.queue.add(buf, promise);
            this.dataSize = this.queue.readableBytes();
        }

        @Override
        public int size() {
            return this.dataSize + this.padding;
        }

        @Override
        public void error(ChannelHandlerContext ctx, Throwable cause) {
            this.queue.releaseAndFailAll(cause);
            DefaultHttp2ConnectionEncoder.this.lifecycleManager.onError(ctx, cause);
        }

        @Override
        public void write(ChannelHandlerContext ctx, int allowedBytes) {
            int queuedData = this.queue.readableBytes();
            if (!this.endOfStream) {
                if (queuedData == 0) {
                    ChannelPromise writePromise = ctx.newPromise().addListener(this);
                    ctx.write(this.queue.remove(0, writePromise), writePromise);
                    return;
                }
                if (allowedBytes == 0) {
                    return;
                }
            }
            int writableData = Math.min(queuedData, allowedBytes);
            ChannelPromise writePromise = ctx.newPromise().addListener(this);
            ByteBuf toWrite = this.queue.remove(writableData, writePromise);
            this.dataSize = this.queue.readableBytes();
            int writablePadding = Math.min(allowedBytes - writableData, this.padding);
            this.padding -= writablePadding;
            DefaultHttp2ConnectionEncoder.this.frameWriter().writeData(ctx, this.stream.id(), toWrite, writablePadding, this.endOfStream && this.size() == 0, writePromise);
        }

        @Override
        public boolean merge(ChannelHandlerContext ctx, Http2RemoteFlowController.FlowControlled next) {
            FlowControlledData nextData;
            if (FlowControlledData.class != next.getClass() || Integer.MAX_VALUE - (nextData = (FlowControlledData)next).size() < this.size()) {
                return false;
            }
            nextData.queue.copyTo(this.queue);
            this.dataSize = this.queue.readableBytes();
            this.padding = Math.max(this.padding, nextData.padding);
            this.endOfStream = nextData.endOfStream;
            return true;
        }
    }

}

