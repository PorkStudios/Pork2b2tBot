/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.DelegatingChannelPromiseNotifier;
import io.netty.channel.EventLoop;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.VoidChannelPromise;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.codec.http2.DefaultHttp2ResetFrame;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2FrameStreamException;
import io.netty.handler.codec.http2.Http2FrameStreamVisitor;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelId;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class Http2MultiplexCodec
extends Http2FrameCodec {
    private static final ChannelFutureListener CHILD_CHANNEL_REGISTRATION_LISTENER = new ChannelFutureListener(){

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            Http2MultiplexCodec.registerDone(future);
        }
    };
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    private static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), DefaultHttp2StreamChannel.Http2ChannelUnsafe.class, "write(...)");
    private static final int MIN_HTTP2_FRAME_SIZE = 9;
    private final ChannelHandler inboundStreamHandler;
    private int initialOutboundStreamWindow = 65535;
    private boolean flushNeeded;
    private boolean parentReadInProgress;
    private int idCount;
    private DefaultHttp2StreamChannel head;
    private DefaultHttp2StreamChannel tail;
    volatile ChannelHandlerContext ctx;

    Http2MultiplexCodec(Http2ConnectionEncoder encoder, Http2ConnectionDecoder decoder, Http2Settings initialSettings, ChannelHandler inboundStreamHandler) {
        super(encoder, decoder, initialSettings);
        this.inboundStreamHandler = inboundStreamHandler;
    }

    private static void registerDone(ChannelFuture future) {
        if (!future.isSuccess()) {
            Channel childChannel = future.channel();
            if (childChannel.isRegistered()) {
                childChannel.close();
            } else {
                childChannel.unsafe().closeForcibly();
            }
        }
    }

    @Override
    public final void handlerAdded0(ChannelHandlerContext ctx) throws Exception {
        if (ctx.executor() != ctx.channel().eventLoop()) {
            throw new IllegalStateException("EventExecutor must be EventLoop of Channel");
        }
        this.ctx = ctx;
    }

    @Override
    public final void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved0(ctx);
        DefaultHttp2StreamChannel ch = this.head;
        while (ch != null) {
            DefaultHttp2StreamChannel curr = ch;
            ch = curr.next;
            curr.next = null;
        }
        this.tail = null;
        this.head = null;
    }

    @Override
    Http2MultiplexCodecStream newStream() {
        return new Http2MultiplexCodecStream();
    }

    @Override
    final void onHttp2Frame(ChannelHandlerContext ctx, Http2Frame frame) {
        Http2Settings settings;
        if (frame instanceof Http2StreamFrame) {
            Http2StreamFrame streamFrame = (Http2StreamFrame)frame;
            this.onHttp2StreamFrame(((Http2MultiplexCodecStream)streamFrame.stream()).channel, streamFrame);
        } else if (frame instanceof Http2GoAwayFrame) {
            this.onHttp2GoAwayFrame(ctx, (Http2GoAwayFrame)frame);
        } else if (frame instanceof Http2SettingsFrame && (settings = ((Http2SettingsFrame)frame).settings()).initialWindowSize() != null) {
            this.initialOutboundStreamWindow = settings.initialWindowSize();
        }
    }

    @Override
    final void onHttp2StreamStateChanged(ChannelHandlerContext ctx, Http2FrameStream stream) {
        Http2MultiplexCodecStream s = (Http2MultiplexCodecStream)stream;
        switch (stream.state()) {
            case HALF_CLOSED_REMOTE: 
            case OPEN: {
                if (s.channel != null) break;
                ChannelFuture future = ctx.channel().eventLoop().register(new DefaultHttp2StreamChannel(s, false));
                if (future.isDone()) {
                    Http2MultiplexCodec.registerDone(future);
                    break;
                }
                future.addListener(CHILD_CHANNEL_REGISTRATION_LISTENER);
                break;
            }
            case CLOSED: {
                DefaultHttp2StreamChannel channel = s.channel;
                if (channel == null) break;
                channel.streamClosed();
                break;
            }
        }
    }

    @Override
    final void onHttp2StreamWritabilityChanged(ChannelHandlerContext ctx, Http2FrameStream stream, boolean writable) {
        ((Http2MultiplexCodecStream)stream).channel.writabilityChanged(writable);
    }

    final Http2StreamChannel newOutboundStream() {
        return new DefaultHttp2StreamChannel(this.newStream(), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    final void onHttp2FrameStreamException(ChannelHandlerContext ctx, Http2FrameStreamException cause) {
        Http2FrameStream stream = cause.stream();
        DefaultHttp2StreamChannel childChannel = ((Http2MultiplexCodecStream)stream).channel;
        try {
            childChannel.pipeline().fireExceptionCaught(cause.getCause());
        }
        finally {
            childChannel.unsafe().closeForcibly();
        }
    }

    private void onHttp2StreamFrame(DefaultHttp2StreamChannel childChannel, Http2StreamFrame frame) {
        switch (childChannel.fireChildRead(frame)) {
            case READ_PROCESSED_BUT_STOP_READING: {
                childChannel.fireChildReadComplete();
                break;
            }
            case READ_PROCESSED_OK_TO_PROCESS_MORE: {
                this.addChildChannelToReadPendingQueue(childChannel);
                break;
            }
            case READ_IGNORED_CHANNEL_INACTIVE: 
            case READ_QUEUED: {
                break;
            }
            default: {
                throw new Error();
            }
        }
    }

    final void addChildChannelToReadPendingQueue(DefaultHttp2StreamChannel childChannel) {
        if (!childChannel.fireChannelReadPending) {
            assert (childChannel.next == null);
            if (this.tail == null) {
                assert (this.head == null);
                this.tail = this.head = childChannel;
            } else {
                this.tail.next = childChannel;
                this.tail = childChannel;
            }
            childChannel.fireChannelReadPending = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onHttp2GoAwayFrame(ChannelHandlerContext ctx, final Http2GoAwayFrame goAwayFrame) {
        try {
            this.forEachActiveStream(new Http2FrameStreamVisitor(){

                @Override
                public boolean visit(Http2FrameStream stream) {
                    int streamId = stream.id();
                    DefaultHttp2StreamChannel childChannel = ((Http2MultiplexCodecStream)stream).channel;
                    if (streamId > goAwayFrame.lastStreamId() && Http2MultiplexCodec.this.connection().local().isValidStreamId(streamId)) {
                        childChannel.pipeline().fireUserEventTriggered(goAwayFrame.retainedDuplicate());
                    }
                    return true;
                }
            });
        }
        catch (Http2Exception e) {
            ctx.fireExceptionCaught(e);
            ctx.close();
        }
        finally {
            goAwayFrame.release();
        }
    }

    @Override
    public final void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.parentReadInProgress = false;
        this.onChannelReadComplete(ctx);
        this.channelReadComplete0(ctx);
    }

    @Override
    public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.parentReadInProgress = true;
        super.channelRead(ctx, msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void onChannelReadComplete(ChannelHandlerContext ctx) {
        try {
            DefaultHttp2StreamChannel current = this.head;
            while (current != null) {
                DefaultHttp2StreamChannel childChannel = current;
                if (childChannel.fireChannelReadPending) {
                    childChannel.fireChannelReadPending = false;
                    childChannel.fireChildReadComplete();
                }
                childChannel.next = null;
                current = current.next;
            }
        }
        finally {
            this.head = null;
            this.tail = null;
            this.flush0(ctx);
        }
    }

    @Override
    public final void flush(ChannelHandlerContext ctx) {
        this.flushNeeded = false;
        super.flush(ctx);
    }

    void flush0(ChannelHandlerContext ctx) {
        this.flush(ctx);
    }

    boolean onBytesConsumed(ChannelHandlerContext ctx, Http2FrameStream stream, int bytes) throws Http2Exception {
        return this.consumeBytes(stream.id(), bytes);
    }

    private boolean initialWritability(Http2FrameCodec.DefaultHttp2FrameStream stream) {
        return !Http2CodecUtil.isStreamIdValid(stream.id()) || this.isWritable(stream);
    }

    private final class DefaultHttp2StreamChannel
    extends DefaultAttributeMap
    implements Http2StreamChannel {
        private final Http2StreamChannelConfig config = new Http2StreamChannelConfig(this);
        private final Http2ChannelUnsafe unsafe = new Http2ChannelUnsafe();
        private final ChannelId channelId;
        private final ChannelPipeline pipeline;
        private final Http2FrameCodec.DefaultHttp2FrameStream stream;
        private final ChannelPromise closePromise;
        private final boolean outbound;
        private volatile boolean registered;
        private volatile boolean writable;
        private boolean closePending;
        private boolean readInProgress;
        private Queue<Object> inboundBuffer;
        private boolean firstFrameWritten;
        private boolean streamClosedWithoutError;
        private boolean inFireChannelReadComplete;
        private boolean flushPending;
        boolean fireChannelReadPending;
        DefaultHttp2StreamChannel next;

        DefaultHttp2StreamChannel(Http2FrameCodec.DefaultHttp2FrameStream stream, boolean outbound) {
            this.stream = stream;
            this.outbound = outbound;
            this.writable = Http2MultiplexCodec.this.initialWritability(stream);
            ((Http2MultiplexCodecStream)stream).channel = this;
            this.pipeline = new DefaultChannelPipeline(this, Http2MultiplexCodec.this){
                final /* synthetic */ Http2MultiplexCodec val$this$0;
                {
                    this.val$this$0 = http2MultiplexCodec;
                    super(x0);
                }

                @Override
                protected void incrementPendingOutboundBytes(long size) {
                }

                @Override
                protected void decrementPendingOutboundBytes(long size) {
                }
            };
            this.closePromise = this.pipeline.newPromise();
            this.channelId = new Http2StreamChannelId(this.parent().id(), ++Http2MultiplexCodec.this.idCount);
        }

        @Override
        public Http2FrameStream stream() {
            return this.stream;
        }

        void streamClosed() {
            this.streamClosedWithoutError = true;
            if (this.readInProgress) {
                this.unsafe().closeForcibly();
            } else {
                this.closePending = true;
            }
        }

        @Override
        public ChannelMetadata metadata() {
            return METADATA;
        }

        @Override
        public ChannelConfig config() {
            return this.config;
        }

        @Override
        public boolean isOpen() {
            return !this.closePromise.isDone();
        }

        @Override
        public boolean isActive() {
            return this.isOpen();
        }

        @Override
        public boolean isWritable() {
            return this.writable;
        }

        @Override
        public ChannelId id() {
            return this.channelId;
        }

        @Override
        public EventLoop eventLoop() {
            return this.parent().eventLoop();
        }

        @Override
        public Channel parent() {
            return Http2MultiplexCodec.this.ctx.channel();
        }

        @Override
        public boolean isRegistered() {
            return this.registered;
        }

        @Override
        public SocketAddress localAddress() {
            return this.parent().localAddress();
        }

        @Override
        public SocketAddress remoteAddress() {
            return this.parent().remoteAddress();
        }

        @Override
        public ChannelFuture closeFuture() {
            return this.closePromise;
        }

        @Override
        public long bytesBeforeUnwritable() {
            return this.config().getWriteBufferHighWaterMark();
        }

        @Override
        public long bytesBeforeWritable() {
            return 0L;
        }

        @Override
        public Channel.Unsafe unsafe() {
            return this.unsafe;
        }

        @Override
        public ChannelPipeline pipeline() {
            return this.pipeline;
        }

        @Override
        public ByteBufAllocator alloc() {
            return this.config().getAllocator();
        }

        @Override
        public Channel read() {
            this.pipeline().read();
            return this;
        }

        @Override
        public Channel flush() {
            this.pipeline().flush();
            return this;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {
            return this.pipeline().bind(localAddress);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {
            return this.pipeline().connect(remoteAddress);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return this.pipeline().connect(remoteAddress, localAddress);
        }

        @Override
        public ChannelFuture disconnect() {
            return this.pipeline().disconnect();
        }

        @Override
        public ChannelFuture close() {
            return this.pipeline().close();
        }

        @Override
        public ChannelFuture deregister() {
            return this.pipeline().deregister();
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return this.pipeline().bind(localAddress, promise);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            return this.pipeline().connect(remoteAddress, promise);
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            return this.pipeline().connect(remoteAddress, localAddress, promise);
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            return this.pipeline().disconnect(promise);
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            return this.pipeline().close(promise);
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            return this.pipeline().deregister(promise);
        }

        @Override
        public ChannelFuture write(Object msg) {
            return this.pipeline().write(msg);
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return this.pipeline().write(msg, promise);
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            return this.pipeline().writeAndFlush(msg, promise);
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            return this.pipeline().writeAndFlush(msg);
        }

        @Override
        public ChannelPromise newPromise() {
            return this.pipeline().newPromise();
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return this.pipeline().newProgressivePromise();
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return this.pipeline().newSucceededFuture();
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            return this.pipeline().newFailedFuture(cause);
        }

        @Override
        public ChannelPromise voidPromise() {
            return this.pipeline().voidPromise();
        }

        public int hashCode() {
            return this.id().hashCode();
        }

        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int compareTo(Channel o) {
            if (this == o) {
                return 0;
            }
            return this.id().compareTo(o.id());
        }

        public String toString() {
            return this.parent().toString() + "(H2 - " + this.stream + ')';
        }

        void writabilityChanged(boolean writable) {
            assert (this.eventLoop().inEventLoop());
            if (writable != this.writable && this.isActive()) {
                this.writable = writable;
                this.pipeline().fireChannelWritabilityChanged();
            }
        }

        ReadState fireChildRead(Http2Frame frame) {
            assert (this.eventLoop().inEventLoop());
            if (!this.isActive()) {
                ReferenceCountUtil.release(frame);
                return ReadState.READ_IGNORED_CHANNEL_INACTIVE;
            }
            if (this.readInProgress && (this.inboundBuffer == null || this.inboundBuffer.isEmpty())) {
                RecvByteBufAllocator.ExtendedHandle allocHandle = this.unsafe.recvBufAllocHandle();
                this.unsafe.doRead0(frame, allocHandle);
                return allocHandle.continueReading() ? ReadState.READ_PROCESSED_OK_TO_PROCESS_MORE : ReadState.READ_PROCESSED_BUT_STOP_READING;
            }
            if (this.inboundBuffer == null) {
                this.inboundBuffer = new ArrayDeque<Object>(4);
            }
            this.inboundBuffer.add(frame);
            return ReadState.READ_QUEUED;
        }

        void fireChildReadComplete() {
            assert (this.eventLoop().inEventLoop());
            try {
                if (this.readInProgress) {
                    this.inFireChannelReadComplete = true;
                    this.readInProgress = false;
                    this.unsafe().recvBufAllocHandle().readComplete();
                    this.pipeline().fireChannelReadComplete();
                }
                Http2MultiplexCodec.this.flushNeeded = Http2MultiplexCodec.this.flushNeeded | this.flushPending;
            }
            finally {
                this.inFireChannelReadComplete = false;
                this.flushPending = false;
            }
        }

        private final class Http2StreamChannelConfig
        extends DefaultChannelConfig {
            Http2StreamChannelConfig(Channel channel) {
                super(channel);
                this.setRecvByteBufAllocator(new Http2StreamChannelRecvByteBufAllocator());
            }

            @Override
            public int getWriteBufferHighWaterMark() {
                return Math.min(DefaultHttp2StreamChannel.this.parent().config().getWriteBufferHighWaterMark(), Http2MultiplexCodec.this.initialOutboundStreamWindow);
            }

            @Override
            public int getWriteBufferLowWaterMark() {
                return Math.min(DefaultHttp2StreamChannel.this.parent().config().getWriteBufferLowWaterMark(), Http2MultiplexCodec.this.initialOutboundStreamWindow);
            }

            @Override
            public MessageSizeEstimator getMessageSizeEstimator() {
                return FlowControlledFrameSizeEstimator.INSTANCE;
            }

            @Override
            public WriteBufferWaterMark getWriteBufferWaterMark() {
                int mark = this.getWriteBufferHighWaterMark();
                return new WriteBufferWaterMark(mark, mark);
            }

            @Override
            public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
                throw new UnsupportedOperationException();
            }

            @Deprecated
            @Override
            public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
                throw new UnsupportedOperationException();
            }

            @Deprecated
            @Override
            public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
                if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
                    throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
                }
                super.setRecvByteBufAllocator(allocator);
                return this;
            }
        }

        private final class Http2ChannelUnsafe
        implements Channel.Unsafe {
            private final VoidChannelPromise unsafeVoidPromise;
            private RecvByteBufAllocator.ExtendedHandle recvHandle;
            private boolean writeDoneAndNoFlush;
            private ChannelPromise pendingClosePromise;

            private Http2ChannelUnsafe() {
                this.unsafeVoidPromise = new VoidChannelPromise(DefaultHttp2StreamChannel.this, false);
            }

            @Override
            public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
                if (!promise.setUncancellable()) {
                    return;
                }
                promise.setFailure(new UnsupportedOperationException());
            }

            @Override
            public RecvByteBufAllocator.ExtendedHandle recvBufAllocHandle() {
                if (this.recvHandle == null) {
                    this.recvHandle = (RecvByteBufAllocator.ExtendedHandle)DefaultHttp2StreamChannel.this.config().getRecvByteBufAllocator().newHandle();
                }
                return this.recvHandle;
            }

            @Override
            public SocketAddress localAddress() {
                return DefaultHttp2StreamChannel.this.parent().unsafe().localAddress();
            }

            @Override
            public SocketAddress remoteAddress() {
                return DefaultHttp2StreamChannel.this.parent().unsafe().remoteAddress();
            }

            @Override
            public void register(EventLoop eventLoop, ChannelPromise promise) {
                if (!promise.setUncancellable()) {
                    return;
                }
                if (DefaultHttp2StreamChannel.this.registered) {
                    throw new UnsupportedOperationException("Re-register is not supported");
                }
                DefaultHttp2StreamChannel.this.registered = true;
                if (!DefaultHttp2StreamChannel.this.outbound) {
                    DefaultHttp2StreamChannel.this.pipeline().addLast(Http2MultiplexCodec.this.inboundStreamHandler);
                }
                promise.setSuccess();
                DefaultHttp2StreamChannel.this.pipeline().fireChannelRegistered();
                if (DefaultHttp2StreamChannel.this.isActive()) {
                    DefaultHttp2StreamChannel.this.pipeline().fireChannelActive();
                }
            }

            @Override
            public void bind(SocketAddress localAddress, ChannelPromise promise) {
                if (!promise.setUncancellable()) {
                    return;
                }
                promise.setFailure(new UnsupportedOperationException());
            }

            @Override
            public void disconnect(ChannelPromise promise) {
                if (!promise.setUncancellable()) {
                    return;
                }
                this.close(promise);
            }

            @Override
            public void close(ChannelPromise promise) {
                if (!promise.setUncancellable()) {
                    return;
                }
                if (DefaultHttp2StreamChannel.this.closePromise.isDone()) {
                    promise.setFailure(new ClosedChannelException());
                    return;
                }
                if (this.pendingClosePromise != null) {
                    this.pendingClosePromise.addListener(new DelegatingChannelPromiseNotifier(promise));
                    return;
                }
                this.pendingClosePromise = promise;
                try {
                    DefaultHttp2StreamChannel.this.closePending = false;
                    DefaultHttp2StreamChannel.this.fireChannelReadPending = false;
                    if (DefaultHttp2StreamChannel.this.parent().isActive() && !DefaultHttp2StreamChannel.this.streamClosedWithoutError && Http2CodecUtil.isStreamIdValid(DefaultHttp2StreamChannel.this.stream().id())) {
                        DefaultHttp2ResetFrame resetFrame = new DefaultHttp2ResetFrame(Http2Error.CANCEL).stream(DefaultHttp2StreamChannel.this.stream());
                        this.write(resetFrame, DefaultHttp2StreamChannel.this.unsafe().voidPromise());
                        this.flush();
                    }
                    if (DefaultHttp2StreamChannel.this.inboundBuffer != null) {
                        Object msg;
                        while ((msg = DefaultHttp2StreamChannel.this.inboundBuffer.poll()) != null) {
                            ReferenceCountUtil.release(msg);
                        }
                    }
                    DefaultHttp2StreamChannel.this.pipeline().fireChannelInactive();
                    if (DefaultHttp2StreamChannel.this.isRegistered()) {
                        this.deregister(DefaultHttp2StreamChannel.this.unsafe().voidPromise());
                    }
                    promise.setSuccess();
                    DefaultHttp2StreamChannel.this.closePromise.setSuccess();
                }
                finally {
                    this.pendingClosePromise = null;
                }
            }

            @Override
            public void closeForcibly() {
                this.close(DefaultHttp2StreamChannel.this.unsafe().voidPromise());
            }

            @Override
            public void deregister(ChannelPromise promise) {
                if (!promise.setUncancellable()) {
                    return;
                }
                if (DefaultHttp2StreamChannel.this.registered) {
                    DefaultHttp2StreamChannel.this.registered = true;
                    promise.setSuccess();
                    DefaultHttp2StreamChannel.this.pipeline().fireChannelUnregistered();
                } else {
                    promise.setFailure(new IllegalStateException("Not registered"));
                }
            }

            @Override
            public void beginRead() {
                boolean continueReading;
                if (DefaultHttp2StreamChannel.this.readInProgress || !DefaultHttp2StreamChannel.this.isActive()) {
                    return;
                }
                DefaultHttp2StreamChannel.this.readInProgress = true;
                RecvByteBufAllocator.Handle allocHandle = DefaultHttp2StreamChannel.this.unsafe().recvBufAllocHandle();
                allocHandle.reset(DefaultHttp2StreamChannel.this.config());
                if (DefaultHttp2StreamChannel.this.inboundBuffer == null || DefaultHttp2StreamChannel.this.inboundBuffer.isEmpty()) {
                    if (DefaultHttp2StreamChannel.this.closePending) {
                        DefaultHttp2StreamChannel.this.unsafe.closeForcibly();
                    }
                    return;
                }
                do {
                    Object m;
                    if ((m = DefaultHttp2StreamChannel.this.inboundBuffer.poll()) == null) {
                        continueReading = false;
                        break;
                    }
                    this.doRead0((Http2Frame)m, allocHandle);
                } while (continueReading = allocHandle.continueReading());
                if (continueReading && Http2MultiplexCodec.this.parentReadInProgress) {
                    Http2MultiplexCodec.this.addChildChannelToReadPendingQueue(DefaultHttp2StreamChannel.this);
                } else {
                    DefaultHttp2StreamChannel.this.readInProgress = false;
                    allocHandle.readComplete();
                    DefaultHttp2StreamChannel.this.pipeline().fireChannelReadComplete();
                    this.flush();
                    if (DefaultHttp2StreamChannel.this.closePending) {
                        DefaultHttp2StreamChannel.this.unsafe.closeForcibly();
                    }
                }
            }

            void doRead0(Http2Frame frame, RecvByteBufAllocator.Handle allocHandle) {
                int numBytesToBeConsumed = 0;
                if (frame instanceof Http2DataFrame) {
                    numBytesToBeConsumed = ((Http2DataFrame)frame).initialFlowControlledBytes();
                    allocHandle.lastBytesRead(numBytesToBeConsumed);
                } else {
                    allocHandle.lastBytesRead(9);
                }
                allocHandle.incMessagesRead(1);
                DefaultHttp2StreamChannel.this.pipeline().fireChannelRead(frame);
                if (numBytesToBeConsumed != 0) {
                    try {
                        this.writeDoneAndNoFlush |= Http2MultiplexCodec.this.onBytesConsumed(Http2MultiplexCodec.this.ctx, DefaultHttp2StreamChannel.this.stream, numBytesToBeConsumed);
                    }
                    catch (Http2Exception e) {
                        DefaultHttp2StreamChannel.this.pipeline().fireExceptionCaught(e);
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void write(Object msg, final ChannelPromise promise) {
                if (!promise.setUncancellable()) {
                    ReferenceCountUtil.release(msg);
                    return;
                }
                if (!DefaultHttp2StreamChannel.this.isActive()) {
                    ReferenceCountUtil.release(msg);
                    promise.setFailure(CLOSED_CHANNEL_EXCEPTION);
                    return;
                }
                try {
                    if (msg instanceof Http2StreamFrame) {
                        Http2StreamFrame frame = this.validateStreamFrame((Http2StreamFrame)msg).stream(DefaultHttp2StreamChannel.this.stream());
                        if (!DefaultHttp2StreamChannel.this.firstFrameWritten && !Http2CodecUtil.isStreamIdValid(DefaultHttp2StreamChannel.this.stream().id())) {
                            if (!(frame instanceof Http2HeadersFrame)) {
                                ReferenceCountUtil.release(frame);
                                promise.setFailure(new IllegalArgumentException("The first frame must be a headers frame. Was: " + frame.name()));
                                return;
                            }
                            DefaultHttp2StreamChannel.this.firstFrameWritten = true;
                            ChannelFuture future = this.write0(frame);
                            if (future.isDone()) {
                                this.firstWriteComplete(future, promise);
                            } else {
                                future.addListener(new ChannelFutureListener(){

                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {
                                        Http2ChannelUnsafe.this.firstWriteComplete(future, promise);
                                    }
                                });
                            }
                            return;
                        }
                    } else {
                        String msgStr = msg.toString();
                        ReferenceCountUtil.release(msg);
                        promise.setFailure(new IllegalArgumentException("Message must be an " + StringUtil.simpleClassName(Http2StreamFrame.class) + ": " + msgStr));
                        return;
                    }
                    ChannelFuture future = this.write0(msg);
                    if (future.isDone()) {
                        this.writeComplete(future, promise);
                    } else {
                        future.addListener(new ChannelFutureListener(){

                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                Http2ChannelUnsafe.this.writeComplete(future, promise);
                            }
                        });
                    }
                }
                catch (Throwable t) {
                    promise.tryFailure(t);
                }
                finally {
                    this.writeDoneAndNoFlush = true;
                }
            }

            private void firstWriteComplete(ChannelFuture future, ChannelPromise promise) {
                Throwable cause = future.cause();
                if (cause == null) {
                    DefaultHttp2StreamChannel.this.writabilityChanged(Http2MultiplexCodec.this.isWritable(DefaultHttp2StreamChannel.this.stream));
                    promise.setSuccess();
                } else {
                    promise.setFailure(cause);
                    this.closeForcibly();
                }
            }

            private void writeComplete(ChannelFuture future, ChannelPromise promise) {
                Throwable cause = future.cause();
                if (cause == null) {
                    promise.setSuccess();
                } else {
                    promise.setFailure(cause);
                }
            }

            private Http2StreamFrame validateStreamFrame(Http2StreamFrame frame) {
                if (frame.stream() != null && frame.stream() != DefaultHttp2StreamChannel.this.stream) {
                    String msgString = frame.toString();
                    ReferenceCountUtil.release(frame);
                    throw new IllegalArgumentException("Stream " + frame.stream() + " must not be set on the frame: " + msgString);
                }
                return frame;
            }

            private ChannelFuture write0(Object msg) {
                ChannelPromise promise = Http2MultiplexCodec.this.ctx.newPromise();
                Http2MultiplexCodec.this.write(Http2MultiplexCodec.this.ctx, msg, promise);
                return promise;
            }

            @Override
            public void flush() {
                if (!this.writeDoneAndNoFlush) {
                    return;
                }
                try {
                    if (DefaultHttp2StreamChannel.this.inFireChannelReadComplete) {
                        DefaultHttp2StreamChannel.this.flushPending = true;
                    } else {
                        Http2MultiplexCodec.this.flush0(Http2MultiplexCodec.this.ctx);
                    }
                }
                finally {
                    this.writeDoneAndNoFlush = false;
                }
            }

            @Override
            public ChannelPromise voidPromise() {
                return this.unsafeVoidPromise;
            }

            @Override
            public ChannelOutboundBuffer outboundBuffer() {
                return null;
            }

        }

    }

    private static enum ReadState {
        READ_QUEUED,
        READ_IGNORED_CHANNEL_INACTIVE,
        READ_PROCESSED_BUT_STOP_READING,
        READ_PROCESSED_OK_TO_PROCESS_MORE;
        

        private ReadState() {
        }
    }

    static class Http2MultiplexCodecStream
    extends Http2FrameCodec.DefaultHttp2FrameStream {
        DefaultHttp2StreamChannel channel;

        Http2MultiplexCodecStream() {
        }
    }

    private static final class Http2StreamChannelRecvByteBufAllocator
    extends DefaultMaxMessagesRecvByteBufAllocator {
        private Http2StreamChannelRecvByteBufAllocator() {
        }

        @Override
        public DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle newHandle() {
            return new DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle(){

                @Override
                public int guess() {
                    return 1024;
                }
            };
        }

    }

    private static final class FlowControlledFrameSizeEstimator
    implements MessageSizeEstimator {
        static final FlowControlledFrameSizeEstimator INSTANCE = new FlowControlledFrameSizeEstimator();
        static final MessageSizeEstimator.Handle HANDLE_INSTANCE = new MessageSizeEstimator.Handle(){

            @Override
            public int size(Object msg) {
                return msg instanceof Http2DataFrame ? (int)Math.min(Integer.MAX_VALUE, (long)((Http2DataFrame)msg).initialFlowControlledBytes() + 9L) : 9;
            }
        };

        private FlowControlledFrameSizeEstimator() {
        }

        @Override
        public MessageSizeEstimator.Handle newHandle() {
            return HANDLE_INSTANCE;
        }

    }

}

