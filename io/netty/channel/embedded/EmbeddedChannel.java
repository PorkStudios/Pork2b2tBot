/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.embedded;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannelId;
import io.netty.channel.embedded.EmbeddedEventLoop;
import io.netty.channel.embedded.EmbeddedSocketAddress;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class EmbeddedChannel
extends AbstractChannel {
    private static final SocketAddress LOCAL_ADDRESS = new EmbeddedSocketAddress();
    private static final SocketAddress REMOTE_ADDRESS = new EmbeddedSocketAddress();
    private static final ChannelHandler[] EMPTY_HANDLERS = new ChannelHandler[0];
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
    private static final ChannelMetadata METADATA_NO_DISCONNECT = new ChannelMetadata(false);
    private static final ChannelMetadata METADATA_DISCONNECT = new ChannelMetadata(true);
    private final EmbeddedEventLoop loop = new EmbeddedEventLoop();
    private final ChannelFutureListener recordExceptionListener = new ChannelFutureListener(){

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            EmbeddedChannel.this.recordException(future);
        }
    };
    private final ChannelMetadata metadata;
    private final ChannelConfig config;
    private Queue<Object> inboundMessages;
    private Queue<Object> outboundMessages;
    private Throwable lastException;
    private State state;

    public EmbeddedChannel() {
        this(EMPTY_HANDLERS);
    }

    public EmbeddedChannel(ChannelId channelId) {
        this(channelId, EMPTY_HANDLERS);
    }

    public /* varargs */ EmbeddedChannel(ChannelHandler ... handlers) {
        this(EmbeddedChannelId.INSTANCE, handlers);
    }

    public /* varargs */ EmbeddedChannel(boolean hasDisconnect, ChannelHandler ... handlers) {
        this(EmbeddedChannelId.INSTANCE, hasDisconnect, handlers);
    }

    public /* varargs */ EmbeddedChannel(boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        this(EmbeddedChannelId.INSTANCE, register, hasDisconnect, handlers);
    }

    public /* varargs */ EmbeddedChannel(ChannelId channelId, ChannelHandler ... handlers) {
        this(channelId, false, handlers);
    }

    public /* varargs */ EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelHandler ... handlers) {
        this(channelId, true, hasDisconnect, handlers);
    }

    public /* varargs */ EmbeddedChannel(ChannelId channelId, boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        super(null, channelId);
        this.metadata = EmbeddedChannel.metadata(hasDisconnect);
        this.config = new DefaultChannelConfig(this);
        this.setup(register, handlers);
    }

    public /* varargs */ EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelConfig config, ChannelHandler ... handlers) {
        super(null, channelId);
        this.metadata = EmbeddedChannel.metadata(hasDisconnect);
        this.config = ObjectUtil.checkNotNull(config, "config");
        this.setup(true, handlers);
    }

    private static ChannelMetadata metadata(boolean hasDisconnect) {
        return hasDisconnect ? METADATA_DISCONNECT : METADATA_NO_DISCONNECT;
    }

    private /* varargs */ void setup(boolean register, final ChannelHandler ... handlers) {
        ObjectUtil.checkNotNull(handlers, "handlers");
        ChannelPipeline p = this.pipeline();
        p.addLast(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                for (ChannelHandler h : handlers) {
                    if (h == null) break;
                    pipeline.addLast(h);
                }
            }
        });
        if (register) {
            ChannelFuture future = this.loop.register(this);
            assert (future.isDone());
        }
    }

    public void register() throws Exception {
        ChannelFuture future = this.loop.register(this);
        assert (future.isDone());
        Throwable cause = future.cause();
        if (cause != null) {
            PlatformDependent.throwException(cause);
        }
    }

    @Override
    protected final DefaultChannelPipeline newChannelPipeline() {
        return new EmbeddedChannelPipeline(this);
    }

    @Override
    public ChannelMetadata metadata() {
        return this.metadata;
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return this.state != State.CLOSED;
    }

    @Override
    public boolean isActive() {
        return this.state == State.ACTIVE;
    }

    public Queue<Object> inboundMessages() {
        if (this.inboundMessages == null) {
            this.inboundMessages = new ArrayDeque<Object>();
        }
        return this.inboundMessages;
    }

    @Deprecated
    public Queue<Object> lastInboundBuffer() {
        return this.inboundMessages();
    }

    public Queue<Object> outboundMessages() {
        if (this.outboundMessages == null) {
            this.outboundMessages = new ArrayDeque<Object>();
        }
        return this.outboundMessages;
    }

    @Deprecated
    public Queue<Object> lastOutboundBuffer() {
        return this.outboundMessages();
    }

    public <T> T readInbound() {
        return (T)EmbeddedChannel.poll(this.inboundMessages);
    }

    public <T> T readOutbound() {
        return (T)EmbeddedChannel.poll(this.outboundMessages);
    }

    public /* varargs */ boolean writeInbound(Object ... msgs) {
        this.ensureOpen();
        if (msgs.length == 0) {
            return EmbeddedChannel.isNotEmpty(this.inboundMessages);
        }
        ChannelPipeline p = this.pipeline();
        for (Object m : msgs) {
            p.fireChannelRead(m);
        }
        this.flushInbound(false, this.voidPromise());
        return EmbeddedChannel.isNotEmpty(this.inboundMessages);
    }

    public ChannelFuture writeOneInbound(Object msg) {
        return this.writeOneInbound(msg, this.newPromise());
    }

    public ChannelFuture writeOneInbound(Object msg, ChannelPromise promise) {
        if (this.checkOpen(true)) {
            this.pipeline().fireChannelRead(msg);
        }
        return this.checkException(promise);
    }

    public EmbeddedChannel flushInbound() {
        this.flushInbound(true, this.voidPromise());
        return this;
    }

    private ChannelFuture flushInbound(boolean recordException, ChannelPromise promise) {
        if (this.checkOpen(recordException)) {
            this.pipeline().fireChannelReadComplete();
            this.runPendingTasks();
        }
        return this.checkException(promise);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public /* varargs */ boolean writeOutbound(Object ... msgs) {
        this.ensureOpen();
        if (msgs.length == 0) {
            return EmbeddedChannel.isNotEmpty(this.outboundMessages);
        }
        RecyclableArrayList futures = RecyclableArrayList.newInstance(msgs.length);
        try {
            int i;
            for (Object m : msgs) {
                if (m == null) break;
                futures.add(this.write(m));
            }
            this.flushOutbound0();
            int size = futures.size();
            for (i = 0; i < size; ++i) {
                ChannelFuture future = (ChannelFuture)futures.get(i);
                if (future.isDone()) {
                    this.recordException(future);
                    continue;
                }
                future.addListener(this.recordExceptionListener);
            }
            this.checkException();
            i = EmbeddedChannel.isNotEmpty(this.outboundMessages) ? 1 : 0;
            return (boolean)i;
        }
        finally {
            futures.recycle();
        }
    }

    public ChannelFuture writeOneOutbound(Object msg) {
        return this.writeOneOutbound(msg, this.newPromise());
    }

    public ChannelFuture writeOneOutbound(Object msg, ChannelPromise promise) {
        if (this.checkOpen(true)) {
            return this.write(msg, promise);
        }
        return this.checkException(promise);
    }

    public EmbeddedChannel flushOutbound() {
        if (this.checkOpen(true)) {
            this.flushOutbound0();
        }
        this.checkException(this.voidPromise());
        return this;
    }

    private void flushOutbound0() {
        this.runPendingTasks();
        this.flush();
    }

    public boolean finish() {
        return this.finish(false);
    }

    public boolean finishAndReleaseAll() {
        return this.finish(true);
    }

    private boolean finish(boolean releaseAll) {
        this.close();
        try {
            this.checkException();
            boolean bl = EmbeddedChannel.isNotEmpty(this.inboundMessages) || EmbeddedChannel.isNotEmpty(this.outboundMessages);
            return bl;
        }
        finally {
            if (releaseAll) {
                EmbeddedChannel.releaseAll(this.inboundMessages);
                EmbeddedChannel.releaseAll(this.outboundMessages);
            }
        }
    }

    public boolean releaseInbound() {
        return EmbeddedChannel.releaseAll(this.inboundMessages);
    }

    public boolean releaseOutbound() {
        return EmbeddedChannel.releaseAll(this.outboundMessages);
    }

    private static boolean releaseAll(Queue<Object> queue) {
        if (EmbeddedChannel.isNotEmpty(queue)) {
            Object msg;
            while ((msg = queue.poll()) != null) {
                ReferenceCountUtil.release(msg);
            }
            return true;
        }
        return false;
    }

    private void finishPendingTasks(boolean cancel) {
        this.runPendingTasks();
        if (cancel) {
            this.loop.cancelScheduledTasks();
        }
    }

    @Override
    public final ChannelFuture close() {
        return this.close(this.newPromise());
    }

    @Override
    public final ChannelFuture disconnect() {
        return this.disconnect(this.newPromise());
    }

    @Override
    public final ChannelFuture close(ChannelPromise promise) {
        this.runPendingTasks();
        ChannelFuture future = super.close(promise);
        this.finishPendingTasks(true);
        return future;
    }

    @Override
    public final ChannelFuture disconnect(ChannelPromise promise) {
        ChannelFuture future = super.disconnect(promise);
        this.finishPendingTasks(!this.metadata.hasDisconnect());
        return future;
    }

    private static boolean isNotEmpty(Queue<Object> queue) {
        return queue != null && !queue.isEmpty();
    }

    private static Object poll(Queue<Object> queue) {
        return queue != null ? queue.poll() : null;
    }

    public void runPendingTasks() {
        try {
            this.loop.runTasks();
        }
        catch (Exception e) {
            this.recordException(e);
        }
        try {
            this.loop.runScheduledTasks();
        }
        catch (Exception e) {
            this.recordException(e);
        }
    }

    public long runScheduledPendingTasks() {
        try {
            return this.loop.runScheduledTasks();
        }
        catch (Exception e) {
            this.recordException(e);
            return this.loop.nextScheduledTask();
        }
    }

    private void recordException(ChannelFuture future) {
        if (!future.isSuccess()) {
            this.recordException(future.cause());
        }
    }

    private void recordException(Throwable cause) {
        if (this.lastException == null) {
            this.lastException = cause;
        } else {
            logger.warn("More than one exception was raised. Will report only the first one and log others.", cause);
        }
    }

    private ChannelFuture checkException(ChannelPromise promise) {
        Throwable t = this.lastException;
        if (t != null) {
            this.lastException = null;
            if (promise.isVoid()) {
                PlatformDependent.throwException(t);
            }
            return promise.setFailure(t);
        }
        return promise.setSuccess();
    }

    public void checkException() {
        this.checkException(this.voidPromise());
    }

    private boolean checkOpen(boolean recordException) {
        if (!this.isOpen()) {
            if (recordException) {
                this.recordException(new ClosedChannelException());
            }
            return false;
        }
        return true;
    }

    protected final void ensureOpen() {
        if (!this.checkOpen(true)) {
            this.checkException();
        }
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EmbeddedEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.isActive() ? LOCAL_ADDRESS : null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.isActive() ? REMOTE_ADDRESS : null;
    }

    @Override
    protected void doRegister() throws Exception {
        this.state = State.ACTIVE;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
    }

    @Override
    protected void doDisconnect() throws Exception {
        if (!this.metadata.hasDisconnect()) {
            this.doClose();
        }
    }

    @Override
    protected void doClose() throws Exception {
        this.state = State.CLOSED;
    }

    @Override
    protected void doBeginRead() throws Exception {
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new EmbeddedUnsafe();
    }

    @Override
    public Channel.Unsafe unsafe() {
        return ((EmbeddedUnsafe)super.unsafe()).wrapped;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        while ((msg = in.current()) != null) {
            ReferenceCountUtil.retain(msg);
            this.handleOutboundMessage(msg);
            in.remove();
        }
    }

    protected void handleOutboundMessage(Object msg) {
        this.outboundMessages().add(msg);
    }

    protected void handleInboundMessage(Object msg) {
        this.inboundMessages().add(msg);
    }

    private final class EmbeddedChannelPipeline
    extends DefaultChannelPipeline {
        EmbeddedChannelPipeline(EmbeddedChannel channel) {
            super(channel);
        }

        @Override
        protected void onUnhandledInboundException(Throwable cause) {
            EmbeddedChannel.this.recordException(cause);
        }

        @Override
        protected void onUnhandledInboundMessage(Object msg) {
            EmbeddedChannel.this.handleInboundMessage(msg);
        }
    }

    private final class EmbeddedUnsafe
    extends AbstractChannel.AbstractUnsafe {
        final Channel.Unsafe wrapped = new Channel.Unsafe(){

            @Override
            public RecvByteBufAllocator.Handle recvBufAllocHandle() {
                return EmbeddedUnsafe.this.recvBufAllocHandle();
            }

            @Override
            public SocketAddress localAddress() {
                return EmbeddedUnsafe.this.localAddress();
            }

            @Override
            public SocketAddress remoteAddress() {
                return EmbeddedUnsafe.this.remoteAddress();
            }

            @Override
            public void register(EventLoop eventLoop, ChannelPromise promise) {
                EmbeddedUnsafe.this.register(eventLoop, promise);
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void bind(SocketAddress localAddress, ChannelPromise promise) {
                EmbeddedUnsafe.this.bind(localAddress, promise);
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
                EmbeddedUnsafe.this.connect(remoteAddress, localAddress, promise);
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void disconnect(ChannelPromise promise) {
                EmbeddedUnsafe.this.disconnect(promise);
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void close(ChannelPromise promise) {
                EmbeddedUnsafe.this.close(promise);
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void closeForcibly() {
                EmbeddedUnsafe.this.closeForcibly();
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void deregister(ChannelPromise promise) {
                EmbeddedUnsafe.this.deregister(promise);
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void beginRead() {
                EmbeddedUnsafe.this.beginRead();
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void write(Object msg, ChannelPromise promise) {
                EmbeddedUnsafe.this.write(msg, promise);
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public void flush() {
                EmbeddedUnsafe.this.flush();
                EmbeddedChannel.this.runPendingTasks();
            }

            @Override
            public ChannelPromise voidPromise() {
                return EmbeddedUnsafe.this.voidPromise();
            }

            @Override
            public ChannelOutboundBuffer outboundBuffer() {
                return EmbeddedUnsafe.this.outboundBuffer();
            }
        };

        private EmbeddedUnsafe() {
        }

        @Override
        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            this.safeSetSuccess(promise);
        }

    }

    private static enum State {
        OPEN,
        ACTIVE,
        CLOSED;
        

        private State() {
        }
    }

}

